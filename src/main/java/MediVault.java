import errors.InvalidCommand;
import inventory.Dispense;
import inventory.Medicine;
import inventory.Order;
import inventory.Stock;
import parser.DateParser;
import parser.Parser;
import ui.Ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helps to start the application, and initialise all variables.
 * It will continuously prompt for input from the user until "EXIT" is received.
 */

public class MediVault {
    private ArrayList<Medicine> medicines;
    private Ui ui;
    private static Logger logger = Logger.getLogger("MediVault");

    public MediVault() {
        medicines = new ArrayList<>();
        ui = new Ui();
        generateData();
        logger.log(Level.INFO, "All variables are initialised.");
    }

    public static void main(String[] args) {
        logger.log(Level.INFO, "Medivault is starting up");
        new MediVault().run();
    }

    /**
     * Prompts input from user and processes it indefinitely until "EXIT" is received.
     */
    private void run() {
        ui.printWelcomeMessage();

        String userInput = "";
        Scanner in = new Scanner(System.in);

        // Loops till exit is received
        while (true) {
            System.out.print("> ");
            // Reads user input
            userInput = in.nextLine();
            try {
                if (Parser.processCommand(ui, userInput, medicines)) { // User entered exit
                    break;
                }
            } catch (InvalidCommand e) {
                // Invalid Command
                ui.printInvalidCommandMessage();
                logger.log(Level.WARNING, "An invalid command was entered!");
            }
        }
        logger.log(Level.INFO, "MediVault is shutting down");
    }

    /**
     * Temporary function to generate data to test features like delete and update. To be removed once add function is
     * complete.
     */
    public void generateData() {
        try {
            medicines.add(new Stock("PANADOL", 20, 20, DateParser.stringToDate("13-9-2021"),
                    "BEST MEDICINE TO CURE HEADACHES, FEVER AND PAINS", 1000));
            medicines.add(new Stock("PANADOL", 20, 10, DateParser.stringToDate("14-9-2021"),
                    "BEST MEDICINE TO CURE HEADACHES, FEVER AND PAINS", 1000));
            medicines.add(new Stock("VICODIN", 10, 20, DateParser.stringToDate("30-9-2021"),
                    "POPULAR DRUG FOR TREATING ACUTE OR CHRONIC MODERATE TO MODERATELY SEVERE PAIN",
                    500));
            medicines.add(new Stock("SIMVASTATIN", 20, 25, DateParser.stringToDate("10-10-2021"),
                    "TREATS HIGH CHOLESTEROL AND REDUCES THE RISK OF STROKE", 800));
            medicines.add(new Stock("LISINOPRIL", 20, 25, DateParser.stringToDate("15-10-2021"),
                    "USED FOR TREATING HYPOTHYROIDISM", 800));
            medicines.add(new Stock("AZITHROMYCIN", 20, 35, DateParser.stringToDate("15-10-2021"),
                    "USED FOR TREATING EAR, THROAT, AND SINUS INFECTIONS", 100));
            medicines.add(new Order("PANADOL", 100, DateParser.stringToDate("9-10-2021")));
            Order order = new Order("VICODIN", 50, DateParser.stringToDate("10-10-2021"));
            order.setDelivered();
            medicines.add(order);
            medicines.add(new Order("SIMVASTATIN", 20, DateParser.stringToDate("11-10-2021")));
            medicines.add(new Order("LISINOPRIL", 200, DateParser.stringToDate("12-10-2021")));
            medicines.add(new Order("AZITHROMYCIN", 100, DateParser.stringToDate("13-10-2021")));
            medicines.add(new Dispense("PANADOL", 10, "S1234567A",
                    DateParser.stringToDate("9-10-2021"), "Jane"));
            medicines.add(new Dispense("VICODIN", 10, "S2345678B",
                    DateParser.stringToDate("10-10-2021"), "Peter"));
            medicines.add(new Dispense("SIMVASTATIN", 10, "S1234567A",
                    DateParser.stringToDate("11-10-2021"), "Sam"));
            medicines.add(new Dispense("LISINOPRIL", 10, "S3456789C",
                    DateParser.stringToDate("12-10-2021"), "Jane"));
            medicines.add(new Dispense("AZITHROMYCIN", 10, "S2345678B",
                    DateParser.stringToDate("13-10-2021"), "Peter"));
        } catch (ParseException e) {
            ui.print("Unable to parse date!");
        }
    }

}
