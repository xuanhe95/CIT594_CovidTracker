package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.Population;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Xuanhe Zhang
 */
public class PopulationParser {
    final String filename;

    /**
     * Constructor
     * @param filename
     */
    public PopulationParser(String filename) {
        this.filename = filename;
    }

    /**
     * parse population data
     * @return list of population
     * @throws Exception if file not found or file format is wrong
     */

    public List<Population> parse() throws Exception {
        List<Population> populations= new ArrayList<>();

        var reader = new CharacterReader(filename);
        var csvReader = new CSVReader(reader);

        // log filename
        Logger.getInstance().log(filename);

        String[] headline;

        // read headline
        headline = csvReader.readRow();

        HashMap<String,Integer> popMap = new HashMap<>();

        // find zip_code and population column
        int zipCodeIndex = 0;
        int popIndex = 0;

        boolean foundZipCode = false;
        boolean foundPopulation = false;

        for(int i = 0; i < headline.length; i++) {
            if (headline[i].trim().equals("zip_code")){
                zipCodeIndex = i;
                foundZipCode = true;
            }
            else if (headline[i].trim().equals("population")){
                popIndex = i;
                foundPopulation = true;
            }
        }
        // throw exception if zip_code or population column not found
        if(!foundZipCode || !foundPopulation) {
            throw new Exception("missing zip_code or population column");
        }
        // read each row
        String[] content;
        String zipCode;
        int population = 0;

        while ((content = csvReader.readRow()) != null) {
            // skip row if zip_code is not 5 digits or population is not an integer
            if((content[zipCodeIndex].trim().length() !=5) || !isInt(content[popIndex])) {
                continue;
            }
            // get zip_code and population
            zipCode = (content[zipCodeIndex].trim());

            try {
                // if population is not an integer, set it to 0
                population = Integer.parseInt(content[popIndex].trim());
            }
            catch(Exception e)
            {
                population = 0;
            }
            // add population to list
            Population pop = new Population(zipCode, population);
            // add population to map
            populations.add(pop);
        }

        // close reader
        reader.close();

        // return list of population
        return populations;
    }

    /**
     * check if a string is an integer
     * @param s string to be checked
     * @return true if it is an integer, false otherwise
     */
    public boolean isInt(String s) {
        try {
            int i = Integer.parseInt(s);
            return true;
        }

        catch(Exception e) {
            return false;
        }
    }
}
