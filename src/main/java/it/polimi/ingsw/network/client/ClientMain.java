

//TODO move remaining classes to the view

package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.GUI.GUIApplication;
import it.polimi.ingsw.view.UserInterface;

/**
 * Entry point for the client application, instances
 * an actual client
 */
public class ClientMain {

    private ClientReceiver receiver;
    private UserInterface userInterface;
    private ClientSender sender;
    private InitialConnector initialConnector;

    /**
     * @param hostname the host to connect to
     * @param portNumber the port of the server to connect to
     * @param guiMode false for CLI, true for GUI
     */
    public ClientMain(String hostname, int portNumber, boolean guiMode){

        initialConnector = new InitialConnector(hostname, portNumber);
        receiver = new ClientReceiver(initialConnector);
        if(guiMode) {
            //todo the GUI will need to get this connector at some point
//            userInterface = new GUIApplication(initialConnector);
        }
        else {
            userInterface = new CLI(initialConnector);
        }
        sender = new ClientSender(initialConnector);
    }

    public void startApplication(){
        initialConnector.setSender(sender);
        initialConnector.setReceiver(receiver);

        receiver.setUI(userInterface);
        userInterface.setSender(sender);

        userInterface.startInterface();
    }

}
