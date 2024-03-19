package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import edu.upenn.cit594.util.CovidReport;

import java.io.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Xuanhe Zhang
 */
public class CovidReportCvsParser extends CovidReportParser {
    /**
     * Constructor
     * @param filename
     */
    public CovidReportCvsParser(String filename) {
        super(filename);
    }

    /**
     * Parse the csv file
     * @return List of CovidReport
     * @throws Exception if file not found or other exceptions
     */
    public  List<CovidReport> parse() throws Exception {

        List<CovidReport> covidReportList = new ArrayList<>();
            var reader = new CharacterReader(filename);
            var csvReader = new CSVReader(reader);
            // log the filename
            Logger.getInstance().log(filename);


            String[] headline;
            headline = csvReader.readRow();

            // parse the headline to get the index of each column

            int zipCodeIndex = 0;
            int partialIndex = 0;
            int fullIndex = 0;
            int boostIndex = 0;
            int reportTimeIndex = 0;

            boolean zipCodeFound = false;
            boolean partialFound = false;
            boolean fullFound = false;
            boolean boostFound = false;
            boolean reportTimeFound = false;

            // find the index of each column

            for(int i = 0; i < headline.length; i++) {
                switch (headline[i].trim()) {
                    case "zip_code" -> {
                        zipCodeIndex = i;
                        zipCodeFound = true;
                    }
                    case "partially_vaccinated" -> {
                        partialIndex = i;
                        partialFound = true;
                    }
                    case "fully_vaccinated" -> {
                        fullIndex = i;
                        fullFound = true;
                    }
                    case "boosted" -> {
                        boostIndex = i;
                        boostFound = true;
                    }
                    case "etl_timestamp" -> {
                        reportTimeIndex = i;
                        reportTimeFound = true;
                    }
                }
            }

            // check if the headline is valid

            if(!zipCodeFound || !partialFound || !fullFound || !boostFound || !reportTimeFound) {
                throw new Exception("Missing column");
            }


            String[] content;
            // parse each row
            while ((content = csvReader.readRow()) != null) {;
                //You should ignore any records where the ZIP Code is not 5 digits or the timestamp is not in the
                //specified format


                String zipCode = content[zipCodeIndex].trim();
                String reportTime = content[reportTimeIndex].trim();
                // check if the zip code is valid
                if(!isInt(zipCode) || zipCode.length() !=5) {
                    continue;
                }
                // parse the number of partially vaccinated, fully vaccinated and booster doses
                int partiallyVaccinated;
                try{
                    partiallyVaccinated = Integer.parseInt(content[partialIndex].trim());
                }
                catch(Exception e) {
                    partiallyVaccinated = 0;
                }

                int fullyVaccinated;

                try{
                    fullyVaccinated = Integer.parseInt(content[fullIndex].trim());
                }
                catch(Exception e) {
                    fullyVaccinated = 0;
                }

                int boosterDoses;

                try{
                    boosterDoses = Integer.parseInt(content[boostIndex].trim());
                }
                catch(Exception e) {
                    boosterDoses = 0;
                }
                // check if the report time is valid
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime reportTimeLDT = LocalDateTime.parse(reportTime, formatter);
                LocalDate reportTimeLD = reportTimeLDT.toLocalDate();
                // create a new CovidReport object
                CovidReport report = new CovidReport(reportTimeLD, zipCode, partiallyVaccinated, fullyVaccinated, boosterDoses);
                // add the report to the list
                covidReportList.add(report);

            }
            // close the reader
            reader.close();

        return covidReportList;
    }


    /**
     * Check if the string is an integer
     * @param s the string to be checked
     * @return true if the string is an integer, false otherwise
     */
    public boolean isInt(String s) {
        // check if the string is an integer
        try {
            int i = Integer.parseInt(s);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

}
