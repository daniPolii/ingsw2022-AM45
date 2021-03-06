package it.polimi.ingsw.network;

import com.google.gson.internal.LinkedTreeMap;
import it.polimi.ingsw.model.StudentEnum;
import it.polimi.ingsw.model.TeamEnum;
import it.polimi.ingsw.model.WizardEnum;
import it.polimi.ingsw.model.assistantCards.Assistant;
import it.polimi.ingsw.model.beans.*;
import it.polimi.ingsw.model.player.PlayerEnum;
import it.polimi.ingsw.model.game.PhaseEnum;
import it.polimi.ingsw.view.GameInitBean;
import it.polimi.ingsw.view.LobbyBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Translates from a LinkedTreeMap to a bean of the correct type
 * This class is fairly fragile, as it needs updating every time an attribute
 * name changes
 * All translating methods in this class take a map from the gson serialization
 * and return a bean of the associated type
 */
public class BeanTranslator {

    /**
     * Takes a map from the gson serialization and returns a LobbyBean
     * @param map A gson map containing the mapping String -> Object, where the string is the name of
     *            a parameter in the bean
     * @return The translated LobbyBean, or null if some fields were null during the translating process
     */
    public static LobbyBean deserializeLobbyBean(LinkedTreeMap<String, Object> map){
        List<String> nicknames;
        List<Boolean> readyPlayers;
        boolean gameStarted;
        int host;
        try {
            nicknames = (List<String>) map.get("nicknames");
            readyPlayers = (List<Boolean>) map.get("readyPlayers");
            gameStarted = (boolean) map.get("gameStarted");
            host = ApplicationHelper.getIntFromBrokerField( map.get("host"));
        } catch (NullPointerException e){
            return null;
        }
        return new LobbyBean(nicknames, readyPlayers, gameStarted, host);
    }

    /**
     * Takes a map from the gson serialization and returns a GameInitBean
     * @param map A gson map containing the mapping String -> Object, where the string is the name of
     *            a parameter in the bean
     * @return The translated GameInitBean, or null if some fields were null during the translating process
     */
    public static GameInitBean deserializeGameInitBean(LinkedTreeMap<String, Object> map){
        List<TeamEnum> chosenColors;
        List<WizardEnum> chosenWizards;
        try{
            chosenColors = toListOfEnum(TeamEnum.class, (ArrayList<String>) map.get("availableColors"));
            chosenWizards = toListOfEnum(WizardEnum.class, (ArrayList<String>) map.get("availableWizards"));
        } catch (NullPointerException e){
            return null;
        }
        return new GameInitBean(chosenColors,chosenWizards);
    }

    private static GameBoardBean deserializeGameBoardBean(LinkedTreeMap<String, Object> map) {
        List<Integer> idIslandGroups = ApplicationHelper.getIntListFromBrokerField(map.get("idIslandGroups"));
        List<Integer> idAssistantsPlayed = ApplicationHelper.getIntListFromBrokerField(map.get("idAssistantsPlayed"));
        List<Integer> idPlayers = ApplicationHelper.getIntListFromBrokerField(map.get("idPlayers"));
        Integer currentPlayerId = ApplicationHelper.getIntFromBrokerField(map.get("currentPlayerId"));
        Integer turn = ApplicationHelper.getIntFromBrokerField(map.get("turn"));
        PhaseEnum phase = PhaseEnum.fromObjectToEnum(map.get("phase"));
        return new GameBoardBean(idIslandGroups,idAssistantsPlayed,idPlayers,currentPlayerId,turn,phase);
    }

