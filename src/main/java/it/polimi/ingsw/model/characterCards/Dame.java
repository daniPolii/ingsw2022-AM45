package it.polimi.ingsw.model.characterCards;

import it.polimi.ingsw.model.AdvancedGame;
import it.polimi.ingsw.model.AdvancedParameterHandler;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Sack;

public class Dame extends InitialEffect{

    private final int startingStudents = 4;

    public Dame(AdvancedParameterHandler advancedParameters){
        super(2,11, advancedParameters);
    }

    @Override
    public void initialise(AdvancedGame game) {
        addAllStudents(game.getSack().drawNStudents(startingStudents));
    }

    @Override
    public void activateEffect() {
        super.activateEffect();
    }

    /**
     * Take  student indexStudentDame from this Card ant put it in player's Hall.
     * Draw a new student from sack and put it on this card
     * @param player
     * @param indexStudenDame
     * @param sack
     */
    public void placeStudentToHall(Player player, int indexStudenDame, Sack sack){
        player.getBoard().addToHall(removeStudent(indexStudenDame));
        addAllStudents(sack.drawNStudents(1));
    }
}
