package utilities.parser;

import command.Command;
import command.ExitCommand;
import command.HelpCommand;
import command.PurgeCommand;
import command.dispense.AddDispenseCommand;
import command.dispense.DeleteDispenseCommand;
import command.dispense.ListDispenseCommand;
import command.dispense.UpdateDispenseCommand;
import command.stock.AddStockCommand;
import command.stock.DeleteStockCommand;
import command.stock.ListStockCommand;
import command.stock.UpdateStockCommand;
import command.order.AddOrderCommand;
import command.order.DeleteOrderCommand;
import command.order.ListOrderCommand;
import command.order.UpdateOrderCommand;
import errors.InvalidCommand;
import utilities.ui.Ui;

import java.util.LinkedHashMap;

import static command.CommandList.ADD;
import static command.CommandList.ADD_DISPENSE;
import static command.CommandList.ADD_ORDER;
import static command.CommandList.ADD_STOCK;
import static command.CommandList.DELETE;
import static command.CommandList.DELETE_DISPENSE;
import static command.CommandList.DELETE_STOCK;
import static command.CommandList.DELETE_ORDER;
import static command.CommandList.EXIT;
import static command.CommandList.HELP;
import static command.CommandList.LIST;
import static command.CommandList.LIST_DISPENSE;
import static command.CommandList.LIST_STOCK;
import static command.CommandList.LIST_ORDER;
import static command.CommandList.PURGE;
import static command.CommandList.UPDATE;
import static command.CommandList.UPDATE_DISPENSE;
import static command.CommandList.UPDATE_STOCK;
import static command.CommandList.UPDATE_ORDER;
import static utilities.parser.Mode.DISPENSE;
import static utilities.parser.Mode.ORDER;
import static utilities.parser.Mode.STOCK;


/**
 * Helps to parse the commands given by the user as well as extract the parameters provided.
 */

public class CommandParser {
    public CommandParser(){
    }

    private static final String DELIMITER = "/";

    /**
     * Processes the user input into a Command Object.
     *
     * @param command          Input provided by user.
     * @param parametersString String parameter entered by user.
     * @param mode             The current mode of the program.
     * @return A Command object.
     * @throws InvalidCommand If a command does not exist.
     */
    public Command processCommand(String command, String parametersString, Mode mode) throws InvalidCommand {
        // Append user's command with mode
        if (command.equals(ADD) || command.equals(LIST) || command.equals(UPDATE)
                || command.equals(DELETE)) {
            command = command + mode.name().toLowerCase();
        }

        LinkedHashMap<String, String> parameters = parseParameters(parametersString);

        switch (command) {
        case ADD_DISPENSE:
            return new AddDispenseCommand(parameters);
        case ADD_STOCK:
            return new AddStockCommand(parameters);
        case ADD_ORDER:
            return new AddOrderCommand(parameters);
        /*case ARCHIVE:
            break;*/
        case DELETE_DISPENSE:
            return new DeleteDispenseCommand(parameters);
        case DELETE_STOCK:
            return new DeleteStockCommand(parameters);
        case DELETE_ORDER:
            return new DeleteOrderCommand(parameters);
        case EXIT:
            return new ExitCommand();
        case HELP:
            return new HelpCommand();
        case LIST_DISPENSE:
            return new ListDispenseCommand(parameters);
        case LIST_STOCK:
            return new ListStockCommand(parameters);
        case LIST_ORDER:
            return new ListOrderCommand(parameters);
        case PURGE:
            return new PurgeCommand();
        /*case RECEIVE_ORDER:
            break;
        case UNDO:
            break;*/
        case UPDATE_STOCK:
            return new UpdateStockCommand(parameters);
        case UPDATE_DISPENSE:
            return new UpdateDispenseCommand(parameters);
        case UPDATE_ORDER:
            return new UpdateOrderCommand(parameters);
        default:
            throw new InvalidCommand();
        }
    }

    /**
     * Splits the user input into command and command parameters.
     *
     * @param userInput String input from user.
     * @return Array of string with size 2 with index 0 representing the command and index 1 representing the
     *     command parameters.
     */
    public String[] parseCommand(String userInput) {
        // Splits user input by spaces
        String[] userInputSplit = userInput.split("\\s+", 2);

        assert (userInputSplit.length <= 2) : "Command extraction failed! More than 2 values were returned!";

        String command = userInputSplit[0].toLowerCase();
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
    public LinkedHashMap<String, String> parseParameters(String parameterString) {
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();

        if (parameterString.equals("")) { // Ensure parameter string is not empty
            return parameters;
        }

        String[] parameterSplit = parameterString.split("\\s+"); // Split by space

        String commandParameter = "";
        StringBuilder parameterContents = new StringBuilder();

        for (String s : parameterSplit) {
            if (s.contains(DELIMITER)) {
                if (!commandParameter.equals("")) { // Ensure it is not the first iteration
                    // Add to linkedhashmap before resetting values
                    parameters.put(commandParameter, parameterContents.toString());
                }

                parameterContents = new StringBuilder(); // Reset the values
                String[] commandSplit = s.split(DELIMITER);

                if (commandSplit.length != 0) { // Ensure '/' exists
                    commandParameter = commandSplit[0].toLowerCase();
                }

                if (commandSplit.length > 1) {
                    parameterContents = new StringBuilder(commandSplit[1]);
                }
            } else { // Add the rest of the string
                parameterContents.append(" ").append(s);
            }
        }
        parameters.put(commandParameter, parameterContents.toString()); // Add to linkedhashmap for the last parameter
        return parameters;
    }

    /**
     * Helps to set the mode of the program.
     *
     * @param ui      Reference to the UI object to print messages.
     * @param command Command entered by the user.
     * @param mode    Current mode of the program.
     * @return New mode requested by the user.
     */
    public Mode changeMode(Ui ui, String command, Mode mode) {
        Mode newMode = mode;
        if (command.equalsIgnoreCase(STOCK.name()) && !mode.name().equalsIgnoreCase(STOCK.name())) {
            newMode = STOCK;
            ui.print("Mode has changed to STOCK.");
        } else if (command.equalsIgnoreCase(Mode.DISPENSE.name()) && !mode.name().equalsIgnoreCase(DISPENSE.name())) {
            newMode = Mode.DISPENSE;
            ui.print("Mode has changed to DISPENSE.");
        } else if (command.equalsIgnoreCase(ORDER.name()) && !mode.name().equalsIgnoreCase(ORDER.name())) {
            newMode = ORDER;
            ui.print("Mode has changed to ORDER.");
        } else {
            ui.print("Already in " + mode.name() + " mode!");
        }
        return newMode;
    }
}