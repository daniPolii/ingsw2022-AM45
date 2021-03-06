package it.polimi.ingsw.model.characterCards;

import it.polimi.ingsw.model.game.AdvancedGame;
import it.polimi.ingsw.model.game.AdvancedParameterHandler;
import it.polimi.ingsw.model.game.ParameterHandler;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.view.VirtualView;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Juggler extends InitialEffect{
    private static final String name ="JUGGLER";
    private static final String description =
            "You may take up to 3 Students " +
            "from this card and replace them " +
            "with the same number of Students " +
            "from your Entrance.";


    private final int maxTradeableStudents = 3;
    private int numTrades;

    public Juggler(ParameterHandler parameters, AdvancedParameterHandler advancedParameters){
        super(1,7, parameters, advancedParameters,name,description);
        requirements = new Requirements(0,maxTradeableStudents,0,maxTradeableStudents);
    }

    @Override
    public void initialise(AdvancedGame game) {
        super.initialise(game);
        final int startingStudents = 6;
        addAllStudents(game.getSack().drawNStudents(startingStudents));
    }

    /**
     * You can take maximum maxTradeableStudents from this card and exchange them with
     * currentPlayer students at Entrance
     */
    @Override
    public void activateEffect() {
        super.activateEffect();
        numTrades = 0;

        List<Integer> indexJuggler,indexEntrance;

        //CHECK IF USER SELECTED STUDENTS AT ENTRANCE
        if(parameters.getSelectedEntranceStudents().isEmpty()){
            parameters.setErrorState("BAD PARAMETERS WITH SelectedEntranceStudent");
            return;
        }


        //CHECK IF USER SELECTED STUDENTS ON CARD
        if(advancedParameters.getSelectedStudentsOnCard().isEmpty()){
            parameters.setErrorState("BAD PARAMETERS WITH SelectedStudentsOnCard");
            return;
        }

        //GET SELECTIONS AND CHECK IF THEY HAVE THE RIGHT SIZE
        indexJuggler = advancedParameters.getSelectedStudentsOnCard().get();
        indexEntrance = parameters.getSelectedEntranceStudents().get();

        Collections.sort(indexJuggler);
        Collections.reverse(indexJuggler);

        Collections.sort(indexEntrance);
        Collections.reverse(indexEntrance);

        if(indexEntrance.size() != indexJuggler.size() && indexJuggler.size() > maxTradeableStudents){
            parameters.setErrorState("WRONG SIZE WITH CHOSEN STUDENTS");
            return;
        }

        //MAKE THE EXCHANGE
        Iterator<Integer> iteratorJuggler = indexJuggler.iterator();
        Iterator<Integer> iteratorEntrance = indexEntrance.iterator();
        while(iteratorJuggler.hasNext() && numTrades < maxTradeableStudents){
            tradeStudents(iteratorEntrance.next(),iteratorJuggler.next());
        }
    }

    /**
     * Take one student from Juggler and one student from Player's Entrance and exchange them
     * if the number of trades in this turn is grater than maxTradeableStudents do not
     * exchange them.
     * @param studentToJuggler > 0 && < Entrance.size()
     * @param index > 0 && < size()
     */
    public void tradeStudents(int studentToJuggler, int index){

        Player player = parameters.getCurrentPlayer();

        if(numTrades > maxTradeableStudents)
            return;

        addStudent(player.getBoard().removeFromEntrance(studentToJuggler));
        player.getBoard().addToEntrance(removeStudent(index));
        numTrades++;
    }

}
