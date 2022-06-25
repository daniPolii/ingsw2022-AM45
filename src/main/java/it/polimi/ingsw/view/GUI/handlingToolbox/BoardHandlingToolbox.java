package it.polimi.ingsw.view.GUI.handlingToolbox;

import it.polimi.ingsw.model.StudentEnum;
import it.polimi.ingsw.network.CommandEnum;
import it.polimi.ingsw.network.client.ClientSender;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BoardHandlingToolbox implements HandlingToolbox{

    private final List<EventHandler<MouseEvent>> onEntranceStudentClick;
    private List<EventHandler<MouseEvent>> onTableClick;

    public static final BoardHandlingToolbox NONINTERACTIVE = new BoardHandlingToolbox(9,5);

    private List<StudentEnum> colorsSelected;
    private List<Integer> entranceStudentsSelected;

    public BoardHandlingToolbox(int entranceStudents, int numTables){

        onEntranceStudentClick = new ArrayList<>();

        for (int student = 0; student < entranceStudents; student++) {
            onEntranceStudentClick.add(DISABLED);
        }

        onTableClick = new ArrayList<>();

        for (int table = 0; table < numTables; table++) {
            onTableClick.add(DISABLED);
        }

        colorsSelected = new ArrayList<>();
        entranceStudentsSelected = new ArrayList<>();
    }

    @Override
    public void allowCommand(CommandEnum command, ClientSender resourceProvider) {
        if (command == CommandEnum.SELECT_STUDENT){
            int studentIndex = 0;

            for (EventHandler<MouseEvent> ignored :
                 onEntranceStudentClick) {

                int finalStudentIndex = studentIndex;

                onEntranceStudentClick.set(finalStudentIndex, event -> {
                        onEntranceStudentClick.set(finalStudentIndex, NO_EFFECT);
                        new Thread(() ->
                    resourceProvider.sendSelectedStudent(finalStudentIndex)).start();}
                );

                studentIndex++;

            }

        }

        if (command == CommandEnum.PUT_IN_HALL){
            int tableIndex = 0;

            for (EventHandler<MouseEvent> ignored : onTableClick) {

                onTableClick.set(tableIndex, event -> new Thread (resourceProvider::sendPutInHall).start());
                tableIndex++;
            }
        }

        if (command == CommandEnum.SELECT_STUDENT_COLORS) {
            int tableIndex = 0;

            for (EventHandler<MouseEvent> ignored : onTableClick){
                int finalIndex = tableIndex;
                onTableClick.set(finalIndex, event -> new Thread(() -> colorsSelected.add(StudentEnum.getColorById(finalIndex))).start());
                tableIndex++;
            }
        }

        if (command == CommandEnum.SELECT_ENTRANCE_STUDENTS){

            int studentIndex = 0;

            for (EventHandler<MouseEvent> ignored: onEntranceStudentClick){
                int finalIndex = studentIndex;
                onEntranceStudentClick.set(finalIndex, event -> new Thread(() -> entranceStudentsSelected.add(finalIndex)).start());
                studentIndex++;
            }
        }

        if (command == CommandEnum.DESELECT_STUDENT){



            for (EventHandler<MouseEvent> handler:
                 onEntranceStudentClick) {

                if (handler == NO_EFFECT) {

                    int index = onEntranceStudentClick.indexOf(handler);

                    onEntranceStudentClick.set(
                            index,
                            event -> {
                                onEntranceStudentClick.set(index, DISABLED);
                                new Thread(resourceProvider::sendDeselectStudent).start();
                            });
                }
            }

        }
    }

    @Override
    public void disableCommand(CommandEnum command) {
        if (command == CommandEnum.SELECT_STUDENT ||
            command == CommandEnum.DESELECT_STUDENT ||
            command == CommandEnum.SELECT_ENTRANCE_STUDENTS) {

            for (EventHandler<MouseEvent> handler:
                 onEntranceStudentClick) {

                if (handler != NO_EFFECT) onEntranceStudentClick.set(onEntranceStudentClick.indexOf(handler), DISABLED);
            }


        }

        if (command == CommandEnum.PUT_IN_HALL ||
            command == CommandEnum.SELECT_STUDENT_COLORS)
            for (EventHandler<MouseEvent> handler:
                 onTableClick) {

                if (handler != NO_EFFECT) onTableClick.set(onTableClick.indexOf(handler), DISABLED);
            }
    }


    /**
     * Returns the allowed action for the given entrance student
     * @param pos the position of the student at the entrance
     * @return The EventHandler to assign to the Entrance student
     */
    public EventHandler<MouseEvent> getOnEntranceStudentClick(int pos){
        return onEntranceStudentClick.get(pos);
    }

    public EventHandler<MouseEvent> getOnHallClick(int pos) {
        return onTableClick.get(pos);
    }

    public List<StudentEnum> getColorsSelected() {
        return colorsSelected;
    }

    public List<Integer> getEntranceStudentsSelected() {
        return entranceStudentsSelected;
    }
}
