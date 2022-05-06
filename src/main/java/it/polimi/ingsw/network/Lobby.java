package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameRule;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private final GameRule gameType;
    private List<Integer> playersReady; //identified by idUser
    private List<Integer> players;
    private int emptySeats;
    private Integer host;

    public Lobby(GameRule gameType){

        this.gameType = gameType;
        players = new ArrayList<>();
        host = null;
        playersReady = new ArrayList<>();
        switch (this.gameType){
            case SIMPLE_2, ADVANCED_2 ->  emptySeats = 2;
            case SIMPLE_3, ADVANCED_3 -> emptySeats = 3;
            case SIMPLE_4, ADVANCED_4 -> emptySeats = 4;
        }
    }

    public boolean isFull(){
        return emptySeats == 0;
    }

    /**
     * Adds a player to the ready players' list
     * @param idUser The idUser of the player to add (must be in the lobby)
     */
    public void addReady(int idUser){
        if (players.contains(idUser)) playersReady.add(idUser);
        else ;//maybe handle case of idUser not present
    }

    /**
     * Removes a player from the ready players' list
     * @param idUser The idUser of the player to remove
     */
    public void removeReady(int idUser){

        Integer integer = idUser;
        playersReady.remove(integer);
    }

    public GameRule getGameType(){
        return gameType;
    }


    /**
     * Removes a player from the Lobby.
     * If he was the host, the role is reassigned.
     * @param idUser The idUser of the player to remove (must be in the lobby)
     */
    public synchronized void removePlayer(int idUser){

        if (players.contains(idUser)) {
            Integer integer = idUser;
            players.remove(integer);
            playersReady.remove(integer);
            assignHost();
        }
        //todo maybe delete lobby when players reach 0
    }

    /**
     * Adds a player to the Lobby.
     * If he's the first player added, it becomes the host.
     * @param idUser The idUser of the player to add
     */
    public synchronized void addPlayer(int idUser){

        players.add(idUser);

        assignHost();
    }

    /**
     *
     */
    private void assignHost(){

        if (players.size() == 0) host = null;

        if (!players.contains(host)) host = null;

        if (host == null) host = players.get(0);
    }

    /**
     *
     * @return the host of this lobby
     */
    public Integer getHost() {
        return host;
    }

}
