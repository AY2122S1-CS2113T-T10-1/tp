package command.order;

import command.Command;
import command.CommandParameters;
import command.CommandSyntax;
import inventory.Medicine;
import inventory.Order;
import inventory.Stock;
import utilities.parser.DateParser;
import utilities.parser.OrderValidator;
import utilities.ui.Ui;
import utilities.storage.Storage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Delete order based on user input given order id.
 */
public class ArchiveOrderCommand extends Command {
    private static Logger logger = Logger.getLogger("Delete Order");

    public ArchiveOrderCommand(LinkedHashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void execute() {
        logger.log(Level.INFO, "Start archiving of order");

        Ui ui = Ui.getInstance();
        ArrayList<Medicine> medicines = Medicine.getInstance();

        String[] requiredParameters = {CommandParameters.DATE};
        String[] optionalParameters = {};

        OrderValidator orderValidator = new OrderValidator();
        boolean isInvalidParameter = orderValidator.containsInvalidParameters(ui, parameters, requiredParameters,
                optionalParameters, CommandSyntax.ARCHIVE_ORDER_COMMAND, true);
        if (isInvalidParameter) {
            logger.log(Level.WARNING, "Invalid parameter is specified by user");
            logger.log(Level.INFO, "Unsuccessful archive of order");
            return;
        }

        boolean isInvalidParameterValues = orderValidator.containsInvalidParameterValues(ui, parameters,
                medicines, CommandSyntax.ARCHIVE_ORDER_COMMAND);
        if (isInvalidParameterValues) {
            logger.log(Level.WARNING, "Invalid parameters values given by user");
            logger.log(Level.INFO, "Unsuccessful archive of order");
            return;
        }

        Date orderArchiveDate = null;
        String orderArchiveDateStr = parameters.get(CommandParameters.DATE);
        try {
            orderArchiveDate = DateParser.stringToDate(orderArchiveDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<Medicine> filteredOrders = new ArrayList<>();

        assert (filteredOrders != null) : "Array is not initialised";

        for (Medicine medicine : medicines) {
            if (!(medicine instanceof Order)) {
                continue;
            }
            Order order = (Order) medicine;
            if (order.getStatus().equalsIgnoreCase("DELIVERED")) {
                if (order.getDate().before(orderArchiveDate) || order.getDate().equals(orderArchiveDate)) {
                    filteredOrders.add(order);
                }
            }
        }

        for (Medicine medicine : filteredOrders) {
            medicines.remove(medicine);
        }

        Storage storage = Storage.getInstance();
        storage.archiveData(filteredOrders);
        storage.saveData(medicines);
        ui.print("Archived delivered orders from " + DateParser.dateToString(orderArchiveDate));
        logger.log(Level.INFO, "Successful archive of order");
    }
}

