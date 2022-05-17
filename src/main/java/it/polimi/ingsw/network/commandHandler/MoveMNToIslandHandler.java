package it.polimi.ingsw.network.commandHandler;

import it.polimi.ingsw.network.ClientHandlerParameters;
import it.polimi.ingsw.network.CommandEnum;
import it.polimi.ingsw.network.MessageBroker;
import it.polimi.ingsw.network.NetworkFieldEnum;
import it.polimi.ingsw.network.connectionState.CloudChoosing;

public class MoveMNToIslandHandler extends CommandHandler{

    public MoveMNToIslandHandler(){
        commandAccepted = CommandEnum.MOVE_MN_TO_ISLAND;
    }

    /**
     * The user moves mother nature
     */
    @Override
    public boolean executeCommand(MessageBroker messageBroker, ClientHandlerParameters parameters) throws UnexecutableCommandException {

        CommandEnum readCommand = CommandEnum.fromObjectToEnum(messageBroker.readField(NetworkFieldEnum.COMMAND));
        if(!checkHandleable(readCommand, commandAccepted)) throw new UnexecutableCommandException();

        Integer steps = (Integer)messageBroker.readField(NetworkFieldEnum.STEPS_MN);
        if(parameters.getUserController().moveMNToIsland(steps)){
            notifySuccessfulOperation(messageBroker);
            parameters.setConnectionState(new CloudChoosing());
            return true;
        }
        else {
            notifyError(messageBroker,"Couldn't move mother nature");
            return false;
        }
    }
}
