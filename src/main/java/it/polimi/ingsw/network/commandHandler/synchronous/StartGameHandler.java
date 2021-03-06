package it.polimi.ingsw.network.commandHandler.synchronous;

import it.polimi.ingsw.network.*;
import it.polimi.ingsw.network.commandHandler.UnexecutableCommandException;
import it.polimi.ingsw.network.server.ActiveLobbies;
import it.polimi.ingsw.network.server.ClientHandlerParameters;

public class StartGameHandler extends CommandHandler{

    public StartGameHandler(){
        commandAccepted = CommandEnum.START_GAME;
    }

    /**
     * The user requests to start the game
     * The request will be successful only if the host coincides with the user and all players are ready
     */
    @Override
    public boolean executeCommand(MessageBroker messageBroker, ClientHandlerParameters parameters) throws UnexecutableCommandException {

        CommandEnum readCommand = CommandEnum.fromObjectToEnum(messageBroker.readField(NetworkFieldEnum.COMMAND));
        if(!checkHandleable(readCommand, commandAccepted)) throw new UnexecutableCommandException();

        // This, SendNotReady and SendReady should all be synchronized to some lock
        parameters.getUserLobby().readyLock.lock();

        try {
            if (!parameters.getUserLobby().isHost(parameters.getIdUser())) {
                notifyError(messageBroker, "You're not the host! You can't start the game.");
                return false;
            } else {
                if (ActiveLobbies.startGame(parameters.getUserLobby())) {
                    notifySuccessfulOperation(messageBroker);
                } else {
                    notifyError(messageBroker, "The game couldn't start, returning to lobby");
                }
            }
        }
        finally {
            parameters.getUserLobby().readyLock.unlock();
        }

        return true;
    }
}