    private static AdvancedGameBoardBean deserializeAdvancedGameBoardBean(LinkedTreeMap<String, Object> map){
        GameBoardBean simpleGameBoardBean = deserializeGameBoardBean(map);
        int numGameCoins = ApplicationHelper.getIntFromBrokerField(map.get("numGameCoins"));
        List<Integer> idCharacterCards = ApplicationHelper.getIntListFromBrokerField(map.get("idCharacterCards"));
        return new AdvancedGameBoardBean(
                simpleGameBoardBean.getIdIslandGroups(),
                simpleGameBoardBean.getIdAssistantsPlayed(),
                simpleGameBoardBean.getIdPlayers(),
                simpleGameBoardBean.getCurrentPlayerId(),
                simpleGameBoardBean.getTurn(),
                simpleGameBoardBean.getPhase(), numGameCoins, idCharacterCards);
    }

    private static CloudBean deserializeCloudBean(LinkedTreeMap<String, Object> map){
        int idCloud = ApplicationHelper.getIntFromBrokerField(map.get("idCloud"));
        List<StudentEnum> students = toListOfEnum(StudentEnum.class, (ArrayList<String>)map.get("students"));
        return new CloudBean(idCloud, students);
    }

    private static CharacterCardBean deserializeCharacterCardBean(LinkedTreeMap<String, Object> map) {
        int id = ApplicationHelper.getIntFromBrokerField(map.get("id"));
        String name = (String) map.get("name");
        int cost = ApplicationHelper.getIntFromBrokerField(map.get("cost"));
        String description = (String) map.get("description");
        List<StudentEnum> students = null;
        int numBlocks = -1;
        if(fieldPresent(map.get("numBlocks"))){
            numBlocks = ApplicationHelper.getIntFromBrokerField(map.get("numBlocks"));
        }
        if(fieldPresent(map.get("students"))) {
            students = toListOfEnum(StudentEnum.class, (ArrayList<String>) map.get("students"));
        }
        boolean hasBeenUsed = (boolean)map.get("hasBeenUsed");
        CharacterCardBean beanToReturn = new CharacterCardBean(id,name, description, students, cost, hasBeenUsed);
        if(numBlocks != -1) beanToReturn.setNumBlocks(numBlocks);
        return beanToReturn;
    }

    private static IslandGroupBean deserializeIslandGroupBean(LinkedTreeMap<String, Object> map) {
        int idIslandGroup = ApplicationHelper.getIntFromBrokerField(map.get("idIslandGroup"));
        List<Integer> idIslands = ApplicationHelper.getIntListFromBrokerField(map.get("idIslands"));
        List<StudentEnum> studentsOnIsland = toListOfEnum(StudentEnum.class, (ArrayList<String>)map.get("studentsOnIsland"));
        boolean isPresentMN = (boolean)map.get("isPresentMN");
        TeamEnum towersColor = TeamEnum.fromObjectToEnum(map.get("towersColor"));
        int idPrevIslandGroup = ApplicationHelper.getIntFromBrokerField(map.get("idPrevIslandGroup"));
        int idNextIslandGroup = ApplicationHelper.getIntFromBrokerField(map.get("idNextIslandGroup"));
        return new IslandGroupBean(idIslandGroup,idIslands,studentsOnIsland,isPresentMN,towersColor,idPrevIslandGroup,idNextIslandGroup);
    }

    private static AdvancedIslandGroupBean deserializeAdvancedIslandGroupBean(LinkedTreeMap<String, Object> map){
        IslandGroupBean islandGroupBean = deserializeIslandGroupBean(map);
        int numBlockTiles = ApplicationHelper.getIntFromBrokerField(map.get("numBlockTiles"));
        return new AdvancedIslandGroupBean(
                islandGroupBean.getIdIslandGroup(),
                islandGroupBean.getIdIslands(),
                islandGroupBean.getStudentsOnIsland(),
                islandGroupBean.isPresentMN(),
                islandGroupBean.getTowersColor(),
                numBlockTiles,
                islandGroupBean.getIdPrevIslandGroup(),
                islandGroupBean.getIdNextIslandGroup()
        );
    }

    private static ErrorBean deserializeErrorBean(LinkedTreeMap<String, Object> map) {
        String error = (String) map.get("error");
        return new ErrorBean(error);
    }

