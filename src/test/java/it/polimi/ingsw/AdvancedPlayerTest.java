package it.polimi.ingsw;

import it.polimi.ingsw.model.game.AdvancedGame;
import it.polimi.ingsw.model.player.AdvancedPlayer;
import it.polimi.ingsw.model.game.IncorrectPlayersException;
import org.junit.Before;

public class AdvancedPlayerTest {

    AdvancedPlayer player;
    AdvancedGame game;

    @Before
    public void initializePlayers(){
        try {
            game = new AdvancedGame(2, 20, 3);
        } catch(IncorrectPlayersException e) {
            e.printStackTrace();
        }
        //TODO
    }
}