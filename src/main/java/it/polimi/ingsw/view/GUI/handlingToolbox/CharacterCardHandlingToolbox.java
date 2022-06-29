package it.polimi.ingsw.view.GUI.handlingToolbox;

import it.polimi.ingsw.network.CommandEnum;
import it.polimi.ingsw.network.client.ClientSender;
import it.polimi.ingsw.view.GUI.CharacterCardSelection;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CharacterCardHandlingToolbox implements HandlingToolbox{

    private EventHandler<MouseEvent> onCharacterCardClick;
    private List<EventHandler<MouseEvent>> onStudentOnCardClick;

    //needed to re-allow all commands whenever the island group morphology changes
    private EnumSet<CommandEnum> allowedCommands = EnumSet.noneOf(CommandEnum.class);
    private ClientSender sender;


    private boolean selected = false;
    private int cardIndex;

    private CharacterCardSelection selections;

    public CharacterCardHandlingToolbox(){
        onCharacterCardClick = HandlingToolbox.DISABLED;
        onStudentOnCardClick = new ArrayList<>();
    }

    public void setCardInfo(int cardIndex, int numStudents){

        this.cardIndex = cardIndex;

        if (onStudentOnCardClick.isEmpty()) {
            for (int student = 0; student < numStudents; student++) {
                onStudentOnCardClick.add(DISABLED);
            }
        }

        if (!allowedCommands.isEmpty()){
            for (CommandEnum command : allowedCommands) {
                allowCommand(command, sender);
            }
        }
    }

    @Override
    public void allowCommand(CommandEnum command, ClientSender resourceProvider) {

        if (!resourceProvider.equals(sender)) sender = resourceProvider;

        allowedCommands.add(command);

        if (command == CommandEnum.SELECT_CHARACTER){

            selected = false;

            onCharacterCardClick = event -> {
                selected = true;
                new Thread(() -> resourceProvider.sendSelectCharacter(cardIndex)).start();
            };
        }

        if (selected && command == CommandEnum.SELECT_STUDENTS_ON_CARD) {

            int studentIndex = 0;

            for (EventHandler<MouseEvent> ignored:
                 onStudentOnCardClick) {
                int finalIndex = studentIndex;
                onStudentOnCardClick.set(finalIndex, event -> {
                    onStudentOnCardClick.set(finalIndex, NO_EFFECT);
                    selections.addStudentOnCard(finalIndex);
                    System.out.println("Added student on card");
                });
                studentIndex++;
            }
        }
    }

    @Override
    public void disableCommand(CommandEnum command) {

        allowedCommands.remove(command);

        if (command == CommandEnum.SELECT_CHARACTER){
            onCharacterCardClick = NO_EFFECT;
        }

        if (command == CommandEnum.SELECT_STUDENTS_ON_CARD){

            int index = 0;

            for (EventHandler<MouseEvent> ignored:
                 onStudentOnCardClick) {
                onStudentOnCardClick.set(index, DISABLED);
                index++;
            }
        }
    }

    public void setSelectionsContainer(CharacterCardSelection selections) {
        this.selections = selections;
    }

    public EventHandler<MouseEvent> getOnCharacterCardClick() {
        return onCharacterCardClick;
    }

    public EventHandler<MouseEvent> getOnStudentOnCardClick(int pos) {
        return onStudentOnCardClick.get(pos);
    }

    public boolean isSelected(){
        return selected;
    }
}
