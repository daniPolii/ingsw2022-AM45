package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.assistantCards.Assistant;
import it.polimi.ingsw.model.assistantCards.FactoryWizard;
import it.polimi.ingsw.model.assistantCards.NoSuchAssistantException;
import it.polimi.ingsw.model.assistantCards.Wizard;
import it.polimi.ingsw.model.beans.GameElementBean;
import it.polimi.ingsw.model.beans.PlayerBean;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.game.ParameterHandler;
import it.polimi.ingsw.model.islands.IslandGroup;
import it.polimi.ingsw.view.VirtualView;
import it.polimi.ingsw.view.observer.PlayerWatcher;
import it.polimi.ingsw.view.observer.Watcher;

import java.util.ArrayList;
import java.util.List;

public class Player extends DrawableObject {
    protected   List<Watcher> watcherList;
    protected PlayerEnum playerId;
    protected String nickname;
    protected ParameterHandler parameters;
    protected Assistant assistantPlayed;
    protected TeamEnum teamColor;
    protected boolean leader;
    protected Board board;
    protected Wizard wizard;
    protected int turn;

    /**
     * ! This method doesn't allow for the choice of the wizard !
     * Basic Player constructor
     * @param playerId id given by SimpleGame on initialization
     * @param nickname string chosen by the user, should not be equal to other players
     * @param teamColor the team's color
     * @param leader true if the player is the leader of their team (has the towers on their board)
     */
    @Deprecated
    public Player(PlayerEnum playerId, String nickname, TeamEnum teamColor, boolean leader, ParameterHandler parameters) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.teamColor = teamColor;
        this.leader = leader;
        this.parameters = parameters;
        this.board = new Board(teamColor, parameters);
        this.wizard = FactoryWizard.getWizard(playerId.index*10);
    }

    /**
     * Player constructor which features all choices made by the user: nickname, tower color and wizard
     * @param playerId id given by SimpleGame on initialization
     * @param nickname nickname chosen by the user
     * @param teamColor this player's team's color
     * @param leader true if the player is the leader of their team (has the towers on their board)
     * @param parameters parameters of the game creating this player
     * @param wizard the wizard chosen by the user
     */
    public Player(PlayerEnum playerId, String nickname, TeamEnum teamColor, Wizard wizard, boolean leader, ParameterHandler parameters){        this.playerId = playerId;
        this.playerId = playerId;
        this.nickname = nickname;
        this.teamColor = teamColor;
        this.wizard = wizard;
        this.leader = leader;
        this.board = createBoard(teamColor, parameters);
        this.parameters = parameters;

        if(!leader)
            board.updateTowers(-board.getNumberOfTowers());
    }

    /**
     * Player constructor which features all choices made by the user: nickname, tower color and wizard <br>
     * Version with observer pattern
     * @param playerId id given by SimpleGame on initialization
     * @param nickname nickname chosen by the user
     * @param teamColor this player's team's color
     * @param leader true if the player is the leader of their team (has the towers on their board)
     * @param parameters parameters of the game creating this player
     * @param wizard the wizard chosen by the user
     * @param virtualView
     */
    public Player(PlayerEnum playerId, String nickname, TeamEnum teamColor,
                  Wizard wizard, boolean leader,
                  ParameterHandler parameters, VirtualView virtualView){
        this.playerId = playerId;
        this.nickname = nickname;
        this.teamColor = teamColor;
        this.wizard = wizard;
        this.leader = leader;
        this.board = createBoard(teamColor, parameters);
        this.parameters = parameters;

        if(!leader)
            board.updateTowers(-board.getNumberOfTowers());

        watcherList = new ArrayList<>();
        PlayerWatcher watcher = new PlayerWatcher(this, virtualView);
        watcherList.add(watcher);
        watchers = watcherList;
        alert();

    }


    protected Board createBoard(TeamEnum teamColor, ParameterHandler parameters){
        return new Board(teamColor, parameters);
    }


    public Board getBoard() {
        return board;
    }

    public PlayerEnum getPlayerId() {
        return playerId;
    }

    public TeamEnum getTeamColor() {
        return teamColor;
    }

    public boolean isLeader() {
        return leader;
    }

    public Assistant getAssistantPlayed() {
        return assistantPlayed;
    }

    public Wizard getWizard(){
        return wizard;
    }

    private void setAssistantPlayed(Assistant assistant){
        this.assistantPlayed = assistant;
    }

    /**
     * Plays the assistant by setting it as assistantPlayed
     * @param assistantId id of the assistant to play
     */
    public void playAssistant(int assistantId){
        Assistant tempAssistant = null;
        try {
            tempAssistant = wizard.playCard(assistantId);
            setAssistantPlayed(tempAssistant);
        }
        catch (NoSuchAssistantException e){
            e.printStackTrace();
            parameters.setErrorState("INCORRECT ASSISTANT ID");
        }
        alert();
    }

    /**
     * Set this player's assistant played to null
     */
    public void clearAssistantPlayed() {
        setAssistantPlayed(null);
        alert();
    }

    public String getNickname() {
        return nickname;
    }



    /**
     * Get from cloud the students to add at Entrance
     * @param cloud != null
     */
    public void getFromCloud(Cloud cloud){
        List<StudentEnum> students = cloud.empty();
        for(StudentEnum stud: students){
            board.addToEntrance(stud);
        }
        alert();
    }

    /**
     *
     * @return true if player's wizard is empty
     */
    public boolean noMoreAssistant(){
        return wizard.isEmpty();
    }

    /**
     *
     * @return false if player is not leader or if he has at least 1 tower
     *  true if player is a leader, and he has not towers
     */
    public boolean noMoreTowers(){
        if(!leader)
            return false;

        return board.getNumberOfTowers() == 0;
    }

    /**
     *
     * @return the number of towers in player's board
     */
    public int getNumTowers(){
        return board.getNumberOfTowers();
    }

    /**
     * Move the student from position parameter.selectedEntranceStudents
     * to Hall
     * @return the moved student's color
     */
    public StudentEnum moveFromEntranceToHall(){
        StudentEnum color= board.moveFromEntranceToHall();
        alert();
        return  color;
    }

    /**
     *
     * @param island != null
     */
    public void moveFromEntranceToIsland(IslandGroup island){
        board.moveFromEntranceToIsland(island);
        alert();
    }

    /**
     *
     * @param tableColor != NO_STUDENT && != null
     * @return the num of student at table with chosen color
     */
    public int getNumStudentAtTable(StudentEnum tableColor){
        return board.getStudentsAtTable(tableColor);
    }

    /**
     *
     * @param position > 0
     * @return the student in chosen position at Entrance
     */
    public StudentEnum getStudentFromEntrance(int position){
        return board.getAtEntrance(position);
    }

    /**
     *
     * @return a bean with all information about this player,
     *      * his nickname, color team, student at tables and at entrance,
     *      * his assistants cards, his professors
     */
    @Override
    public GameElementBean toBean(){
        int numTowers = getNumTowers();                                      //Get remaining towers
        List<StudentEnum> studAtEntrance = board.getStudentsAtEntrance();   //Get students at entrance
        List<Integer> studPerTable = new ArrayList<>();
        List<StudentEnum> professors;
        List<Assistant> Assistants = wizard.getRemainedAssistants();        //Get assistant cards id
        for(StudentEnum color: StudentEnum.values()){                       //Get students per table
            if(color != StudentEnum.NOSTUDENT)
                studPerTable.add(board.getStudentsAtTable(color));
        }

        professors = parameters.getProfessorsByPlayer(playerId);            //Get professors



        PlayerBean bean = new PlayerBean(nickname, playerId, leader, teamColor, numTowers,
                studAtEntrance,studPerTable,professors, Assistants,assistantPlayed, turn);

        if(turn != 0)
            bean.setTurn(turn);

        return bean;
    }

    public void addEntrance(StudentEnum student){
        board.addToEntrance(student);
        alert();
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

}
