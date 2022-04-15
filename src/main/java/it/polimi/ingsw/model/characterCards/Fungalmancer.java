package it.polimi.ingsw.model.characterCards;

import it.polimi.ingsw.model.AdvancedGame;
import it.polimi.ingsw.model.AdvancedParameterHandler;
import it.polimi.ingsw.model.ParameterHandler;

public class Fungalmancer extends CharacterCard {

    public Fungalmancer(ParameterHandler parameters, AdvancedParameterHandler advancedParameters){
        super(3,9, parameters,advancedParameters);
    }

    /**
     * During this turn one color of students don't give influence points
     * Set IgnoredStudent true
     * @param game
     */
    //@Override
    //TODO I'm not touching this but it needs to be changed
    public void activateEffect(AdvancedGame game) {

        super.activateEffect();

        game.setIgnoredStudent(true);
    }
}
