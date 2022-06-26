package it.polimi.ingsw.view.GUI.handlingToolbox;

import it.polimi.ingsw.model.beans.AdvancedIslandGroupBean;
import it.polimi.ingsw.model.beans.AdvancedPlayerBean;
import it.polimi.ingsw.model.beans.IslandGroupBean;
import it.polimi.ingsw.model.islands.AdvancedIslandGroup;
import it.polimi.ingsw.network.CommandEnum;
import it.polimi.ingsw.network.client.ClientSender;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.*;

public class IslandHandlingToolbox implements HandlingToolbox{

    private List<EventHandler<MouseEvent>> onIslandClick;

    private Map<Integer, Integer> indexToId;

    private int motherNaturePos;

    private int maxMNSteps;

    //needed to re-allow all commands whenever the island group morphology changes
    private EnumSet<CommandEnum> allowedCommands = EnumSet.noneOf(CommandEnum.class);
    private ClientSender sender;

    /**
     * Provides the toolbox with the necessary information regarding the islands to keep providing the handling resources
     * @param islands The list containing all the relevant information about each island
     */
    public void updateAdvancedIslandGroups(List<AdvancedIslandGroupBean> islands){

        List<EventHandler<MouseEvent>> newOnIslandClick = new ArrayList<>();
        Map<Integer, Integer> newIndexToId = new HashMap<>();

        for (AdvancedIslandGroupBean island : islands){

            newOnIslandClick.add(NO_EFFECT);

            newIndexToId.put(islands.indexOf(island), island.getIdIslandGroup());

            if (island.isPresentMN()) motherNaturePos = islands.indexOf(island);
        }

        onIslandClick = newOnIslandClick;
        indexToId = newIndexToId;

        if (!allowedCommands.isEmpty()) {
            for (CommandEnum command : allowedCommands) {
                allowCommand(command, sender);
            }
        }
    }

    public void updateIslandGroups(List<IslandGroupBean> islands){

        List<AdvancedIslandGroupBean> adaptedIslands = new ArrayList<>();

        for (IslandGroupBean island:
             islands) {
            adaptedIslands.add(AdvancedIslandGroupBean.getPromotedBean(island));
        }

        updateAdvancedIslandGroups(adaptedIslands);
    }

    public void setMaxMNSteps(int steps){maxMNSteps = steps;}

    @Override
    public void allowCommand(CommandEnum command, ClientSender resourceProvider) {

        if (!resourceProvider.equals(sender)) sender = resourceProvider;

        allowedCommands.add(command);

        if (command == CommandEnum.PUT_IN_ISLAND) {
            int islandIndex = 0;

            for (EventHandler<MouseEvent> ignored: onIslandClick) {
                int finalIndex = islandIndex;
                onIslandClick.set(islandIndex, event -> new Thread(() -> resourceProvider.sendPutInIsland(indexToId.get(finalIndex))).start());
                islandIndex++;
            }
        }

        if (command == CommandEnum.SELECT_ISLAND_GROUP){

            int islandIndex = 0;

            for (EventHandler<MouseEvent> ignored: onIslandClick) {
                int finalIndex = islandIndex;
                onIslandClick.set(islandIndex, event -> new Thread(() -> resourceProvider.sendSelectIslandGroup(indexToId.get(finalIndex))).start());
                islandIndex++;
            }
        }

        if (command == CommandEnum.MOVE_MN){

            for (int pos = motherNaturePos + 1; pos <= motherNaturePos + maxMNSteps; pos++){

                int finalSteps = pos - motherNaturePos;
                onIslandClick.set(pos % onIslandClick.size(), event -> new Thread(() ->  resourceProvider.sendMoveMN(finalSteps)).start());
            }
        }
    }

    @Override
    public void disableCommand(CommandEnum command) {
        allowedCommands.remove(command);

        if (command == CommandEnum.PUT_IN_ISLAND ||
                command == CommandEnum.SELECT_ISLAND_GROUP ||
                command == CommandEnum.MOVE_MN){

            int index = 0;

            if (onIslandClick != null){
                for (EventHandler<MouseEvent> ignored : onIslandClick) onIslandClick.set(index++, DISABLED);
            }
        }

    }

    public EventHandler<MouseEvent> getOnIslandClick(int pos){
        return onIslandClick.get(pos);
    }
}