    private static AdvancedPlayerBean deserializeAdvancedPlayerBean(LinkedTreeMap<String, Object> map) {
        PlayerBean playerBean = deserializePlayerBean(map);
        int numCoins = ApplicationHelper.getIntFromBrokerField(map.get("numCoins"));

        return new AdvancedPlayerBean(
                playerBean.getNickname(),
                playerBean.getPlayerId(),
                playerBean.isLeader(),
                playerBean.getTowerColor(),
                playerBean.getNumTowers(),
                playerBean.getStudentsAtEntrance(),
                playerBean.getStudentsPerTable(),
                playerBean.getProfessors(),
                playerBean.getAssistants(),
                numCoins,
                playerBean.getAssistantPlayed(),
                playerBean.getTurn()
        );
    }

    private static PlayerBean deserializePlayerBean(LinkedTreeMap<String, Object> map) {
        String nickname = (String) map.get("nickname");
        PlayerEnum playerId = PlayerEnum.fromObjectToEnum(map.get("playerId"));
        boolean leader = (boolean) map.get("leader");
        TeamEnum towerColor = TeamEnum.fromObjectToEnum(map.get("towerColor"));
        int numTowers = ApplicationHelper.getIntFromBrokerField(map.get("numTowers"));
        List<StudentEnum> studentsAtEntrance = toListOfEnum(StudentEnum.class, (ArrayList<String>) map.get("studentsAtEntrance"));
        List<Integer> studentsPerTable = ApplicationHelper.getIntListFromBrokerField(map.get("studentsPerTable"));
        List<Assistant> assistants = getListOfAssistantsFromObject((ArrayList<LinkedTreeMap<String, Object>>)map.get("assistants"));
        List<StudentEnum> professors = toListOfEnum(StudentEnum.class, (ArrayList<String>) map.get("professors"));
        int turn = ApplicationHelper.getIntFromBrokerField(map.get("turn"));

        Assistant assistantPlayed = null;
        //todo there might be problems with this null value
        if(fieldPresent(map.get("assistantPlayed"))){
            assistantPlayed = getAssistantFromObject((LinkedTreeMap<String, Object>) map.get("assistantPlayed"));
        }
        return new PlayerBean(nickname, playerId, leader, towerColor, numTowers, studentsAtEntrance, studentsPerTable, professors, assistants, assistantPlayed, turn);
    }

    private static Assistant getAssistantFromObject(LinkedTreeMap<String, Object> map) {
        int id = ApplicationHelper.getIntFromBrokerField(map.get("id"));
        int motherNatureSteps = ApplicationHelper.getIntFromBrokerField(map.get("motherNatureSteps"));
        int turnOrder = ApplicationHelper.getIntFromBrokerField(map.get("turnOrder"));
        return new Assistant(id, motherNatureSteps, turnOrder);
    }

    private static List<Assistant> getListOfAssistantsFromObject(ArrayList<LinkedTreeMap<String, Object>> list) {
        List<Assistant> assistants = new ArrayList<>();
        for(LinkedTreeMap<String, Object> assistant : list){
            assistants.add(getAssistantFromObject(assistant));
        }
        return assistants;
    }

