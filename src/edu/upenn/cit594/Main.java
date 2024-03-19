package edu.upenn.cit594;

import edu.upenn.cit594.datamanagement.*;
import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.Processor;
import edu.upenn.cit594.ui.UserInterface;

import java.util.HashMap;
import java.util.HashSet;
/**
 * @author Xuanhe Zhang
 */
public class Main {
    private static final String FORMAT = "^--(?<name>.+?)=(?<value>.+)$";
    public static void main(String[] args) {
//        The text menu explaining the actions listed above should be followed by an input prompt line. The
//        prompt line, which should be displayed any time the program wants data from the user (not just
//        after the menu) should have the form of a new line which begins with a greater than sign followed
//        5Hint: take a look at the documentation for the java.io.File class, at:
//        https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html.
//        7
//        by a space (“> ”). In order to ensure that the prompt actually appears for the user, you must flush
//        the output buffer after printing it, using the command “System.out.flush()”. If the user enters
//        anything other than an integer between 0-7, the program should show an error message and prompt
//        the user for another selection. This includes inputs such as:


        // hash set to validate args
        HashSet<String> validArgs = new HashSet<>();
        validArgs.add("--covid");
        validArgs.add("--properties");
        validArgs.add("--population");
        validArgs.add("--log");

        // hash map to store args
        HashMap<String, String> processedArgsMap = new HashMap<>();
        HashMap<String, String> rawArgsMap = new HashMap<>();


        for(String arg : args) {
            // parse args
            if(arg.matches(FORMAT)) {
                String name = arg.split("=")[0];
                String value = arg.split("=")[1];
                boolean valid = false;
                if(validArgs.contains(name)) {
                    // remove valid args from set
                    validArgs.remove(name);
                    // set valid to true
                    valid = true;
                }
                else{
                    // invalid args
                    throw new IllegalArgumentException("Invalid argument");
                }
                if(valid && name.equals("--log")) {
                    // set log file name
                    name = name.substring(2);
                    Logger.getInstance().setDestination(value);
                }
                else if(valid){
                    // put args in map
                    name = name.substring(2);
                    processedArgsMap.put(name, value);
                }
                else{
                    // invalid args
                    throw new IllegalArgumentException("Invalid argument");
                }
            }
            else{
                // invalid args
                throw new IllegalArgumentException("Invalid argument");
            }
        }

        if(!Logger.getInstance().isSet()){
            // log file not set
            System.err.println("Log file not set");
            throw new IllegalArgumentException("Log file not set");
        }

        // get file names
        String covidDataFileName = processedArgsMap.getOrDefault("covid", null);
        String populationDataFileName = processedArgsMap.getOrDefault("population", null);
        String propertyDataFileName = processedArgsMap.getOrDefault("properties", null);

        // log args
        String argsLog = "";
        for(String arg : args){
            argsLog += arg + " ";
        }
        Logger.getInstance().log(argsLog.trim());


        // create parsers
        CovidReportParser covidReportParser = null;
        // check file format
        if(covidDataFileName == null){
            covidReportParser = null;
        }
        else if(covidDataFileName.toLowerCase().endsWith(".csv")){
            // parse csv
            covidReportParser = new CovidReportCvsParser(covidDataFileName);
        }else if(covidDataFileName.toLowerCase().endsWith(".json")){
            // parse json
            covidReportParser = new CovidReportJsonParser(covidDataFileName);
        }else{
            // unsupported file format
            throw new IllegalArgumentException("Unsupported file format");
        }

        PopulationParser populationParser = populationDataFileName == null ? null : new PopulationParser(populationDataFileName);
        PropertyParser propertyParser = propertyDataFileName == null ? null : new PropertyParser(propertyDataFileName);

        // create processor
        Processor processor = new Processor(covidReportParser, populationParser, propertyParser);
        // create ui
        UserInterface ui = new UserInterface(processor);
        // start ui
        ui.start();

    }
}