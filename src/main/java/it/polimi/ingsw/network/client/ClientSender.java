package it.polimi.ingsw.network.client;

import it.polimi.ingsw.controller.GameRuleEnum;
import it.polimi.ingsw.model.TeamEnum;
import it.polimi.ingsw.model.WizardEnum;
import it.polimi.ingsw.network.CommandEnum;
import it.polimi.ingsw.network.MessageBroker;
import it.polimi.ingsw.network.NetworkFieldEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is only tasked to send messages to the server, it doesn't check
 * their respective answers, which will be left to the Receiver
 * This class doesn't deal with the ping routine
 */
public class ClientSender {

    private OutputStream outputStream;
    private MessageBroker mainBroker;
    private AtomicBoolean connected;
    private int progressiveIdRequest;
    private int idUser;
    private AtomicBoolean isCommandScheduled;
    private InitialConnector initialConnector; //Used only to communicate network error to the receiver

    public ClientSender(InitialConnector initialConnector){
        this.initialConnector = initialConnector;
        progressiveIdRequest = 1;
    }

    public void initialize(OutputStream outputStream, MessageBroker mainBroker, AtomicBoolean connected, AtomicBoolean isCommandScheduled) {
        this.outputStream = outputStream;
        this.mainBroker = mainBroker;
        this.connected = connected;
        this.isCommandScheduled = isCommandScheduled;
    }

    /*
            METHODS THAT REFLECT THE INTERFACE
                                                 */

    /**
     * Sends the chosen nickname to the server to evaluate whether it's acceptable or not
     * @param nickname the nickname chosen by the user
     * @return true if the message was successfully sent, false if nothing was sent to the server
     */
    public boolean sendNickname(String nickname){

        if(!acquireSendingRights()) return false;
// This command exclusively won't require the lock to be acquired, to allow for the revving up of the
// sending / receiving system

        mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.CONNECTION_REQUEST);
        mainBroker.addToMessage(NetworkFieldEnum.NICKNAME, nickname);

        sendToServer();
        return true;
    }

    /**
     * Sends the gamerule preferences to the server
     * @param gamemode the gamemode chosen, "1" for Simple, "2" for Advanced
     * @param numPlayers the number of players chosen
     * @return true if the message was successfully sent, false if nothing was sent to the server
     */
    public boolean sendGameModePreference(int gamemode, int numPlayers){

        GameRuleEnum rule;
        //Converts the input to a gamerule
        if(gamemode == 1){
            rule = GameRuleEnum.getBasicRule(numPlayers);
        }
        else rule = GameRuleEnum.getAdvancedRule(numPlayers);

        if(!acquireSendingRights()) return false;

        mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.PLAY_GAME);
        mainBroker.addToMessage(NetworkFieldEnum.GAME_RULE, rule);

        sendToServer();

        return true;
    }

    /**
     * Updates the server on the readiness of the user
     * @param ready true if the client is signaling that they're ready
     * @return true if the message was successfully sent, false if nothing was sent to the server
     */
    public boolean sendReadyStatus(boolean ready){

        if(!acquireSendingRights()) return false;

        if (ready) {
            mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.READY_TO_START);
        } else {
            mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.NOT_READY);
        }
        sendToServer();
        return true;
    }

    /**
     * Tries to start the game from the lobby
     * @return true if the message was successfully sent, false if nothing was sent to the server
     */
    public boolean startGame(){

        if(!acquireSendingRights()) return false;

        mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.START_GAME);
        sendToServer();
        return true;
    }

    /**
     * The user asks to leave the current lobby
     * @return true if the message was successfully sent, false if nothing was sent to the server
     */
    public boolean leaveLobby(){

        if(!acquireSendingRights()) return false;

        mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.LEAVE_LOBBY);
        sendToServer();
        return true;

    }

    /**
     * Sends the color preference for the game
     * @param colorChosen the color (white, grey or black, depending on the game mode)
     *                    the user chooses
     * @return true if the message was successfully sent, false if nothing was sent to the server
     */
    public boolean sendTeamColorChoice(TeamEnum colorChosen){

        if(!acquireSendingRights()) return false;

        mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.SELECT_TOWER_COLOR);
        mainBroker.addToMessage(NetworkFieldEnum.ID_TOWER_COLOR, colorChosen.index);

        sendToServer();
        return true;
    }

    /**
     * Sends the wizard chosen for this game
     * @param wizardChosen the wizard, among the 4 available, chosen by the user
     * @return true if the message was successfully sent, false if nothing was sent to the server
     */
    public boolean sendWizardChoice(WizardEnum wizardChosen){
        if(!acquireSendingRights()) return false;

        mainBroker.addToMessage(NetworkFieldEnum.COMMAND, CommandEnum.SELECT_WIZARD);
        mainBroker.addToMessage(NetworkFieldEnum.ID_WIZARD, wizardChosen.index);

        sendToServer();
        return true;
    }

    /*
        UTILITY METHODS
                        */

    /**
     * This method, using the output stream of the server, adds the request ID
     * and tries to send the message that was previously constructed in the appropriate
     * methods
     */
    private boolean sendToServer() {

        int requestId = addIdRequest();
        addIdUser();

        try{
            mainBroker.send(outputStream);
        }
        catch (IOException e){
            initialConnector.notifyNetworkError("Couldn't send the message to the serve, closing...");
            return false;
        }
        //todo add ack check basically

        /*
        Double gottenId;
        gottenId = (Double) mainBroker.readField(NetworkFieldEnum.ID_REQUEST);
        if(requestId != gottenId.intValue()){
            System.out.println("Messages received in wrong order.");
            return false;
        }

         */
        return true;
    }

    private void addIdUser() {
        mainBroker.addToMessage(NetworkFieldEnum.ID_USER, this.idUser);
    }

    /**
     * Adds the idRequest field to the current outgoing message
     * @return the id that's just been added
     */
    private int addIdRequest(){
        int idReq = increaseAndGetRequestId();
        mainBroker.addToMessage(NetworkFieldEnum.ID_REQUEST, idReq);
        return idReq;
    }

    /**
     * Increases idRequest and returns it
     * @return The increased idRequest
     */
    private int increaseAndGetRequestId(){
        progressiveIdRequest++;
        return progressiveIdRequest;
    }

    /**
     * Tries to acquire sending rights by checking the atomic boolean.
     * @return true if the boolean was successfully set, false otherwise
     */
    private synchronized boolean acquireSendingRights() {
        //It's not atomic but it does not cause problems with the other thread since
        // this method is synchronized
       if(isCommandScheduled.get()){
           return false;
       } else {
           isCommandScheduled.set(true);
           return true;
       }
    }


    public void assignIdUser(int idUser) {
        this.idUser = idUser;
    }
}