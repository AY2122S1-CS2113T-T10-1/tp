# User Guide

## Introduction

MediVault is a Command Line Interface (CLI) application that will help to manage medication supplies within a pharmacy.
It is an integrated solution that provides real-time tracking of stock, orders and dispensing of medication.

## Contents

* [Quick Start](#quick-start)
* [Features](#features)
    * [Changing modes](#changing-modes)
    * [Add medication stock](#adding-a-medication-stock-addstock)
    * [Delete medication stock](#deleting-a-medication-stock-deletestock)
    * [Update medication stock](#updating-medication-stock-information-updatestock)
    * [List medication stock](#listing-medication-stock--liststock)
    * [Add dispense record](#adding-a-dispense-record-adddispense)
    * [Delete dispense record](#deleting-a-dispense-record-deletedispense)
    * [Update dispense record](#updating-dispense-record-updatedispense)
    * [List dispense record](#listing-dispense-record--listdispense)
    * [Add order](#adding-an-order-addorder)
    * [Delete order](#deleting-an-order-deleteorder)
    * [Update order](#updating-order-updateorder)
    * [List order](#listing-order--listorder)
    * [Receive order](#receiving-order--receiveorder)
    * [Archive](#archiving-data--archive)
    * [Purge data](#purging-existing-data--purge)
    * [Help](#showing-help-page--help)
    * [Exit](#exiting-medivault--exit)
    * [Data Storage](#data-storage)
    * [Data Editing](#data-editing)
* [FAQ](#faq)
* [Command Summary](#command-summary)

## Quick Start

1. Ensure that you have Java 11 or above installed.
2. Download the latest version of `MediVault.jar`
   from [here](https://github.com/AY2122S1-CS2113T-T10-1/tp/releases/tag/v1.0).

## Features

Notes about the commands:

* Parameters enclosed in `[]` are **one or more** optional parameters.
* Parameters enclosed in `{}` are **totally** optional parameters.
* Parameters you specify can be in any order.
    * E.g. `update i/1 q/100 m/200` and `update i/1 m/200 q/100` are both acceptable.
* MediVault ignores additional parameters provided when commands do not require one.
* If you specify the same parameter multiple times, MediVault will accept the last occurrence.
    * E.g. `delete i/2 i/1`, MediVault interprets the command as `delete i/1`.
* MediVault's commands are case-insensitive.
* Dates in the `e/EXPIRY_DATE` field are in `DD-MM-YYYY` format.
* Column names in the `sort` parameter can be provided as the full column name or the column alias.
    * E.g. `NAME` is equivalent to `n` and `QUANTITY` is equivalent to `q`.
* For the `list` commands, use the `sort` parameter to sort by a column in ascending order and `rsort` parameter to sort
  in descending order.

### Changing Modes

Your current mode is indicated in the square brackets on the bottom left of the console `[STOCK] >`. It allows you to
type `add`, `list`, `update`, `delete` without typing in the full command. For example, when you are in `stock` mode,
typing `list` is equivalent to `liststock`.

Type `stock`, `dispense` or `order` to change to the respective modes.

Example (Current mode is Stock): `dispense`

Expected Output:

```
Mode has changed to DISPENSE.
[DISPENSE] > 
```

### Adding a medication stock: `addstock`
Adds medication into the inventory.
* If medication exists, description and maximum quantity will be optional parameters. If you include `d/DESCRIPTION m/MAX_QUANTITY` parameter, it will be ignored, MediVault will add with the existing description and existing maximum quantity.

Format: `addstock n/NAME p/PRICE q/QUANTITY e/EXPIRY_DATE [d/DESCRIPTION m/MAX_QUANTITY]`

Example 1 (If medication exists): `addstock n/panadol p/5 q/50 e/19-09-2021`

Expected Output 1:
```
Medicine exists. Using existing description and maximum quantity.
Medication added: panadol
+====+=========+=======+==========+=============+===============================================+==============+
| ID |  NAME   | PRICE | QUANTITY | EXPIRY_DATE |                  DESCRIPTION                  | MAX_QUANTITY | 
+====+=========+=======+==========+=============+===============================================+==============+
| 7  | panadol | $5.00 |    50    | 19-09-2021  |  BEST MEDICINE TO CURE HEADACHES, FEVER AND   |     1000     | 
|    |         |       |          |             |                     PAINS                     |              | 
+----+---------+-------+----------+-------------+-----------------------------------------------+--------------+
```
Example 2 (If medication does not exists): `addstock n/paracetamol q/10 p/10 e/02-11-2021 d/used to treat fever and pain m/500`

Expected Output 2:
```
Medication added: paracetamol
+====+=============+========+==========+=============+==============================+==============+
| ID |    NAME     | PRICE  | QUANTITY | EXPIRY_DATE |         DESCRIPTION          | MAX_QUANTITY | 
+====+=============+========+==========+=============+==============================+==============+
| 10 | paracetamol | $10.00 |    10    | 02-11-2021  | used to treat fever and pain |     500      | 
+----+-------------+--------+----------+-------------+------------------------------+--------------+
```

### Deleting a medication stock: `deletestock`

Deletes medication from the inventory.

* Able to delete a specific stock by specifying stock Id using `i/STOCK_ID`.
* Able to delete multiple stocks that have expiry date before and equals to specified date using `e/EXPIRY_DATE`.

Format: `deletestock [i/STOCK_ID e/EXPIRY_DATE]`

Example 1 (Deletion by stock Id): `deletestock i/3`

Expected output:

```
Deleted row with Stock Id: 3
```

Example 2 (Deletion by expiry date): `deletestock e/10-10-2021`

Expected output:

```
Deleted expired medications! Rows deleted: 4
```

### Updating medication stock information: `updatestock`

Edits an existing medication stock information in the inventory.

* Edits the optional parameters specified by the stock Id that you input.
    * The Stock Id must exist in MediVault.
* You cannot update the Stock Id. The allocation of Id is determined by MediVault.
* If you include the `n/NAME`, `d/DESCRIPTION` or `m/MAX_QUANTITY` parameter, MediVault updates 
**all** entries that has same existing medication name given the `i/STOCK_ID` with your input values for these parameters.
* A new Stock Id will be assigned to the current stock if your update has the `n/NAME` parameter. 

Format: `updatestock i/STOCK_ID [n/NAME p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY]`

Example:
`update i/3 n/amoxil p/20 q/50 e/01-12-2021 d/Treats infections to ear, nose, throat, skin, or urinary tract m/100`

Expected output:

```
Updated! Number of rows affected: 1
Stock Id changed from:
3 -> 7
+====+========+========+==========+=============+===============================================+==============+
| ID |  NAME  | PRICE  | QUANTITY | EXPIRY_DATE |                  DESCRIPTION                  | MAX_QUANTITY | 
+====+========+========+==========+=============+===============================================+==============+
| 7  | amoxil | $20.00 |    50    | 01-12-2021  | Treats infections to ear, nose, throat, skin, |     100      | 
|    |        |        |          |             |                or urinary tract               |              | 
+----+--------+--------+----------+-------------+-----------------------------------------------+--------------+
```

### Listing medication stock : `liststock`

Lists all existing medication in the inventory.

* All parameters for `liststock` command are optional, you can choose to list medication by any of the parameters.
* Example 1 demonstrates the list of all medication without parameters.
* Example 2 demonstrates list by medication name.

Format: `liststock {i/STOCK_ID n/NAME p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY sort/COLUMN_NAME rsort/COLUMN_NAME}`

Example 1: `liststock`

Expected output:

```
+====+==============+========+==============+=============+===============================================+==============+
| ID |     NAME     | PRICE  |   QUANTITY   | EXPIRY_DATE |                  DESCRIPTION                  | MAX_QUANTITY | 
+====+==============+========+==============+=============+===============================================+==============+
| 1  |   PANADOL    | $20.00 |      20      | 13-09-2021  |  BEST MEDICINE TO CURE HEADACHES, FEVER AND   |     1000     | 
|    |              |        | PENDING: 150 |             |                     PAINS                     |              | 
+----+--------------+--------+--------------+-------------+-----------------------------------------------+--------------+
| 2  |   PANADOL    | $20.00 |      10      | 14-09-2021  |  BEST MEDICINE TO CURE HEADACHES, FEVER AND   |     1000     | 
|    |              |        | PENDING: 150 |             |                     PAINS                     |              | 
+----+--------------+--------+--------------+-------------+-----------------------------------------------+--------------+
| 3  |   VICODIN    | $10.00 |      20      | 30-09-2021  |  POPULAR DRUG FOR TREATING ACUTE OR CHRONIC   |     500      | 
|    |              |        | PENDING: 30  |             |      MODERATE TO MODERATELY SEVERE PAIN       |              | 
+----+--------------+--------+--------------+-------------+-----------------------------------------------+--------------+
| 4  | SIMVASTATIN  | $20.00 |      25      | 10-10-2021  |   TREATS HIGH CHOLESTEROL AND REDUCES THE     |     800      | 
|    |              |        | PENDING: 20  |             |                RISK OF STROKE                 |              | 
+----+--------------+--------+--------------+-------------+-----------------------------------------------+--------------+
| 5  |  LISINOPRIL  | $20.00 |      25      | 15-10-2021  |       USED FOR TREATING HYPOTHYROIDISM        |     800      | 
|    |              |        | PENDING: 200 |             |                                               |              | 
+----+--------------+--------+--------------+-------------+-----------------------------------------------+--------------+
| 6  | AZITHROMYCIN | $20.00 |      35      | 15-10-2021  |   USED FOR TREATING EAR, THROAT, AND SINUS    |     100      | 
|    |              |        | PENDING: 100 |             |                  INFECTIONS                   |              | 
+----+--------------+--------+--------------+-------------+-----------------------------------------------+--------------+
```

Example 2: `liststock n/panadol`

Expected output:

```
+====+=========+========+==============+=============+===============================================+==============+
| ID |  NAME   | PRICE  |   QUANTITY   | EXPIRY_DATE |                  DESCRIPTION                  | MAX_QUANTITY | 
+====+=========+========+==============+=============+===============================================+==============+
| 1  | PANADOL | $20.00 |      20      | 13-09-2021  |  BEST MEDICINE TO CURE HEADACHES, FEVER AND   |     1000     | 
|    |         |        | PENDING: 150 |             |                     PAINS                     |              | 
+----+---------+--------+--------------+-------------+-----------------------------------------------+--------------+
| 2  | PANADOL | $20.00 |      10      | 14-09-2021  |  BEST MEDICINE TO CURE HEADACHES, FEVER AND   |     1000     | 
|    |         |        | PENDING: 150 |             |                     PAINS                     |              | 
+----+---------+--------+--------------+-------------+-----------------------------------------------+--------------+
```

### Adding a dispense record: `adddispense`
Adds a dispense.

Format: `adddispense n/NAME q/QUANTITY s/STAFF c/CUSTOMER_ID`

Example: `adddispense n/panadol q/5 s/john c/123`

Expected Output:
```
Dispensed:panadol Quantity:5 Expiry Date:13-09-2021
```

### Deleting a dispense record: `deletedispense`
Deletes dispense by specifying the dispense Id.

Format: `deletedispense i/DISPENSE_ID`

Example: `deletedispense i/3`

Expected output:
```
Dispense deleted for Dispense Id 3
```

### Updating dispense record: `updatedispense`

Edits an existing medication dispense information in the inventory.

* A new Dispense Id will be assigned to the current dispense record if you update a medication dispense information.
* You cannot update either the Stock or the Dispense Id. The allocation of Id is determined by MediVault.
* When you update a dispense information, the stock information may be affected as well

Format: `updatedispense i/ID [n/name q/QUANTITY c/CUSTOMER_ID d/DATE s/STAFF_NAME]`

Example: `updatedispense i/1 q/5`

Expected output:
```
Restored 5 PANADOL
Updated dispense information!
+====+=========+==========+=============+============+=======+==========+
| ID |  NAME   | QUANTITY | CUSTOMER ID |    DATE    | STAFF | STOCK ID | 
+====+=========+==========+=============+============+=======+==========+
| 6  | PANADOL |    5     |  S1234567A  | 09-10-2021 | Jane  |    1     | 
+----+---------+----------+-------------+------------+-------+----------+
```

### Listing dispense record : `listdispense`

Lists all dispense records in the application.

* All parameters for `listdispense` command are optional, you can choose to list the records by any of the parameters.
* Example 1 demonstrates the list of all dispense records without parameters.
* Example 2 demonstrates the list sorted in ascending order by staff name.

Format: `listdispense {i/ID q/QUANTITY c/CUSTOMER_ID d/DATE s/STAFF_NAME sid/STOCK_ID sort/COLUMN_NAME rsort/COLUMN NAME}`

Example 1: `listdispense`

Expected output:

```
+====+==============+==========+=============+============+=======+==========+
| ID |     NAME     | QUANTITY | CUSTOMER ID |    DATE    | STAFF | STOCK ID | 
+====+==============+==========+=============+============+=======+==========+
| 1  |   PANADOL    |    10    |  S1234567A  | 09-10-2021 | Jane  |    1     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 2  |   VICODIN    |    10    |  S2345678B  | 10-10-2021 | Peter |    3     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 3  | SIMVASTATIN  |    10    |  S1234567A  | 11-10-2021 |  Sam  |    4     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 4  |  LISINOPRIL  |    10    |  S3456789C  | 12-10-2021 | Jane  |    5     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 5  | AZITHROMYCIN |    10    |  S2345678B  | 13-10-2021 | Peter |    6     | 
+----+--------------+----------+-------------+------------+-------+----------+
```

Example 2: `listdispense sort/s`

Expected output:

```
+====+==============+==========+=============+============+=======+==========+
| ID |     NAME     | QUANTITY | CUSTOMER ID |    DATE    | STAFF | STOCK ID | 
+====+==============+==========+=============+============+=======+==========+
| 1  |   PANADOL    |    10    |  S1234567A  | 09-10-2021 | Jane  |    1     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 4  |  LISINOPRIL  |    10    |  S3456789C  | 12-10-2021 | Jane  |    5     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 2  |   VICODIN    |    10    |  S2345678B  | 10-10-2021 | Peter |    3     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 5  | AZITHROMYCIN |    10    |  S2345678B  | 13-10-2021 | Peter |    6     | 
+----+--------------+----------+-------------+------------+-------+----------+
| 3  | SIMVASTATIN  |    10    |  S1234567A  | 11-10-2021 |  Sam  |    4     | 
+----+--------------+----------+-------------+------------+-------+----------+
```

### Adding an order: `addorder`
Adds an order for medicine.
* The date parameter is optional, if there is no input for date, MediVault will set it as the date you added in the order. 
  * Assuming you place an order on 21 October 2021:
    * `addorder n/panadol q/150 d/21-10-2021` is the same as `addorder n/panadol q/150`
* If the order quantity exceeds the maximum stock quantity allowed, you are unable to add that order.

Format: `addorder n/NAME q/QUANTITY {d/DATE}`

Example 1: `addorder n/panadol q/150 d/21-10-2021`

Expected Output:
```
+====+=========+==========+============+=========+
| ID |  NAME   | QUANTITY |    DATE    | STATUS  |
+====+=========+==========+============+=========+
| 1  | panadol |   150    | 21-10-2021 | PENDING |
+----+---------+----------+------------+---------+
```

Example 2: `addorder n/panadol q/1000`

Expected Output:
```
Order for panadol exists. Unable to add order as total order quantity exceeds maximum stock quantity.
```

### Deleting an order: `deleteorder`
Deletes order by specifying the order Id.

Format: `deleteorder i/ORDER_ID`

Example: `deleteorder i/1`

Expected output:
```
Order deleted for Order Id 1
```

### Updating order: `updateorder`

Edits an existing medication order information in the inventory.

* You cannot update the Order Id or the status of the order.
  * The allocation of Id is determined by MediVault.
  * The status of the order will only be changed when you run the `receiveorder` command.
* When you update an order information, MediVault reflects the pending stocks shown in the current medication stocks.

Format: `updateorder i/ID [n/name q/QUANTITY c/CUSTOMER_ID d/DATE s/STAFF_NAME]`

Example: `updateorder i/1 q/50`

Expected output:
```
Updated! Number of rows affected: 1
+====+=========+==========+============+=========+
| ID |  NAME   | QUANTITY |    DATE    | STATUS  | 
+====+=========+==========+============+=========+
| 1  | PANADOL |    50    | 09-10-2021 | PENDING | 
+----+---------+----------+------------+---------+
```

### Listing order : `listorder`

Lists all order records in the application.

* All parameters for `listorder` command are optional, you can choose to list the records by any of the parameters.
* You are able to listorder by id, name, quantity, date, status and also sort/rsort by columns.
* Example 1 demonstrates the list of all order records without parameters.
* Example 2 demonstrates the list of all orders that are PENDING.

Format: `listorder {i/ID n/NAME q/QUANTITY d/DATE s/STATUS sort/COLUMN_NAME rsort/COLUMN NAME}`

Example 1: `listorder`

Expected output:

```
+====+==============+==========+============+===========+
| ID |     NAME     | QUANTITY |    DATE    |  STATUS   | 
+====+==============+==========+============+===========+
| 1  |   PANADOL    |   100    | 09-10-2021 |  PENDING  | 
+----+--------------+----------+------------+-----------+
| 2  |   VICODIN    |    30    | 09-10-2021 |  PENDING  | 
+----+--------------+----------+------------+-----------+
| 3  |   VICODIN    |    50    | 10-10-2021 | DELIVERED | 
+----+--------------+----------+------------+-----------+
| 4  | SIMVASTATIN  |    20    | 11-10-2021 |  PENDING  | 
+----+--------------+----------+------------+-----------+
| 5  |  LISINOPRIL  |   200    | 12-10-2021 |  PENDING  | 
+----+--------------+----------+------------+-----------+
| 6  | AZITHROMYCIN |   100    | 13-10-2021 |  PENDING  | 
+----+--------------+----------+------------+-----------+
| 7  |   PANADOL    |    50    | 13-10-2021 |  PENDING  | 
+----+--------------+----------+------------+-----------+
```

Example 2: `listorder s/pending`

Expended output:

```
+====+==============+==========+============+=========+
| ID |     NAME     | QUANTITY |    DATE    | STATUS  | 
+====+==============+==========+============+=========+
| 1  |   PANADOL    |   100    | 09-10-2021 | PENDING | 
+----+--------------+----------+------------+---------+
| 2  |   VICODIN    |    30    | 09-10-2021 | PENDING | 
+----+--------------+----------+------------+---------+
| 4  | SIMVASTATIN  |    20    | 11-10-2021 | PENDING | 
+----+--------------+----------+------------+---------+
| 5  |  LISINOPRIL  |   200    | 12-10-2021 | PENDING | 
+----+--------------+----------+------------+---------+
| 6  | AZITHROMYCIN |   100    | 13-10-2021 | PENDING | 
+----+--------------+----------+------------+---------+
| 7  |   PANADOL    |    50    | 13-10-2021 | PENDING | 
+----+--------------+----------+------------+---------+
```

### Receiving order : `receiveorder`

### Archiving data : `archive`

### Purging existing data : `purge`

Deletes all data in MediVault.

Format: `purge`

Example: `purge`

Expected output:

```
Are you sure you want to delete all data? (Y/N)
Y
All data has been cleared!
```

### Showing help page : `help`

Displays the command syntax of all accepted commands by Medivault.

Format:`help`

Example: `help`

Expected output:

```
Welcome to the help page.
Your current mode is indicated in the square brackets at the bottom left of the console.
It allows you to type add, list, update, delete without typing in the full command.
Type stock, dispense or order to change to respective modes.
Note that parameters in {curly braces} are optional.
Parameters in [square braces] indicate that at least one of the parameter(s) must be provided.
+================+===================================================================================================================+
|    COMMAND     |                                                  COMMAND SYNTAX                                                   | 
+================+===================================================================================================================+
|    addstock    | addstock n/NAME p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY                                     | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|  deletestock   | deletestock [i/ID e/EXPIRY_DATE]                                                                                  | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|  updatestock   | updatestock i/ID [n/NAME p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY]                           | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|   liststock    | liststock {i/ID p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY sort/COLUMN_NAME rsort/COLUMN NAME} | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|  adddispense   | adddispense n/NAME q/QUANTITY c/CUSTOMER_ID s/STAFF_NAME                                                          | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
| deletedispense | deletedispense i/ID                                                                                               | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|  listdispense  | listdispense {i/ID q/QUANTITY c/CUSTOMER_ID d/DATE s/STAFF_NAME sid/STOCK_ID sort/COLUMN_NAME rsort/COLUMN NAME}  | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|    addorder    | addorder n/NAME q/QUANTITY {d/DATE}                                                                               | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|  deleteorder   | deleteorder i/ID                                                                                                  | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|  updateorder   | updateorder i/ID [n/NAME q/QUANTITY d/DATE]                                                                       | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|   listorder    | listorder {i/ID n/NAME q/QUANTITY d/DATE s/STATUS sort/COLUMN_NAME rsort/COLUMN NAME}                             | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|     purge      | purge                                                                                                             | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|      help      | help                                                                                                              | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
|      exit      | exit                                                                                                              | 
+----------------+-------------------------------------------------------------------------------------------------------------------+
For more information, refer to User Guide: https://ay2122s1-cs2113t-t10-1.github.io/tp/
```

### Exiting MediVault : `exit`

Exits MediVault.

Format: `exit`

Example: `exit`

Expected output:

```
Quitting MediVault...
```

### Data Storage
MediVault will automatically save data after any operation that modifies stock, order or dispense. The data will be 
stored in 3 separate files (data/stock.txt, data/order.txt, data/dispense.txt). Data is saved in a specific format with
fields delimited by a pipe `|`. 
* For data/stock.txt
  * `ID|NAME|PRICE|QUANTITY|EXPIRY_DATE|DESCRIPTION|MAX_QUANTITY`
* For data/order.txt
  * `ID|NAME|QUANTITY|DATE|STATUS`
* For data/dispense.txt
  * `ID|NAME|QUANTITY|CUSTOMER_ID|DATE|STAFF|STOCK_ID`

### Data Editing
It is possible to directly edit the data files but it is **NOT** recommended unless you know exactly what you are doing.
If MediVault detects corruption or invalid data, you may not be able to load the data into the program. In that case, 
either fix the data or in the worst case scenario, delete the corresponding data file.
Also, it may result in unintended behaviour if data file is tampered with while the program is running.

## FAQ

**Q**: How do I transfer my data to another computer?

**A**: You can transfer data to another computer by moving the 3 data files into the folder where MediVault.jar is. 
Ensure that the data files are in a folder named `data`. You should expect to see `stock.txt, order.txt, dispense.txt`
in that folder.

## Command Summary

Syntax | Command Parameters
------ | ------
addstock | `addstock n/NAME p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY`
deletestock | `deletestock i/STOCK_ID`
updatestock | `updatestock i/STOCK_ID [n/NAME p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY]`
liststock | `liststock {i/STOCK_ID p/PRICE q/QUANTITY e/EXPIRY_DATE d/DESCRIPTION m/MAX_QUANTITY sort/COLUMN_NAME rsort/COLUMN NAME}`
adddispense | `adddispense n/NAME q/QUANTITY c/CUSTOMER_ID s/STAFF_NAME`
deletedispense | `deletedispense i/ID`
listdispense | `listdispense {i/ID q/QUANTITY c/CUSTOMER_ID d/DATE s/STAFF_NAME sid/STOCK_ID sort/COLUMN_NAME rsort/COLUMN NAME}`
addorder | `addorder n/NAME q/QUANTITY {d/DATE}`
deleteorder | `deleteorder i/ID`
updateorder | `updateorder i/ID [n/NAME q/QUANTITY d/DATE]  `
listorder | `listorder {i/ID n/NAME q/QUANTITY d/DATE s/STATUS sort/COLUMN_NAME rsort/COLUMN NAME}`
purge | `no parameters`
help | `no parameters`
exit | `no parameters`