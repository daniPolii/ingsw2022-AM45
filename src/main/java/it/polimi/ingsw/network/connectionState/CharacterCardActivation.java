package it.polimi.ingsw.network.connectionState;

import it.polimi.ingsw.network.CommandEnum;

public class CharacterCardActivation extends ConnectionState{

    public CharacterCardActivation(){

        super();
        allow(CommandEnum.SELECT_ENTRANCE_STUDENTS);
        allow(CommandEnum.SELECT_STUDENT_COLORS);
        allow(CommandEnum.SELECT_ISLAND_GROUP);
        allow(CommandEnum.SELECT_STUDENTS_ON_CARD);
        allow(CommandEnum.PLAY_CHARACTER);
    }
}
