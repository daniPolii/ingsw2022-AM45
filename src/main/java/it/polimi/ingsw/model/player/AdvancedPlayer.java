package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.StudentEnum;
import it.polimi.ingsw.model.beans.AdvancedPlayerBean;
import it.polimi.ingsw.model.beans.GameElementBean;
import it.polimi.ingsw.model.board.AdvancedBoard;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.game.ParameterHandler;
import it.polimi.ingsw.model.TeamEnum;

import java.util.ArrayList;
import java.util.List;

public class AdvancedPlayer extends Player {
    private int numCoins;

    public AdvancedPlayer(PlayerEnum playerId, String nickname, TeamEnum teamColor, boolean leader, ParameterHandler parameters){
        super(playerId, nickname, teamColor, leader, parameters);
        this.numCoins = 1;
    }

    @Override
    protected Board createBoard(TeamEnum teamColor, ParameterHandler parameters) {
        return new AdvancedBoard(teamColor, parameters);
    }

    @Override
    public StudentEnum moveFromEntranceToHall() {
        return board.moveFromEntranceToHall();
    }

    public void addCoin(){
        numCoins++;
    }
    public void useCoin(){
        numCoins--;
    }

    public int getNumCoins() {
        return numCoins;
    }

    /**
     *
     * @return a bean with all information about this player,
     * his nickname, color team, student at tables and at entrance,
     * his assistants cards, his professors and his coins
     */
    @Override
    public GameElementBean toBean() {
        int numTowers = getNumTowers();
        List<StudentEnum> studentsAtEntrance = board.getStudentsAtEntrance();
        List<Integer> studPerTable = new ArrayList<>();
        List<StudentEnum> professors;
        List<Integer> idAssistants = wizard.getRemainedAssistants();        //Get assistant cards id
        for(StudentEnum color: StudentEnum.values()){                       //Get students per table
            if(color != StudentEnum.NOSTUDENT)
                studPerTable.add(board.getStudentsAtTable(color));
        }

        professors = parameters.getProfessorsByPlayer(playerId);
        AdvancedPlayerBean bean = new AdvancedPlayerBean(nickname,playerId,leader,teamColor,numTowers,
                studentsAtEntrance, studPerTable, professors, idAssistants, numCoins);
        return bean;
    }
}
