package command.medicine;

import command.Command;
import command.CommandParameters;
import command.CommandSyntax;
import inventory.Stock;
import inventory.Medicine;
import parser.DateParser;
import parser.MedicineManager;
import parser.StockValidator;
import ui.Ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Update medication information based on user input given stock id.
 */

public class UpdateCommand extends Command {

    @Override
    public void execute(Ui ui, HashMap<String, String> parameters, ArrayList<Medicine> medicines) {
        String[] requiredParameter = {CommandParameters.STOCK_ID};
        String[] optionalParameters = {CommandParameters.PRICE, CommandParameters.QUANTITY,
            CommandParameters.EXPIRY_DATE, CommandParameters.DESCRIPTION, CommandParameters.UPDATED_MEDICINE_NAME,
            CommandParameters.MAX_QUANTITY};

        boolean isInvalidParameter = CommandSyntax.containsInvalidParameters(ui, parameters, requiredParameter,
                optionalParameters, CommandSyntax.UPDATE_COMMAND, "update");
        if (isInvalidParameter) {
            return;
        }

        boolean isInvalidParameterValues = CommandSyntax.containsInvalidParameterValues(ui, parameters, medicines);
        if (isInvalidParameterValues) {
            return;
        }

        Stock stock = MedicineManager.extractStockObject(parameters, medicines);
        boolean isValidQuantityValues = processQuantityValues(ui, parameters, medicines, stock);
        if (!isValidQuantityValues) {
            return;
        }

        boolean isValidExpDate = processDateInput(ui, parameters, medicines, stock);
        if (!isValidExpDate) {
            return;
        }

        setUpdatesByStockID(parameters, medicines, stock);
        ui.print("Updated");
        ui.printStock(stock);
    }

    private boolean processDateInput(Ui ui, HashMap<String, String> parameters, ArrayList<Medicine> medicines,
                                     Stock stock) {
        String name = stock.getMedicineName();

        boolean hasExpiryDate = parameters.containsKey(CommandParameters.EXPIRY_DATE);
        if (!hasExpiryDate) {
            return true;
        }

        Date expiryDate = null;
        try {
            expiryDate = DateParser.stringToDate(parameters.get(CommandParameters.EXPIRY_DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return StockValidator.dateValidityChecker(ui, medicines, expiryDate, name);
    }

    /**
     * Process quantity values to be updated given a stock id.
     *
     * @param ui         Reference to the UI object passed by Main to print messages.
     * @param parameters HashMap Key-Value set for parameter and user specified parameter value.
     * @param medicines  Arraylist of all medicines.
     * @return Boolean value indicating if quantity values are valid.
     */
    private boolean processQuantityValues(Ui ui, HashMap<String, String> parameters, ArrayList<Medicine> medicines,
                                          Stock stock) {

        String name = stock.getMedicineName();

        int quantity = 0;
        int maxQuantity = 0;
        int totalStockQuantity = 0;
        int initialQuantity = 0;
        int updatedQuantity = 0;

        boolean hasQuantity = parameters.containsKey(CommandParameters.QUANTITY);
        boolean hasMaxQuantity = parameters.containsKey(CommandParameters.MAX_QUANTITY);

        // initialise quantity and max quantity based on the different combinations of user inputs
        if (hasQuantity && hasMaxQuantity) {
            totalStockQuantity = MedicineManager.getTotalStockQuantity(medicines, name);
            initialQuantity = stock.getQuantity();
            updatedQuantity = Integer.parseInt(parameters.get(CommandParameters.QUANTITY));
            quantity = totalStockQuantity - initialQuantity + updatedQuantity;
            maxQuantity = Integer.parseInt(parameters.get(CommandParameters.MAX_QUANTITY));
        }

        if (hasQuantity && !hasMaxQuantity) {
            totalStockQuantity = MedicineManager.getTotalStockQuantity(medicines, name);
            initialQuantity = stock.getQuantity();
            updatedQuantity = Integer.parseInt(parameters.get(CommandParameters.QUANTITY));
            quantity = totalStockQuantity - initialQuantity + updatedQuantity;
            maxQuantity = MedicineManager.getMaxStockQuantity(medicines, name);
        }

        if (!hasQuantity && hasMaxQuantity) {
            quantity = MedicineManager.getTotalStockQuantity(medicines, name);
            maxQuantity = Integer.parseInt(parameters.get(CommandParameters.MAX_QUANTITY));
        }

        return StockValidator.quantityValidityChecker(ui, quantity, maxQuantity);
    }

    /**
     * Update values provided by user for a given stock id.
     *
     * @param parameters HashMap Key-Value set for parameter and user specified parameter value.
     * @param medicines  Arraylist of all medicines.
     * @param stock      Stock object of the given stock id.
     */
    private void setUpdatesByStockID(HashMap<String, String> parameters, ArrayList<Medicine> medicines, Stock stock) {
        ArrayList<Stock> filteredMedicines = new ArrayList<>();
        for (Medicine medicine : medicines) {
            if (medicine instanceof Stock && medicine.getMedicineName().equalsIgnoreCase(stock.getMedicineName())) {
                filteredMedicines.add((Stock) medicine);
            }
        }
        for (String parameter : parameters.keySet()) {
            String parameterValue = parameters.get(parameter);
            switch (parameter) {
            case CommandParameters.UPDATED_MEDICINE_NAME:
                for (Stock targetStock : filteredMedicines) {
                    targetStock.setMedicineName(parameterValue);
                }
                break;
            case CommandParameters.PRICE:
                stock.setPrice(Double.parseDouble(parameterValue));
                break;
            case CommandParameters.QUANTITY:
                stock.setQuantity(Integer.parseInt(parameterValue));
                break;
            case CommandParameters.EXPIRY_DATE:
                try {
                    stock.setExpiry(DateParser.stringToDate(parameterValue));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case CommandParameters.DESCRIPTION:
                for (Stock targetStock : filteredMedicines) {
                    targetStock.setDescription(parameterValue);
                }
                break;
            case CommandParameters.MAX_QUANTITY:
                for (Stock targetStock : filteredMedicines) {
                    targetStock.setMaxQuantity(Integer.parseInt(parameterValue));
                }
                break;
            default:
                break;
            }
        }
    }
}
