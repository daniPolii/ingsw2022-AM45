package it.polimi.ingsw.network.commandHandler.synchronous;

import it.polimi.ingsw.model.characterCards.Requirements;
import it.polimi.ingsw.network.ApplicationHelper;
import it.polimi.ingsw.network.commandHandler.UnexecutableCommandException;
import it.polimi.ingsw.network.server.ClientHandlerParameters;
import it.polimi.ingsw.network.CommandEnum;
import it.polimi.ingsw.network.MessageBroker;
import it.polimi.ingsw.network.NetworkFieldEnum;
import it.polimi.ingsw.network.connectionState.CharacterCardActivation;

public class SelectCharacterHandler extends CommandHandler{

    public SelectCharacterHandler(){
        commandAccepted = CommandEnum.SELECT_CHARACTER;
    }

    /**
     * The user selects a character card to play
     */
    @Override
    public boolean executeCommand(MessageBroker messageBroker, ClientHandlerParameters parameters) throws UnexecutableCommandException {

        CommandEnum readCommand = CommandEnum.fromObjectToEnum(messageBroker.readField(NetworkFieldEnum.COMMAND));
        if(!checkHandleable(readCommand, commandAccepted)) throw new UnexecutableCommandException();


        Integer cardPosition = ApplicationHelper.getIntFromBrokerField(messageBroker.readField(NetworkFieldEnum.CHARACTER_CARD_POSITION));
        if(!parameters.getUserController().selectCard(cardPosition)){
            notifyError(messageBroker,parameters.getUserController().getGameErrorMessage());
            return false;
        }
        else {
            //Here we need to memorize what the previous state the user had to resume the turn
            // after the character takes effect
            parameters.setCallbackConnectionState(parameters.getConnectionState());

            notifySuccessfulOperation(messageBroker);
            Requirements requirements = parameters.getUserController().getCardRequirements();
            messageBroker.addToMessage(NetworkFieldEnum.ENTRANCE_REQUIRED, requirements.studentAtEntrance);
            messageBroker.addToMessage(NetworkFieldEnum.COLORS_REQUIRED, requirements.studentType);
            messageBroker.addToMessage(NetworkFieldEnum.ISLANDS_REQUIRED, requirements.islands);
            messageBroker.addToMessage(NetworkFieldEnum.ON_CARD_REQUIRED, requirements.studentOnCard);

            parameters.setConnectionState(new CharacterCardActivation());
            return true;
        }
    }
}
