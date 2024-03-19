package edu.upenn.cit594.logging;


import java.io.*;

/**
 * @author Xuanhe Zhang
 */
public class Logger {
    // Singleton pattern
    private static Logger instance;
    // The name of the file to write to
    private String outputFile;

    /**
     * Private constructor for the Logger class
     */
    private Logger(){
    }
    /**
     * Returns the instance of the Logger class
     * @return the instance of the Logger class
     */
    public static Logger getInstance(){
        // If the instance is null, create a new instance
        if(instance == null){
            instance = new Logger();
        }
        // Return the instance
        return instance;
    }

    /**
     * Writes a message to the log file
     * @param message   the message to write to the log file
     */

    public void log(String message) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(outputFile, true), true)) {
            long currentTime = System.currentTimeMillis();
            // Write the message to the log file
            printWriter.println(currentTime + " " + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Sets the destination of the log file
     * @param filename  the name of the file to write to
     */
    public void setDestination(String filename) {
        try {
            // If the output file is not null, close the FileWriter object
            if(outputFile != null) {
                // Create a new FileWriter object
                FileWriter fw = new FileWriter(outputFile, true);
                fw.close();
            }
            outputFile = filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the output file is set
     * @return true if the output file is set, false otherwise
     */
    public boolean isSet(){
        return outputFile != null;
    }
}
