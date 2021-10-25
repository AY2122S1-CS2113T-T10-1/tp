package command.stock;

import command.Command;
import command.CommandParameters;
import command.CommandSyntax;
import inventory.Medicine;
import inventory.Stock;
import utilities.parser.DateParser;
import utilities.parser.StockManager;
import utilities.parser.StockValidator;
import utilities.storage.Storage;
import utilities.ui.Ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//@@author deonchung

/**
 * Add medication based on user input.
 * User input include name, price, quantity, expiry date, description and maximum quantity of medication.
 */
public class AddStockCommand extends Command {
    private static Logger logger = Logger.getLogger("AddCommand");

    public AddStockCommand(LinkedHashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void execute() {
        logger.log(Level.INFO, "Start addition of stock");

        Ui ui = Ui.getInstance();
        ArrayList<Medicine> medicines = Medicine.getInstance();

        boolean nameExist = false;
        String nameToAdd = parameters.get(CommandParameters.NAME);
        String[] optionalParameters = {};
        StockValidator stockValidator = new StockValidator();
        ArrayList<Stock> filteredStocks = new ArrayList<>();

        if (parameters.containsKey(CommandParameters.NAME)) {
            nameToAdd = parameters.get(CommandParameters.NAME);
            for (Medicine medicine : medicines) {
                if (medicine instanceof Stock && medicine.getMedicineName().equalsIgnoreCase(nameToAdd)
                        && !((Stock) medicine).isDeleted()) {
                    filteredStocks.add((Stock) medicine);
                    nameExist = true;
                }
            }
        }

        if (nameExist) {
            String[] requiredParameters = {CommandParameters.NAME, CommandParameters.QUANTITY,
                    CommandParameters.EXPIRY_DATE};

            if (checkValidParametersAndValues(ui, parameters, medicines, requiredParameters, optionalParameters,
                    stockValidator)) {
                return;
            }

            String quantityToAdd = parameters.get(CommandParameters.QUANTITY);
            String expiryToAdd = parameters.get(CommandParameters.EXPIRY_DATE);

            Date formatExpiry = null;
            try {
                formatExpiry = DateParser.stringToDate(expiryToAdd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int totalStock = StockManager.getTotalStockQuantity(medicines, nameToAdd);
            assert totalStock > 0 : "Total Stock should be more than 0";
            if (nameExpiryExist(ui, filteredStocks, quantityToAdd, formatExpiry)) {
                return;
            }
            String[] requiredParams = {CommandParameters.NAME, CommandParameters.PRICE,
                    CommandParameters.QUANTITY, CommandParameters.EXPIRY_DATE};

            if (checkValidParametersAndValues(ui, parameters, medicines, requiredParams, optionalParameters,
                    stockValidator)) {
                return;
            }

            for (Stock stock : filteredStocks) {
                int quantity = Integer.parseInt(quantityToAdd);
                int existingMaxQuantity = stock.getMaxQuantity();
                boolean isValidQuantity =
                        stockValidator.quantityValidityChecker(ui, (totalStock + quantity),
                                existingMaxQuantity);

                if (!isValidQuantity) {
                    logger.log(Level.WARNING, "Invalid Quantity is specified by user");
                    logger.log(Level.INFO, "Unsuccessful addition of stock");
                    return;
                }
                String existingDescription = stock.getDescription();
                String priceToAdd = parameters.get(CommandParameters.PRICE);
                double price = Double.parseDouble(priceToAdd);
                ui.print("Medicine exists. Using existing description and maximum quantity.");
                addMedicine(ui, medicines, nameToAdd, existingDescription, price,
                        quantity, formatExpiry, existingMaxQuantity);
                return;
            }
        } else {
            String[] requiredParameters = {CommandParameters.NAME, CommandParameters.PRICE,
                    CommandParameters.QUANTITY, CommandParameters.EXPIRY_DATE,
                    CommandParameters.DESCRIPTION, CommandParameters.MAX_QUANTITY};

            if (checkValidParametersAndValues(ui, parameters, medicines, requiredParameters, optionalParameters,
                    stockValidator)) {
                return;
            }

            String priceToAdd = parameters.get(CommandParameters.PRICE);
            String quantityToAdd = parameters.get(CommandParameters.QUANTITY);
            String expiryToAdd = parameters.get(CommandParameters.EXPIRY_DATE);
            String descriptionToAdd = parameters.get(CommandParameters.DESCRIPTION);
            String maxQuantityToAdd = parameters.get(CommandParameters.MAX_QUANTITY);

            Date formatExpiry = null;
            try {
                formatExpiry = DateParser.stringToDate(expiryToAdd);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int maxQuantity = Integer.parseInt(maxQuantityToAdd);
            int quantity = Integer.parseInt(quantityToAdd);
            double price = Double.parseDouble(priceToAdd);

            if (quantity > maxQuantity) {
                ui.print("Unable to add. Quantity is more than Maximum Quantity.");
                return;
            }

            addMedicine(ui, medicines, nameToAdd, descriptionToAdd, price, quantity, formatExpiry, maxQuantity);
        }
        Storage storage = Storage.getInstance();
        storage.saveData(medicines);
    }

    /**
     * Change quantity of medication with same name and expiry date.
     *
     * @param ui             Reference to the UI object to print messages.
     * @param filteredStocks List of all medication with same name as input name of medication.
     * @param quantityToAdd  Quantity of medication to add.
     * @param formatExpiry   Expiry Date of medication to add.
     * @return Boolean value indicating if same name and expiry date of medication exist.
     */
    private boolean nameExpiryExist(Ui ui, ArrayList<Stock> filteredStocks, String quantityToAdd,
                                    Date formatExpiry) {
        for (Stock stock : filteredStocks) {
            int quantity = Integer.parseInt(quantityToAdd);

            if (formatExpiry.equals(stock.getExpiry())) {
                quantity += stock.getQuantity();
                stock.setQuantity(quantity);
                ui.print("Same Medication and Expiry Date exist. Update existing quantity.");
                ui.printStock(stock);
                logger.log(Level.INFO, "Successful addition of stock");
                return true;
            }
        }
        return false;
    }

    /**
     * Check if input contains Invalid Parameters and Invalid Parameter Values.
     *
     * @param ui                 Reference to the UI object to print messages.
     * @param parameters         The parameter that is not found.
     * @param medicines          List of all medicines.
     * @param requiredParameters The required parameters to check.
     * @param optionalParameters The optional parameters to check.
     * @param stockValidator     Reference to StockValidator object.
     * @return Boolean value indicating if parameter and parameter values are valid.
     */
    private boolean checkValidParametersAndValues(Ui ui, LinkedHashMap<String, String> parameters,
                                                  ArrayList<Medicine> medicines, String[] requiredParameters,
                                                  String[] optionalParameters, StockValidator stockValidator) {
        boolean isInvalidParameters = stockValidator.containsInvalidParameters(ui, parameters, requiredParameters,
                optionalParameters, CommandSyntax.ADD_STOCK_COMMAND, false);
        if (isInvalidParameters) {
            logger.log(Level.WARNING, "Invalid parameter is specified by user");
            logger.log(Level.INFO, "Unsuccessful addition of stock");
            return true;
        }
        boolean isInvalidParameterValues = stockValidator.containsInvalidParameterValues(ui, parameters,
                medicines, CommandSyntax.ADD_STOCK_COMMAND);
        if (isInvalidParameterValues) {
            logger.log(Level.WARNING, "Invalid parameter is specified by user");
            logger.log(Level.INFO, "Unsuccessful addition of stock");
            return true;
        }
        return false;
    }

    /**
     * Add medication based on user input.
     *
     * @param ui          Reference to the UI object to print messages.
     * @param medicines   List of all medicines.
     * @param name        Name of medication to add.
     * @param description Description of medication to add.
     * @param price       Price of medication to add.
     * @param quantity    Quantity of medication to add.
     * @param expiryDate  Expiry Date of medication to add.
     * @param maxQuantity Maximum Quantity of medication to add.
     */
    private void addMedicine(Ui ui, ArrayList<Medicine> medicines, String name, String description,
                             double price, int quantity, Date expiryDate, int maxQuantity) {
        Stock stock = new Stock(name, price, quantity, expiryDate, description, maxQuantity);
        medicines.add(stock);
        ui.print("Medication added: " + name);
        ui.printStock(stock);
        logger.log(Level.INFO, "Successful addition of stock");
        return;

    }

}