    public static VirtualViewBean deserializeViewBean(LinkedTreeMap<String, Object> map) {
        List<CloudBean> cloudBeans = null;
        List<CharacterCardBean> characterCardBeans = null;
        List<IslandGroupBean> islandGroupBeans = null;
        List<AdvancedIslandGroupBean> advancedIslandGroupBeans = null;
        List<PlayerBean> playerBeans = null;
        List<AdvancedPlayerBean> advancedPlayerBeans = null;
        List<ErrorBean> errorBeans = null;
        GameBoardBean gameBoardBean = null;
        AdvancedGameBoardBean advancedGameBoardBean = null;

        if(fieldPresent(map.get("cloudBeans"))) {
            cloudBeans = new ArrayList<>();
            for (LinkedTreeMap<String, Object> element : (ArrayList<LinkedTreeMap<String, Object>>) map.get("cloudBeans")) {
                cloudBeans.add(deserializeCloudBean(element));
            }
        }
        if(fieldPresent(map.get("characterCardBeans"))) {
            characterCardBeans = new ArrayList<>();
            for (LinkedTreeMap<String, Object> element : (ArrayList<LinkedTreeMap<String, Object>>) map.get("characterCardBeans")) {
                characterCardBeans.add(deserializeCharacterCardBean(element));
            }
        }
        if(fieldPresent(map.get("islandGroupBeans"))) {
            islandGroupBeans = new ArrayList<>();
            for (LinkedTreeMap<String, Object> element : (ArrayList<LinkedTreeMap<String, Object>>) map.get("islandGroupBeans")) {
                islandGroupBeans.add(deserializeIslandGroupBean(element));
            }
        }
        if(fieldPresent(map.get("advancedIslandGroupBeans"))) {
            advancedIslandGroupBeans = new ArrayList<>();
            for (LinkedTreeMap<String, Object> element : (ArrayList<LinkedTreeMap<String, Object>>) map.get("advancedIslandGroupBeans")) {
                advancedIslandGroupBeans.add(deserializeAdvancedIslandGroupBean(element));
            }
        }
        if(fieldPresent(map.get("playerBeans"))) {
            playerBeans = new ArrayList<>();
            for (LinkedTreeMap<String, Object> element : (ArrayList<LinkedTreeMap<String, Object>>) map.get("playerBeans")) {
                playerBeans.add(deserializePlayerBean(element));
            }
        }
        if(fieldPresent(map.get("advancedPlayerBeans"))) {
            advancedPlayerBeans = new ArrayList<>();
            for (LinkedTreeMap<String, Object> element : (ArrayList<LinkedTreeMap<String, Object>>) map.get("advancedPlayerBeans")) {
                advancedPlayerBeans.add(deserializeAdvancedPlayerBean(element));
            }
        }
        if(fieldPresent(map.get("errorBeans"))) {
            errorBeans = new ArrayList<>();
            for (LinkedTreeMap<String, Object> element : (ArrayList<LinkedTreeMap<String, Object>>) map.get("errorBeans")) {
                errorBeans.add(deserializeErrorBean(element));
            }
        }
        if(fieldPresent(map.get("gameBoardBean"))){
            gameBoardBean = deserializeGameBoardBean( (LinkedTreeMap<String, Object>) map.get("gameBoardBean"));
        }
        if(fieldPresent(map.get("advancedGameBoardBean"))){
            advancedGameBoardBean = deserializeAdvancedGameBoardBean((LinkedTreeMap<String, Object>) map.get("advancedGameBoardBean"));
        }
        return new VirtualViewBean(cloudBeans,characterCardBeans,islandGroupBeans,advancedIslandGroupBeans,
                playerBeans, advancedPlayerBeans,errorBeans,gameBoardBean,advancedGameBoardBean);
    }


    /**
     * Checks whether the field in the received message is meaningful
     * This should account for the possibility that the field doesn't exist AND the case in which the
     * field exists but the value is null
     * @param field the field to check for nullity
     * @return true if the field is not null
     */
    private static boolean fieldPresent(Object field) {
        return field != null;
    }


    /**
     * Translates the list from a gson map into a list of appropriate enums
     * @param enumClass the type of enum one wishes to translate to
     * @param list the list (an ArrayList< String >) to get the data from
     * @param <T> A generic enum
     * @return a list of enums of type T reflecting the list structure
     */
    private static <T extends Enum<T>> List<T> toListOfEnum (Class<T> enumClass, ArrayList<String> list){
        List<T> retList = new ArrayList<>();
        for(String element : list){
            retList.add(T.valueOf(enumClass, element));
        }
        return retList;
    }

}
