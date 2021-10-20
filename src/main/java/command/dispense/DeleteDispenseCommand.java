package command.dispense;

import command.Command;
import command.CommandParameters;
import command.CommandSyntax;
import inventory.Dispense;
import inventory.Medicine;
import inventory.Stock;
import utilities.parser.DispenseValidator;
import utilities.storage.Storage;
import utilities.ui.Ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Delete dispense based on user input given dispense id.
 */
public class DeleteDispenseCommand extends Command {
    private static Logger logger = Logger.getLogger("DeleteDispense");

    public DeleteDispenseCommand(LinkedHashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void execute() {
        logger.log(Level.INFO, "Start deletion of dispense");

        Ui ui = Ui.getInstance();
        ArrayList<Medicine> medicines = Medicine.getInstance();
        Storage storage = Storage.getInstance();

        String[] requiredParameters = {CommandParameters.ID};
        String[] optionalParameters = {};

        boolean isInvalidParameter = CommandSyntax.containsInvalidParameters(ui, parameters, requiredParameters,
                optionalParameters, CommandSyntax.DELETE_DISPENSE_COMMAND, true);

        if (isInvalidParameter) {
            logger.log(Level.WARNING, "Invalid parameter is specified by user");
            logger.log(Level.INFO, "Unsuccessful deletion of dispense");
            return;
        }
        String dispenseIdToDelete = parameters.get(CommandParameters.ID);

        boolean isValidDispenseId = DispenseValidator.isValidDispenseId(ui, dispenseIdToDelete, medicines);
        if (!isValidDispenseId) {
            logger.log(Level.WARNING, "Invalid dispense id is specified by user");
            logger.log(Level.INFO, "Unsuccessful deletion of dispense");
            return;
        }

        int dispenseId = Integer.parseInt(dispenseIdToDelete);

        assert dispenseId <= Dispense.getDispenseCount() : "Dispense Id should not exceed max dispense count";

        int stockIdToDispense;
        int dispenseQuantity;
        for (Medicine medicine : medicines) {
            if (!(medicine instanceof Dispense)) {
                continue;
            }
            Dispense dispense = (Dispense) medicine;
            if (dispense.getDispenseId() == dispenseId) {
                stockIdToDispense = dispense.getStockId();
                dispenseQuantity = dispense.getQuantity();
                if (setStockQuantity(ui, medicines, stockIdToDispense, dispenseQuantity)) {
                    return;
                }
                medicines.remove(dispense);
                ui.print("Dispense deleted for Dispense Id " + dispenseId);
                logger.log(Level.INFO, "Successful deletion of Dispense");
                return;
            }
        }
    }

    /**
     * Check stock if stock exist. If stock exist, add the quantity to the stock quantity.
     *
     * @param ui                Reference to the UI object to print messages.
     * @param medicines         Arraylist of medicines
     * @param stockIdToDispense Stock ID dispensed.
     * @param dispenseQuantity  Quantity dispensed.
     * @return Boolean value indicating if stock id exist.
     */
    private boolean setStockQuantity(Ui ui, ArrayList<Medicine> medicines, int stockIdToDispense,
                                     int dispenseQuantity) {
        for (Medicine medicine : medicines) {
            if (!(medicine instanceof Stock)) {
                continue;
            }
            Stock stock = (Stock) medicine;
            if (stock.isDeleted()) {
                stock.setDeleted(false);
            }
            if (stock.getStockID() == stockIdToDispense) {
                int quantityToRestore = stock.getQuantity() + dispenseQuantity;
                if (quantityToRestore > stock.getMaxQuantity()) {
                    ui.print("Unable to delete dispense. Quantity added will exceed maximum quantity.");
                    ui.print("Maximum quantity: " + stock.getMaxQuantity() + " Total Quantity after deletion: "
                            + quantityToRestore);
                    return true;
                }
                stock.setQuantity(quantityToRestore);
            }
        }
        return false;
    }

}

