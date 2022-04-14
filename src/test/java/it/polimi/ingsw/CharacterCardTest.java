package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.characterCards.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterCardTest {

    /**
     * tests if FactoryCharacterCard create a card correctly, with right id and cost
     */
    @Test
    public void createCard(){

        CharacterCard card = FactoryCharacterCard.getSpecificCard(1,null);
        CharacterCard priest = new Priest(null);
        assertEquals(card, priest, "Wrong card");
        assertEquals(card.id, priest.id, "Wrong id");
        assertEquals(card.getCardCost(), priest.getCardCost(), "Wrong cost");

    }

    /**
     * tests if FactoryCharacterCard return 3 random and different cards
     */
    @Test
    public void randomCards(){
        List<CharacterCard> listCards = new ArrayList<>();
        CharacterCard card1 = FactoryCharacterCard.getCharacterCard(listCards,null);
        listCards.add(card1);
        CharacterCard card2 = FactoryCharacterCard.getCharacterCard(listCards,null);
        listCards.add(card2);
        CharacterCard card3 = FactoryCharacterCard.getCharacterCard(listCards,null);
        assertNotEquals(card1,card2, "Two equal cards");
        assertNotEquals(card2,card3,"Two equals cards");
        assertNotEquals(card1,card3,"Two equals cards");
    }

    /**
     * tests if after activate effect of a card, this one increment his cost by one
     */
    @Test
    public  void incrementCost(){
        AdvancedGame game = null;
        try {
            game = new AdvancedGame(2, 20,3);
        }
        catch (IncorrectPlayersException e) {
            e.printStackTrace();
        }
        CharacterCard card = FactoryCharacterCard.getCharacterCard(game.getAdvancedParameters());
        int costBefore = card.getCardCost();
        card.activateEffect();
        int costNow = card.getCardCost();
        assertEquals(costBefore + 1, costNow, "Cost updating not correct");
    }

    /**
     * tests if cost increment is applied one time
     */
    @Test
    public void oneTimeIncrementCost(){
        AdvancedGame game = null;
        try {
            game = new AdvancedGame(2, 20,3);
        }
        catch (IncorrectPlayersException e) {
            e.printStackTrace();
        }        List<CharacterCard> cards = new ArrayList<>();
        cards.addAll(FactoryCharacterCard.getAllCards(game.getAdvancedParameters()));
        int costBefore;
        int costNow;

        //TIME WHERE INCREMENT
        for(CharacterCard cardSel: cards){
            costBefore = cardSel.getCardCost();
            cardSel.activateEffect();
            costNow = cardSel.getCardCost();
            assertEquals(costBefore + 1, costNow, "Wrong cost with" + cardSel.id + "card");
        }

        //TIME WHERE DO NOT INCREMENT
        for(CharacterCard cardSel: cards){
            costBefore = cardSel.getCardCost();
            cardSel.activateEffect();
            costNow = cardSel.getCardCost();
            assertEquals(costBefore, costNow, "Wrong cost with" + cardSel.id + "card");
        }

    }

    /**
     * tests InitialEffect Cards, if after using their methods they still have a consistent state
     */
    @Test
    public void InitialEffectCards(){
        List<CharacterCard> cards = new ArrayList<>();
        AdvancedGame game = null;
        try {
            game = new AdvancedGame(2, 20,3);
        }
        catch (IncorrectPlayersException e) {
            e.printStackTrace();
        }
        cards.addAll(FactoryCharacterCard.getInitialEffectCards(game.getAdvancedParameters()));
        ParameterHandler parameters = new ParameterHandler(2);
        game.setCurrentPlayer(new Player(PlayerEnum.PLAYER1,"Bob", TeamEnum.BLACK, true,parameters));
        StudentEnum studentSel;
        Board board = game.getCurrentPlayer().getBoard();
        IslandGroup island = new IslandGroup(0,null,null,
                                            null, new ArrayList<>(),TeamEnum.NOTEAM,parameters);


        for(CharacterCard cardSel: cards){
            if(cardSel.id == 7){

                //JUGGLER SET TO TEST
                Juggler juggler = (Juggler) cardSel;
                juggler.removeAll();
                juggler.addStudent(StudentEnum.RED);
                studentSel = juggler.getStudents(0);

                //JUGGLER METHOD
                juggler.tradeStudents(StudentEnum.BLUE, 0, game.getCurrentPlayer());

                //TESTS
                assertEquals(juggler.getStudents(0), StudentEnum.BLUE,
                        "Wrong student added to Juggler");
                assertEquals(studentSel,board.getAtEntrance(board.entranceSize() - 1),
                        "Wrong student at Entrance");
            }


            if(cardSel.id == 1){

                //PRIEST SET TO TEST
                Priest priest = (Priest) cardSel;
                priest.removeAll();
                priest.addStudent(StudentEnum.RED);
                studentSel = priest.getStudents(0);

                //PRIEST METHOD
                priest.placeStudentOnIsland(game,island,0);

                //TESTS
                assertEquals(studentSel, island.getStudents().get(0), "Wrong student add to island");
                assertTrue(!priest.isEmpty(),"Did not draw from sack");
            }

            if(cardSel.id == 11){

                //DAME SET TO TEST
                Dame dame = (Dame) cardSel;
                dame.removeAll();
                dame.addStudent(StudentEnum.RED);
                studentSel = dame.getStudents(0);
                int studentAtTable = board.getStudentsAtTable(studentSel).intValue();

                //DAME METHOD
                dame.placeStudentToHall(game.getCurrentPlayer(),0,game.getSack());

                //TESTS
                assertEquals(studentAtTable + 1, board.getStudentsAtTable(studentSel).intValue(),
                        "Wrong number of students in Hall");
                assertTrue(!dame.isEmpty(), "Did not draw from sack");
            }
        }

    }
}
