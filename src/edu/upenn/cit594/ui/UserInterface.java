package edu.upenn.cit594.ui;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.Processor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * @author Xuanhe Zhang
 */
public class UserInterface {
    // the processor that processes the data
    private Processor processor;
    // the scanner that reads user input
    private Scanner scanner;
    // the logger that logs user input
    private Logger logger;

    /**
     * Constructor
     */
    public UserInterface(Processor processor){
        this.processor = processor;
        this.scanner = new Scanner(System.in);
        logger = Logger.getInstance();
    }


    /**
     * start the user interface
     */
    public void start() {
        // init the scanner for user input
        scanner = new Scanner(System.in);;
        while(true){
            // show all actions
            showAllActions();

            int commandInt = -1;
            do{
                // prompt the user to enter a command
                prompt("Please enter a command (0 - 7):");
                // read the command and log it
                String command = getResponse();


                // try to parse the command to an integer
                try{
                    commandInt = Integer.parseInt(command);
                }
                catch (NumberFormatException e){
                    // if the command is not an integer, prompt the user to enter a command again
                    continue;
                }
                // execute the command
                int code = selectOption(commandInt);
                // if the command is exit, exit the program
                if(code == -1){
                    return;
                }
                // if the command is valid, break the loop and prompt the user to enter a command again
                else if(code == 1){
                    break;
                }
                // if the command is invalid, prompt the user to enter a command again
                else{
                    continue;
                }

            } while(true);
        }
    }

    /**
     * helper method to get prompt from the user
     * @param message
     */
    private void prompt(String message){
        System.out.println(message);
        System.out.print("> ");
        // flush the output stream
        System.out.flush();
    }


    /**
     * select an option based on the given option
     * @param option the option to select
     * @return 0 if the option is valid, 1 if the option is invalid, -1 if the option is exit
     */
    private int selectOption(int option) {
        // check if the option is available
        boolean available = processor.isAvailableAction(option);
        if(available){
            switch (option) {
                case 0 -> {
                    return -1;
                }
                case 1 -> {
                    showAvailableActions();
                    return 1;
                }
                case 2 -> {
                    showTotalPopulationForAllZipCodes();
                    return 1;
                }
                case 3 -> {
                    showTotalPartialOrFullVaccinationsPerCapita();
                    return 1;
                }
                case 4 -> {
                    showAverageMarketValueOrTotalLivableArea("averageMarketValue");
                    return 1;
                }
                case 5 -> {
                    showAverageMarketValueOrTotalLivableArea("totalLivableArea");
                    return 1;
                }
                case 6 -> {
                    showTotalMarketValuePerCapita();
                    return 1;
                }
                case 7 -> {
                    showTotalLivableAreaMultiplyFullyVaccinatedPeopleRatio();
                    return 1;
                }
                default -> {
                    System.out.println("Error: Invalid option.");
                    return 0;
                }
            }
        }
        else{
            System.out.println("Error: Invalid option.");
        }

        return 0;
    }


    /**
     * show all actions
     */
    private void showAllActions(){
        // show all actions
        System.out.println("0. Exit the program.");
        System.out.println("1. Show the available actions.");
        System.out.println("2. Show total population for all ZIP Codes");
        System.out.println("3. Show the total vaccinations per capita " +
                "for each ZIP Code for the specified date");
        System.out.println("4. Show the average market value " +
                "for properties in a specified ZIP Code");
        System.out.println("5. Show the average total livable area " +
                "for properties in a specified ZIP Code");
        System.out.println("6. Show the total market value of " +
                "properties, per capita, for a specified ZIP Code");
        System.out.println("7. Show the avarage livable area of fully vaccinated people (additional feature)");
    }


