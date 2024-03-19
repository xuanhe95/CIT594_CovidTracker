package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.CovidReport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Xuanhe Zhang
 */
public class CovidReportJsonParser extends CovidReportParser {
    /**
     * Constructor
     * @param filename
     */
    public CovidReportJsonParser(String filename) {
        super(filename);
    }
    /**
     * Parse the json file
     * @return List of CovidReport
     * @throws Exception if file not found or other exceptions
     */
    public List<CovidReport> parse() throws Exception {
        List<CovidReport> covidReportList = new ArrayList<>();

        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader(filename);
        // log the filename
        Logger.getInstance().log(filename);

        JSONArray array = (JSONArray) parser.parse(reader);

        // parse the headline to get the index of each column
        for(Object data: array) {
            JSONObject covidData = (JSONObject) data;
            Long zipCodeL =(Long) covidData.get("zip_code");
            String reportTime = (String) covidData.get("etl_timestamp");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime reportTimeLdt = LocalDateTime.parse(reportTime, formatter);
            LocalDate reportTimeLd = reportTimeLdt.toLocalDate();

            //You should ignore any records where the ZIP Code is not 5 digits or the timestamp is not in the
            //specified format

            // check if the zip code is valid
            if( zipCodeL < 100000 && zipCodeL >= 0){
                int zipCodeInt = zipCodeL.intValue();
                StringBuilder zipCode = new StringBuilder(String.valueOf(zipCodeInt));
                // add 0 to the front of the zip code if it is less than 5 digits
                while(zipCode.length() < 5){
                    zipCode.insert(0, "0");
                }
                // get the number of partially vaccinated, fully vaccinated and booster doses
                Long  partiallyVaccinatedL = (Long)covidData.get("partially_vaccinated");
                Long fullyVaccinatedL = (Long) covidData.get("fully_vaccinated");
                Long boosterDosesL = (Long)covidData.get("boosted");

                // if the number is null, set it to 0
                if(partiallyVaccinatedL == null){
                    partiallyVaccinatedL = 0L;
                }
                if(fullyVaccinatedL == null){
                    fullyVaccinatedL = 0L;
                }
                if(boosterDosesL == null){
                    boosterDosesL = 0L;
                }
                // convert the number to int
                int partiallyVaccinated = partiallyVaccinatedL.intValue();
                int fullyVaccinated = fullyVaccinatedL.intValue();
                int boosterDoses = boosterDosesL.intValue();
                // create a new CovidReport object
                CovidReport report = new CovidReport(reportTimeLd, zipCode.toString(), partiallyVaccinated, fullyVaccinated, boosterDoses);
                // add the report to the list
                covidReportList.add(report);
            }
        }

        // close the reader
        reader.close();
        // return the list
        return covidReportList;
    }
}
