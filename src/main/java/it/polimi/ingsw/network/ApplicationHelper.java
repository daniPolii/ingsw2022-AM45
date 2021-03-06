package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameRuleEnum;
import it.polimi.ingsw.model.StudentEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationHelper {

    /**
     * Gets the int value from the read Object.<br>
     * The field must be an Object that already contains an integer
     * @param readField the read Object
     * @return the int value parsed from the object
     */
    public static int getIntFromBrokerField(Object readField){
        Double dRead = (Double) readField;
        return dRead.intValue();
    }

    public static int[] getIntArrayFromBrokerField(Object readField){
        List<Integer> integerList = getIntListFromBrokerField(readField);
        return integerList.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * Gets a list of integers from the read field.<br>
     * The field must be an Object that already contains a list of integers in some form
     * @param readField the read Object
     * @return the int list parsed from the object, or an empty list if the parsing was unsuccessful
     */
    public static List<Integer> getIntListFromBrokerField(Object readField) {
        try{
            List<Object> readList = (List<Object>) readField;
            return getIntListFromBrokerField(readList);
        } catch (ClassCastException e){}  // I didn't think gson would make me do this

        try{
            Object[] readList2 = (Object[]) readField;
            return getIntListFromBrokerField(readList2);
        } catch (ClassCastException e){}

        try{
            double[] readList3 = (double[]) readField;
            return getIntListFromBrokerField(readList3);
        } catch (ClassCastException e){}

        return new ArrayList<Integer>();
    }

    /**
     * Gets a list of integers from the read field.<br>
     * The field must be an Object that already contains a list of integers
     * @param readField the read array of doubles
     * @return the int list parsed from the object
     */
    public static List<Integer> getIntListFromBrokerField(double[] readField) {
        List<Integer> retList = new ArrayList<>();
        Double dRead;
        for(double d : readField){
            dRead = (Double) d;
            retList.add(dRead.intValue());
        }
        return retList;
    }

    /**
     * Gets a list of integers from the read field.<br>
     * The field must be an Object that already contains a list of integers
     * @param readField the read array of objects
     * @return the int list parsed from the object
     */
    public static List<Integer> getIntListFromBrokerField(Object[] readField) {
        List<Integer> retList = new ArrayList<>();
        Double dRead;
        for(Object d : readField){
            dRead = (Double) d;
            retList.add(dRead.intValue());
        }
        return retList;
    }

    /**
     * Gets a list of integers from the read field.<br>
     * The field must be an Object that already contains a list of integers
     * @param readField the read list of objects
     * @return the int list parsed from the object
     */
    public static List<Integer> getIntListFromBrokerField(List<Object> readField) {

        List<Integer> retList = new ArrayList<>();
        Double dRead;
        for(Object o : readField){
            dRead = (Double) o;
            retList.add(dRead.intValue());
        }
        return retList;
    }

    public static boolean isInt(String inputString) {
        try{
            Integer.parseInt(inputString);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    /**
     * Checks whether the input is the name of a GameRuleEnum instance
     * @param inputString the input string
     * @return true if this string exactly matches one of the GameRuleEnum objects
     */
    public static boolean isGameRuleEnum(String inputString) {
        try{
            GameRuleEnum.valueOf(inputString);
        } catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

    /**
     * Checks whether the input is the name of a StudentEnum instance
     * @param inputString the input string
     * @return true if this string exactly matches one of the StudentEnum objects
     */
    public static boolean isStudentEnum(String inputString) {
        try{
            StudentEnum.valueOf(inputString);
        } catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

    /**
     * Checks whether the input can represent an int array ( int[] )
     * This representation uses square brackets ( [] ) as array delimiters and
     * commas ( , ) as element separators <br>
     * No spaces are allowed
     * No empty arrays allowed
     * @param inputString the input string
     * @return true if the string represents an int array
     */
    public static boolean isIntArray(String inputString) {
        String[] splitResult;

        if(!isAnArray(inputString)) return false;
        if(!correctExternalSeparators(inputString)) return false;

        splitResult = stripBracketAndSplit(inputString);


        for(int position = 0; position < splitResult.length; position++){ // Check that each of the values is an integer
            if(!isInt(splitResult[position])) return false;
        }
        return true;
    }


    /**
     * Checks whether the input can represent a StudentEnum array ( StudentEnum[] )
     * This representation uses square brackets ( [] ) as array delimiters and
     * commas ( , ) as element separators <br>
     * @param inputString the input string
     * @return true if the string represents a StudentEnum array
     */
    public static boolean isStudentEnumArray(String inputString) {
        String[] splitResult;
        if(!isAnArray(inputString)) return false;
        splitResult = stripBracketAndSplit(inputString);
        for(int position = 0; position < splitResult.length; position++){
            if(!isStudentEnum(splitResult[position])) return false;
        }
        return true;
    }

    private static String[] stripBracketAndSplit(String inputString) {
        return inputString
               .substring(1, inputString.length()-1) // Removing the brackets
               .split(",");
    }

    private static boolean correctExternalSeparators(String inputString) {
        //first and last are brackets
        if(inputString.substring(1,inputString.length()-1).equals("")) return true;
        else if(inputString.charAt(1) == ',' || inputString.charAt(inputString.length()-2) == ',') return false;
        return true;
    }

    private static boolean isAnArray(String inputString) {
        if(inputString.length() < 2) return false;
        if(inputString.charAt(0) != '[' ||
                inputString.charAt(inputString.length()-1) != ']') return false;
        return true;
    }

    /**
     * Get an integer from a string.
     * The string must represent an integer
     * @return the int that the string represented
     */
    public static int getIntFromString(String s) {
        return Integer.parseInt(s);
    }

    /**
     * Get a student enum from a string.
     * The string must represent an student enum
     * @return the student enum that the string represented
     */
    public static StudentEnum getStudentEnumFromString(String s) {
        return StudentEnum.valueOf(s);
    }

    /**
     * Get a student enum list from a string.
     * The string must represent a student enum list
     * @return the student enum list that the string represented
     */
    public static List<StudentEnum> getStudentEnumListFromString(String s) {
        String[] splitString = stripBracketAndSplit(s);
        return Arrays.stream(splitString)
                .map(ApplicationHelper::getStudentEnumFromString)
                .toList();
    }

    /**
     * Get an int list from a string.
     * The string must represent an list
     * @return the int list that the string represented
     */
    public static List<Integer> getIntListFromString(String s) {
        String[] splitString = stripBracketAndSplit(s);
        return Arrays.stream(splitString)
                .map(ApplicationHelper::getIntFromString)
                .toList();
    }
}
