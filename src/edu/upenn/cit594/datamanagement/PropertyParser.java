package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xuanhe Zhang
 */
public class PropertyParser {
    final String filename;

    /**
     * Constructor
     * @param filename
     */
    public PropertyParser(String filename) {
        this.filename = filename;
    }

    /**
     * parse property data
     * @return list of property
     * @throws Exception if file not found or file format is wrong
     */
    public List<Property> parse() throws Exception{


        List<Property> properties = new ArrayList<Property>();
            var reader = new CharacterReader(filename);
            var csvReader = new CSVReader(reader);

            // log filename
            Logger.getInstance().log(filename);

            // read headline
            String[] headline;
            headline = csvReader.readRow();

            // find market_value, total_livable_area and zip_code column
            int marketValueIndex=0;
            int totalLivableAreaIndex=0;
            int zipCodeIndex=0;

            boolean marketValueFound = false;
            boolean totalLivableAreaFound = false;
            boolean zipCodeFound = false;

            for(int i = 0; i < headline.length; i++) {
                if (headline[i].trim().equals("market_value")){
                    marketValueIndex = i;
                    marketValueFound = true;
                }
                else if (headline[i].trim().equals("total_livable_area")){
                    totalLivableAreaIndex = i;
                    totalLivableAreaFound = true;
                }
                else if (headline[i].trim().equals("zip_code")){
                    zipCodeIndex=i;
                    zipCodeFound = true;
                }
            }
            // if any of the three columns is missing, throw exception
            if(!marketValueFound || !totalLivableAreaFound || !zipCodeFound) {
                throw new Exception("missing market_value or total_livable_area or zip_code column");
            }
            // read each row
            String content[];
            String zipCode;
            Double marketValue;
            Double totalLivableArea;


            while ((content = csvReader.readRow()) != null) {
                // skip row if zip code is not 5 digits
                if(content[zipCodeIndex].trim().length() < 5 || !isInt(content[zipCodeIndex].trim())){
                    continue;
                }
                // zip code is the first 5 digits
                zipCode = content[zipCodeIndex].trim().substring(0,5);

                // skip row if market_value or total_livable_area is not a number
                try{
                    marketValue = Double.parseDouble(content[marketValueIndex]);
                }
                catch(Exception e) {
                    marketValue = null;
                }
                try{
                    totalLivableArea = Double.parseDouble(content[totalLivableAreaIndex]);
                }
                catch(Exception e) {
                    totalLivableArea = null;
                }


                // create a property object and add to list
                Property property = new Property(marketValue, totalLivableArea, zipCode);
                // add property to list
                properties.add(property);
            }
            // close reader
            reader.close();
            // return list of property
        return properties;

    }

    /**
     * check if a string is an integer
     * @param s string
     * @return  true if s is an integer, false otherwise
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
