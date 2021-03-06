package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameRuleEnum;
import it.polimi.ingsw.model.TeamEnum;
import it.polimi.ingsw.model.beans.GameElementBean;
import it.polimi.ingsw.model.beans.VirtualViewBean;
import it.polimi.ingsw.model.game.PhaseEnum;
import it.polimi.ingsw.network.Bean;
import it.polimi.ingsw.network.client.ClientSender;
import it.polimi.ingsw.network.CommandEnum;

/**
 * This class is an interface to what the client will see, it contains a NetworkManager which
 * takes requests from the View and sends them to the server
 */
public interface UserInterface {

    void addBean(GameElementBean bean);

    Bean removeBean(int index);

    void clearBeans();

    //todo? wrap these in a client state like with the server?
    void addCommand(CommandEnum command);

    CommandEnum removeCommand(int index);

    void clearCommands();

    /**
     * Sets the message sender for this UI
     * @param sender the sender that will communicate with the server
     */
    void setSender(ClientSender sender);

    /**
     * Shows a welcome screen for the game
     */
    void showWelcomeScreen();

    /**
     * Shows a goodbye screen for the game
     */
    void showGoodbyeScreen();

    /**
     * Shows the login screen;
     * This screen should allow the player to enter their desired username
     */
    void showLoginScreen();

    /**
     * To show in case the login procedure failed
     * It will then call showLoginScreen
     */
    void showLoginScreenFailure();

    /**
     * To show in case the login procedure succeeded
     * It will then call showGameruleSelection
     */
    void showSuccessLoginScreen();

    /**
     * Shows the selection of the game rules
     * Here the user can select whether the game is simple or advanced
     * and the number of players to play with
     */
    void showGameruleSelection();

    /**
     * This method is called if the user was able to join a lobby successfully
     * It will then call showLobby
     */
    void showSuccessJoiningLobby();

    /**
     * This method is called if the user was not able to join a lobby
     * It will then call showGameruleSelection
     */
    void showErrorJoiningLobby();

    /**
     * Once a lobby with the given rules is found,
     * this screen will let the user decide if they're ready,
     * start the game if they're the host and see how many players are ready
     * in the lobby
     */
    void showLobby();

    /**
     * Shows that the status was successfully received by the server
     * @param status the status that the user is currently in
     *               true if the choice was "ready"
     */
    void showSuccessReadyStatus(boolean status);

    /**
     * Shows that the status was not correctly received by the server
     * @param status the status that the user tried to send
     *               true if the choice was "ready"
     */
    void showErrorReadyStatus(boolean status);

    /**
     * Shows that the request to start the game was successful
     */
    void showSuccessStartGame();

    /**
     * Shows that the request to start the game wasn't successful
     */
    void showErrorStartGame();

    /**
     * Show that the lobby was successfully left
     */
    void showSuccessLeaveLobby();

    /**
     * Show that there was a problem leaving the lobby
     */
    void showErrorLeaveLobby();

    /**
     * This screen lets the user choose the wizard and tower color
     * for the game
     */
    void showTowerAndWizardSelection();

    /**
     * Shows an error with the color selection
     * @param color the color chosen
     */
    void showErrorSelectingColor(String color);

    /**
     * Shows success with the color selection
     * @param color the color chosen
     */
    void showSuccessSelectingColor(String color);

    /**
     * SShows an error with the wizard selection
     * @param wizard the wizard chosen
     */
    void showErrorSelectingWizard(String wizard);

    /**
     * Shows success with the wizard selection
     * @param wizard the wizard chosen
     */
    void showSuccessSelectingWizard(String wizard);

    /**
     * This screen will be used to get commands from the client
     * It will orchestrate when to show updates, polled with the updateAvailable flag
     */
    void showMainGameInterface();

    /**
     * Shows the user that it's their turn to play
     * @param phase the phase in which the game is currently in
     */
    void showItsYourTurn(PhaseEnum phase);

    /**
     * Shows that there has been an error with the command that was input
     * @param error the error message
     */
    void showGameCommandError(String error);

    /**
     * Shows that there has been an error with the command that was input
     */
    void showGameCommandError();

    /**
     * Shows that the command went through successfully
     */
    void showGameCommandSuccess();

    /**
     * Initializes and starts the application interface
     */
    void startInterface();

    /**
     * This screen will be shown when an error occurred, the user will
     * then be brought back to the login screen
     */
    void showNetworkError();

    /**
     * Shows that a user disconnected
     * @param disconnectedUser the nickname of the user that disconnected
     */
    void showUserDisconnected(String disconnectedUser);

    // Asynchronous methods

    /**
     * Sends the updated lobby bean to the UI
     * @param lobbyBean the updated lobby bean received from the server
     */
    void printLobby(LobbyBean lobbyBean);

    /**
     * Sends the updated game init bean to the UI
     * @param gameInitBean the updated game init bean received from the server
     */
    void printGameInitInfo(GameInitBean gameInitBean);

    /**
     * The main game interface, will be called by an asynchronous update whenever the game state changes
     */
    void printGameInterface(VirtualViewBean virtualView);


    // Setters

    void setChosenNickname(String nickname);

    void setGameMode(GameRuleEnum gameMode);

    void setTeamColor(String teamColor);

    void setWizard(String wizard);

    void setInLobby(boolean inLobby);

    /**
     * Used to notify the UI that the lobby has started and that any previous methods
     * should return (via the lobbyStarting flag which is set to true)
     */
    void setLobbyStarting();

    /**
     * Used to notify the UI that the actual game has started and that any previous methods
     * should return (via the gameStarting flag which is set to true)
     */
    void setGameStarting();

    /**
     * Set to true in case something ends the game, this will interrupt the main game interface
     * @param interrupted true if there has been a problem and the game ended
     */
    void setGameInterrupted(boolean interrupted);

    /**
     * Set to true whenever a view update is available (called via the asynchronous method)
     * @param available true if an updated view has been received
     */
    void setUpdateAvailable(boolean available);

    /**
     * Set to true whenever it's this user's turn (called via the asynchronous method)
     * @param isYourTurn true if it's this player's turn
     */
    void setYourTurn(boolean isYourTurn);

    /**
     * Sets the game's winner to the specified team
     * @param winner the team that won the game
     */
    void setGameWon (TeamEnum winner);

    /**
     * Sets the amount of game elements of various types that need to be selected
     * to activate the previously selected card
     */
    void setCardRequirements(int islandsRequired, int studentsOnCardRequired, int studentsAtEntranceRequired, int colorsRequired);


}
