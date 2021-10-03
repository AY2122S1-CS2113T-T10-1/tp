package parser;

import command.PurgeCommand;
import command.medicine.AddCommand;
import command.medicine.DeleteCommand;
import command.medicine.ListCommand;
import errors.InvalidCommand;
import inventory.Stock;
import ui.Ui;

import java.util.ArrayList;
import java.util.HashMap;

import static command.CommandList.ADD;
import static command.CommandList.ADD_ORDER;
import static command.CommandList.ARCHIVE;
import static command.CommandList.DELETE;
import static command.CommandList.DELETE_ORDER;
import static command.CommandList.DISPENSE;
import static command.CommandList.EXIT;
import static command.CommandList.HELP;
import static command.CommandList.LIST;
import static command.CommandList.LIST_ORDERS;
import static command.CommandList.PURGE;
import static command.CommandList.QUERY;
import static command.CommandList.RECEIVE_ORDER;
import static command.CommandList.UNDO;
import static command.CommandList.UPDATE;
import static command.CommandList.UPDATE_ORDER;


/**
 * Helps to parse the commands given by the user as well as extract the parameters provided.
 */

public class Parser {

    private static final String DELIMITER = "/";

    /**
     * Processes the user input into a Command Object.
     *
     * @param ui        Reference to the UI object passed by Main to print messages.
     * @param userInput Input provided by user.
     * @param stocks    Arraylist of all stocks.
     * @return A boolean value indicating isExit.
     * @throws InvalidCommand If a command does not exist.
     */
    public static boolean processCommand(Ui ui, String userInput, ArrayList<Stock> stocks) throws InvalidCommand {
        String[] userCommand = parseCommand(userInput);
        String command = userCommand[0];
        String commandParameters = userCommand[1];
        HashMap<String, String> parameters = new HashMap<>();
        if (!commandParameters.equals("")) {
            parameters = parseParameters(commandParameters);
        }
        switch (command) {
        case ADD:
            new AddCommand().execute(ui, parameters, stocks);
            break;
        case ADD_ORDER:
            break;
        case ARCHIVE:
            break;
        case DELETE:
            new DeleteCommand().execute(ui, parameters, stocks);
            break;
        case DELETE_ORDER:
            break;
        case DISPENSE:
            break;
        case EXIT:
            return true;
        case HELP:
            break;
        case LIST:
            new ListCommand().execute(ui, parameters, stocks);
            break;
        case LIST_ORDERS:
            break;
        case PURGE:
            new PurgeCommand().execute(ui, parameters, stocks);
            break;
        case QUERY:
            break;
        case RECEIVE_ORDER:
            break;
        case UNDO:
            break;
        case UPDATE:
            break;
        case UPDATE_ORDER:
            break;
        default:
            throw new InvalidCommand();
        }
        return false;
    }

    /**
     * Splits the user input into command and command parameters.
     *
     * @param userInput String input from user.
     * @return Array of string with size 2 with index 0 representing the command and index 1 representing the
     *     command parameters.
     */
    public static String[] parseCommand(String userInput) {
        String[] splitCommand = new String[2];
        // Splits user input by spaces
        String[] userInputSplit = userInput.split("\\s+", 2);
        String command = userInputSplit[0].toUpperCase();
        String commandParameters = "";
        if (userInputSplit.length > 1) { // Ensure command parameter exists
            commandParameters = userInputSplit[1];
        }
        return new String[]{command, commandParameters};
    }

    /**
     * Returns all the parameters passed entered as a hashmap.
     *
     * @param parameterString String of parameters.
     * @return HashMap with parameter as key and parameter contents as value.
     */
    public static HashMap<String, String> parseParameters(String parameterString) {
        HashMap<String, String> parameters = new HashMap<>();

        String[] parameterSplit = parameterString.split("\\s+"); // Split by space

        String commandParameter = "";
        StringBuilder parameterContents = new StringBuilder();

        for (String s : parameterSplit) {
            if (s.contains(DELIMITER)) {
                if (!commandParameter.equals("")) { // Ensure it is not the first iteration
                    // Add to hashmap before resetting values
                    parameters.put(commandParameter, parameterContents.toString());
                }

                parameterContents = new StringBuilder(); // Reset the values
                String[] commandSplit = s.split(DELIMITER);
                commandParameter = commandSplit[0].toUpperCase();

                if (commandSplit.length > 1) {
                    parameterContents = new StringBuilder(commandSplit[1]);
                }
            } else { // Add the rest of the string
                parameterContents.append(" ").append(s);
            }
        }
        parameters.put(commandParameter, parameterContents.toString()); // Add to hashmap for the last parameter
        return parameters;
    }
}
