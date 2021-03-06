package it.polimi.ingsw.model;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.FullEntranceException;
import it.polimi.ingsw.model.game.ParameterHandler;
import it.polimi.ingsw.model.islands.Island;
import it.polimi.ingsw.model.islands.IslandGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    Board board = null;
    StudentEnum student = null;
    ParameterHandler parameters = null;

    @BeforeEach
    public void initialise(){
        parameters = new ParameterHandler(2);
        board = new Board(TeamEnum.WHITE, parameters);
        student = StudentEnum.GREEN;
    }

    /**
     * Tests if updateTowers returns the correct boolean value
     */
    @Test
    public void updateTowersTest(){
        assertFalse(board.updateTowers(-2),"Returned true when not out of towers");
        assertTrue(board.updateTowers(-6),"Returned false when out of towers");
    }




    /**
     * Tests if the selected student is correctly moved from the Entrance to the right table
     */
    @Test
    public void moveFromEntranceToHallTest(){
        board.addToEntrance(student);
        parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
        assertEquals(board.moveFromEntranceToHall(),student,"Not corresponding Student types");
        assertEquals(board.entranceSize(),0,"Student is still at the Entrance");
        assertEquals(board.getStudentsAtTable(student),1,"Student was not moved to the correct table");

        for (StudentEnum table: StudentEnum.values()){

            if (table == student || table == StudentEnum.NOSTUDENT) continue;

            assertEquals(board.getStudentsAtTable(table),0,"Student was probably moved on the wrong table");
        }
    }

    /**
     * Tests if movement to a full table is rejected
     */
    @Test
    public void moveFromEntranceToHallFullTableTest(){
        for(int count = 0; count < 10; count++){
            board.addToEntrance(student);
            parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
            board.moveFromEntranceToHall();
        }

        board.addToEntrance(student);
        parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
        assertEquals(board.moveFromEntranceToHall(),StudentEnum.NOSTUDENT,"Student table overflown");
    }

    /**
     * Tests if selectedEntranceStudentPos is correctly reset.
     */
    @Test
    public void moveFromEntranceToHallResetTest(){
        board.addToEntrance(student);
        parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
        board.moveFromEntranceToHall();

        assertEquals(board.moveFromEntranceToHall(),StudentEnum.NOSTUDENT,"Selected student has not been reset");
    }

    /**
     * Tests correct placing from Entrance to IslandGroup
     */
    @Test
    public void moveFromEntranceToIslandTest(){
        Island island = new Island(0);
        List<Island> islands = new ArrayList<>();
        islands.add(island);

        IslandGroup islandGroup = new IslandGroup(0,islands,null,null, new ArrayList<>(),TeamEnum.NOTEAM,parameters);

        board.addToEntrance(student);
        parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
        board.moveFromEntranceToIsland(islandGroup);

        assertEquals(board.entranceSize(),0,"Student is still at the Entrance");
    }

    /**
     * Tests if selectedEntranceStudentPos is correctly reset.
     */
    @Test
    public void moveFromEntranceToIslandResetTest(){
        Island island = new Island(0);
        List<Island> islands = new ArrayList<>();
        islands.add(island);

        IslandGroup islandGroup = new IslandGroup(0,islands,null,null, new ArrayList<>(),TeamEnum.NOTEAM,parameters);

        board.addToEntrance(student);
        parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
        board.moveFromEntranceToIsland(islandGroup);
        board.addToEntrance(student);
        board.moveFromEntranceToIsland(islandGroup);

        assertEquals(board.entranceSize(),1,"Selected student has not been reset");
    }

    /**
     * Tests if students are correctly collected from Cloud
     */
    @Test
    public void moveFromCloudTest(){
        Cloud cloud = new Cloud(0,3);
        List<StudentEnum> students = new ArrayList<>();

        while (students.size() < 3) students.add(student);
        cloud.fill(students);
        board.moveFromCloud(cloud);

        assertEquals(board.entranceSize(),3,"Moved wrong number of students");
        for (int count = 0; count < 3; count++) assertEquals(board.getStudentsAtEntrance().get(count),student,"There's an impostor among us");
    }

    /**
     * Tests if student is correctly moved from the Hall to the entrance
     */
    @Test
    public void moveFromHallToEntranceTest(){
        board.addToEntrance(student);
        parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
        board.moveFromEntranceToHall();

        try {
            board.moveFromHallToEntrance(student);
        } catch (FullEntranceException e){
            e.printStackTrace();
        }

        assertEquals(board.getAtEntrance(0),student,"Wrong student type at Entrance");
        assertEquals(board.entranceSize(),1,"There is not exactly 1 student at the Entrance");
        assertEquals(board.getStudentsAtTable(student),0,"Student is still in the Hall");

        for (StudentEnum studentEnum : StudentEnum.values()){

            if (studentEnum == student || studentEnum == StudentEnum.NOSTUDENT) continue;

            assertEquals(board.getStudentsAtTable(studentEnum),0,"Somehow another table was modified");
        }
    }

    /**
     * Tests correct throw of FullEntranceException
     */
    @Test
    public void moveFromHallToFullEntranceTest(){
        while (board.entranceSize() < parameters.getMaxStudentsAtEntrance()) board.addToEntrance(student);
        parameters.setSelectedEntranceStudents(new ArrayList<>(){{add(0);}});
        board.moveFromEntranceToHall();
        board.addToEntrance(student);
        assertThrows(FullEntranceException.class, () -> board.moveFromHallToEntrance(student),"Method did not interrupt on faulty call");
    }

    /**
     * Test if Students are correctly removed from the Hall
     */
    /* no need to test the case of an empty Hall because it is already handled by
    removeFromHall which is method simple enough to not require testing */
    @Test
    public void removeNStudentsFromHallTest(){

        int numStudents = 3;

        for (int i = 0; i < numStudents; i++) {
            board.addToHall(student);
        }

        List<StudentEnum> students;

        students = board.removeNStudentsFromHall(student, numStudents);

        assertEquals(board.getStudentsAtTable(student).intValue(),0,"Not all students were removed");

        for (int pos = 0; pos < numStudents; pos++) {
            assertEquals(students.get(pos), student,"There's an impostor among us");
        }
    }
}
