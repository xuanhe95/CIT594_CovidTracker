package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.util.CovidReport;
import edu.upenn.cit594.util.Population;
import edu.upenn.cit594.util.Property;

import java.time.LocalDate;
import java.util.*;

/**
 * @author Xuanhe Zhang
 */
public class Database {
    // cache for all data
    private Map<String, List<CovidReport>> covidReportsCache;
    private Map<String, Population> populationsCache;
    private Map<String, List<Property>> propertiesCache;
    private Map<String, List<CovidReport>> covidReportsByDate;

    /**
     * constructor for database
     */
    public Database(){
        covidReportsCache = new HashMap<>();
        populationsCache = new HashMap<>();
        propertiesCache = new HashMap<>();
        covidReportsByDate = new HashMap<>();
    }

    /**
     * add record to database
     * @param record   record to be added
     */
    public void add(Record record){
        // add record to corresponding cache
        if(record instanceof CovidReport){
            covidReportsCache.put(((CovidReport) record).zipCode(), covidReportsCache.getOrDefault(((CovidReport) record).zipCode(), new ArrayList<>()));
            covidReportsCache.get(((CovidReport) record).zipCode()).add((CovidReport) record);
            covidReportsByDate.put(((CovidReport) record).reportTime().toString(), covidReportsByDate.getOrDefault(((CovidReport) record).reportTime().toString(), new ArrayList<>()));
            covidReportsByDate.get(((CovidReport) record).reportTime().toString()).add((CovidReport) record);
        }else if(record instanceof Population){
            populationsCache.put(((Population) record).zipCode(), (Population) record);
        }else if(record instanceof Property){
            List<Property> properties = propertiesCache.getOrDefault(((Property) record).zipCode(), new ArrayList<>());
            properties.add((Property) record);
            propertiesCache.put(((Property) record).zipCode(), properties);
        }
    }

    /**
     * get covid reports by zip code
     * @param zipCode  zip code
     * @return  list of covid reports
     */
    public List<CovidReport> getCovidReportsByZipCode(String zipCode){
        return covidReportsCache.getOrDefault(zipCode, null);
    }

    /**
     * get population by zip code
     * @param zipCode  zip code
     * @return  population
     */
    public Population getPopulationByZipCode(String zipCode){
        return populationsCache.getOrDefault(zipCode, null);
    }

    /**
     * get properties by zip code
     * @param zipCode zip code
     * @return list of properties
     */

    public List<Property> getPropertiesByZipCode(String zipCode){
        return propertiesCache.getOrDefault(zipCode, null);
    }


    /**
     * get all populations
     * @return map of zip code to population
     */
    public Map<String, Population> getAllPopulations(){
        return populationsCache;
    }


    /**
     * get covid reports by date
     * @param date date
     * @return list of covid reports
     */
    public List<CovidReport> getCovidReportsByDate(LocalDate date){
        return covidReportsByDate.getOrDefault(date.toString(),  new ArrayList<>());
    }
}