    /**
     * show available actions based on the data
     */
    private void showAvailableActions(){
        // get the available actions
        int mask = processor.getAvailableActions();

        switch(mask){
            // DONE no data
            case 0b000 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("END OUTPUT");
            }
            // DONE only covid
            case 0b001 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("END OUTPUT");
            }
            // DONE only properties
            case 0b010 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("4");
                System.out.println("5");
                System.out.println("END OUTPUT");
            }
            // DONE properties and covid
            case 0b011 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("4");
                System.out.println("5");
                System.out.println("END OUTPUT");
            }
            // DONE only population
            case 0b100 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("2");
                System.out.println("END OUTPUT");

            }
            // DONE population and covid
            case 0b101 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("2");
                System.out.println("3");
                System.out.println("END OUTPUT");
            }
            // DONE population and properties
            case 0b110 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("2");
                System.out.println("4");
                System.out.println("5");
                System.out.println("6");
                System.out.println("END OUTPUT");
            }
            // DONE all
            case 0b111 -> {
                System.out.println("BEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                System.out.println("2");
                System.out.println("3");
                System.out.println("4");
                System.out.println("5");
                System.out.println("6");
                System.out.println("7");
                System.out.println("END OUTPUT");
            }
        }
    }


    /**
     * show total population for all zip codes
     */
    private void showTotalPopulationForAllZipCodes(){
        // get total population
        int totalPopulation = processor.getTotalPopulationForAllZipCodes();
        // show total population
        System.out.println("BEGIN OUTPUT");
        System.out.println(totalPopulation);
        System.out.println("END OUTPUT");
    }

    /**
     * show total partial or full vaccinations per capita
     */
    private void showTotalPartialOrFullVaccinationsPerCapita(){
        // prompt for partial or full
        prompt("Please type partial or full: ");
        String option = getResponse();
        // validate input
        while(!"partial".equals(option) && !"full".equals(option)){
            prompt("Invalid input, please type partial or full: ");
            option = getResponse();
        }
        LocalDate date;
        LinkedHashMap<String, Double> vaccinationsPerCapita;

        // prompt for date
        prompt("Please type a date (YYYY-MM-DD): ");
        String dateInput = getResponse();
        // validate input
        while(true){
            try{
                // parse date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(dateInput, formatter);
                // get total vaccinations per capita
                vaccinationsPerCapita = processor.getPartialOrFullVaccinationsPerCapita(option, date);
                break;
            } catch(DateTimeParseException e){
                // prompt for date
                prompt("Invalid input, please type a date (yyyy-mm-dd): ");
                dateInput = getResponse();
            }
        }

        // if no data
        if(vaccinationsPerCapita == null || vaccinationsPerCapita.isEmpty()){
            System.out.println("BEGIN OUTPUT");
            System.out.println("0");
            System.out.println("END OUTPUT");
        }
        else {
            // show total vaccinations per capita
            System.out.println("BEGIN OUTPUT");
            for(Map.Entry<String, Double> entry : vaccinationsPerCapita.entrySet()){
                System.out.printf("%s %.4f\n", entry.getKey(), entry.getValue());
            }
            System.out.println("END OUTPUT");
        }


    }

    /**
     * show average market value or total livable area
     * @param option averageMarketValue or averageTotalLivableArea
     */
    private void showAverageMarketValueOrTotalLivableArea(String option){
        // prompt for zipcode
        prompt("Please type a zipcode: ");
        String zipCode = getResponse();
        // validate input
        while(!zipCode.matches("\\d{5}")){
            prompt("Invalid input, please type a zipcode: ");
            zipCode = getResponse();
        }
        // get value
        long value = 0;
        if("averageMarketValue".equals(option)){
            // get average market value
            value = processor.getAverageMarketValue(zipCode);
        } else{
            // get average total livable area
            value = processor.getAverageTotalLivableArea(zipCode);
        }
        // print result
        System.out.println("BEGIN OUTPUT");
        System.out.println(value);
        System.out.println("END OUTPUT");
    }

    /**
     * show total market value per capita
     */
    private void showTotalMarketValuePerCapita(){
        // prompt for zipcode
        prompt("Please type a zipcode: ");
        String zipCode = getResponse();
        // validate input
        while(!zipCode.matches("\\d{5}")){
            prompt("Invalid input, please type a zipcode: ");
            zipCode = getResponse();
        }
        // get value
        long totalMarketValuePerCapita = processor.getTotalMarketValuePerCapita(zipCode);
        // print result
        System.out.println("BEGIN OUTPUT");
        System.out.println(totalMarketValuePerCapita);
        System.out.println("END OUTPUT");
    }


    private void showTotalLivableAreaMultiplyFullyVaccinatedPeopleRatio(){
        prompt("Please type a zipcode: ");
        // get zipcode
        String zipCode = getResponse();
        while(!zipCode.matches("\\d{5}")){
            prompt("Invalid input, please type a zipcode: ");
            zipCode = getResponse();
        }
        // get result
        double result = processor.getTotalLivableAreaMultiplyFullyVaccinatedPeopleRatio(zipCode);
        // print result
        System.out.println("BEGIN OUTPUT");
        System.out.printf("%.4f\n", result);
        System.out.println("END OUTPUT");
    }


    /**
     * get response from the user, log the response and return the response
     * @return the response from the user
     */
    private String getResponse() {
        // get response
        String answer = scanner.nextLine();
        // log response
        logger.log(answer);
        System.out.println();
        return answer;
    }



}
