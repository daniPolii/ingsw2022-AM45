package it.polimi.ingsw.network.commandHandler.synchronous;

import it.polimi.ingsw.network.ApplicationHelper;
import it.polimi.ingsw.network.commandHandler.UnexecutableCommandException;
import it.polimi.ingsw.network.server.ClientHandlerParameters;
import it.polimi.ingsw.network.CommandEnum;
import it.polimi.ingsw.network.MessageBroker;
import it.polimi.ingsw.network.NetworkFieldEnum;

public class PlayCharacterHandler extends CommandHandler{

    public PlayCharacterHandler(){
        commandAccepted = CommandEnum.PLAY_CHARACTER;
    }

    /**
     * The player plays the card for which they (optionally) selected the requirements
     */
    @Override
    public boolean executeCommand(MessageBroker messageBroker, ClientHandlerParameters parameters) throws UnexecutableCommandException {

        CommandEnum readCommand = CommandEnum.fromObjectToEnum(messageBroker.readField(NetworkFieldEnum.COMMAND));
        if(!checkHandleable(readCommand, commandAccepted)) throw new UnexecutableCommandException();

        if(parameters.getUserController().playCard()){
            super.notifySuccessfulOperation(messageBroker);
            parameters.setConnectionState(parameters.getCallbackConnectionState());
            return true;
        }
        else {
            //Error while playing the card
            // everything has been deselected already
            // go back to the previous state
            parameters.setConnectionState(parameters.getCallbackConnectionState());
            notifyError(messageBroker,"Couldn't play the character card");
            return false;
        }
    }
}
