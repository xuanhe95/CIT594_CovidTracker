package edu.upenn.cit594.processor;

import edu.upenn.cit594.datamanagement.*;
import edu.upenn.cit594.util.CovidReport;
import edu.upenn.cit594.util.Population;
import edu.upenn.cit594.util.Property;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xuanhe Zhang
 */
public class Processor {
    // database
    private final Database database;
    // parser
    private CovidReportParser covidReportParser = null;
    private PopulationParser populationParser = null;
    private PropertyParser propertyParser = null;
    // cache for dynamic programming
    private Integer totalPopulationForAllZipCodesCache;
    private HashMap<String, LinkedHashMap<String, Double>> partialVaccinationsPerCapitaCache;
    private HashMap<String, LinkedHashMap<String, Double>> fullVaccinationsPerCapitaCache;
    private Map<String, Long> averageMarketValueCache;
    private Map<String, Long> averageTotalLivableAreaCache;
    private Map<String, Long> totalMarketValuePerCapitaCache;

    // whether parser is set
    private boolean isCovidReportParserSet = false;
    private boolean isPopulationParserSet = false;
    private boolean isPropertyParserSet = false;


    /**
     * constructor for processor
     * @param covidReportParser     covid report parser
     * @param populationParser      population parser
     * @param propertyParser        property parser
     */
    public Processor(CovidReportParser covidReportParser, PopulationParser populationParser, PropertyParser propertyParser){
        this.database = new Database();
        averageMarketValueCache = new HashMap<>();
        averageTotalLivableAreaCache = new HashMap<>();
        totalMarketValuePerCapitaCache = new HashMap<>();
        partialVaccinationsPerCapitaCache = new HashMap<>();
        fullVaccinationsPerCapitaCache = new HashMap<>();
        this.covidReportParser = covidReportParser;
        this.populationParser = populationParser;
        this.propertyParser = propertyParser;
        try{
            if(this.covidReportParser != null){
                // parse covid report
                List<CovidReport> covidReports = this.covidReportParser.parse();
                // add to database
                for(CovidReport covidReport : covidReports){
                    database.add(covidReport);
                }
                // set flag
                isCovidReportParserSet = true;
            }
            else{
                System.err.println("Covid Report Parser is null");
            }
        }
        catch(Exception e){
            System.err.println("Covid Report Parser parse error");
        }

        try {
            if (this.populationParser != null) {
                // parse population
                List<Population> populations = this.populationParser.parse();
                // add to database
                for (Population population : populations) {
                    database.add(population);
                }
                // set flag
                isPopulationParserSet = true;
            }
            else{
                System.err.println("Population Parser is null");
            }
        } catch(Exception e) {
            System.err.println("Population Parser parse error");
        }

        try{
            if(this.propertyParser != null){
                // parse property
                List<Property> properties = this.propertyParser.parse();
                // add to database
                for(Property property : properties){
                    database.add(property);
                }
                // set flag
                isPropertyParserSet = true;
            }
            else{
                System.err.println("Property Parser is null");
            }
        }
        catch (Exception e){
            System.err.println("Property Parser parse error");
        }
    }


    public int getAvailableActions(){
        // masks
        int covidMask = isCovidReportParserSet ? 1 : 0;
        int propertyMask = isPropertyParserSet ? 1 : 0;
        int populationMask = isPopulationParserSet ? 1 : 0;
        int mask = 0;
        // set mask
        mask |= covidMask;
        mask |= propertyMask << 1;
        mask |= populationMask << 2;
        return mask;
    }

    public boolean isAvailableAction(int action){
        // masks
        int mask = getAvailableActions();
        // check mask
        switch(mask){
            case 0b000:
                if(action == 0 || action == 1){
                    return true;
                }
                break;
            case 0b001:
                if(action == 0 || action == 1){
                    return true;
                }
                break;
            case 0b010:
                if(action == 0 || action == 1 || action == 4 || action == 5){
                    return true;
                }
                break;
            case 0b011:
                if(action == 0 || action == 1 || action == 4 || action == 5){
                    return true;
                }
                break;
            case 0b100:
                if(action == 0 || action == 1 || action == 2){
                    return true;
                }
                break;
            case 0b101:
                if(action == 0 || action == 1 || action == 2 || action == 3){
                    return true;
                }
                break;
            case 0b110:
                if(action == 0 || action == 1 || action == 2 || action == 4 || action == 5 || action == 6){
                    return true;
                }
                break;
            case 0b111:
                return true;
        }
        return false;
    }


    //    3.2 Total Population for All ZIP Codes
//    If the user enters a 2 at the main menu, the program should display the total population for all of
//    the ZIP Codes in the population input file.
//    Your program must not write any other information to the console. It must only display
//    the total population, i.e., the sum of the populations of all ZIP Codes in the input file, and then it
//    should display the main menu and await the next input.
//    Hint! For this feature, your program should print 1603797 when run on the data files we have
//    provided. If it does not print this, then your program is not working correctly. This is the only
//    feature for which we will provide the correct output in advance! Each group must determine for
//    themselves what the correct output should be for other parts of this assignment.

    /**
     * get total population for all zip codes
     * @return total population
     */

    public int getTotalPopulationForAllZipCodes() {
        // check cache
        if(totalPopulationForAllZipCodesCache != null){
            return totalPopulationForAllZipCodesCache;
        }
        // get population map
        Map<String, Population> populationMap = database.getAllPopulations();
        int totalPopulation = 0;
        // calculate total population
        for (Population population : populationMap.values()) {
            totalPopulation += population.population();
        }
        // set cache
        totalPopulationForAllZipCodesCache = totalPopulation;
        return totalPopulation;
    }

//    3.3 Total Partial or Full Vaccinations Per Capita
//    If the user enters a 3 at the main menu, your program should prompt the user to type “partial”
//    or “full” by printing a question to this effect followed by an input prompt line (as specified above).
//    Once the user inputs a valid response, your program should then prompt the user to type in a date
//    in the format: YYYY-MM-DD. After receiving a valid response, your program should display (to
//    the console) the total number of partial or full vaccinations per capita for each ZIP Code on that
//    day, i.e., the total number of vaccinations for the specified day divided by the population of that
//    ZIP Code, as provided in the population input file.


    /**
     * get partial or full vaccinations per capita
     * @param option    partial or full
     * @return  map of zip code and vaccinations per capita
     */
    public LinkedHashMap<String, Double> getPartialOrFullVaccinationsPerCapita(String option, LocalDate date) {
        // option partial or full
        // check cache
        if("partial".equals(option) && partialVaccinationsPerCapitaCache.containsKey(date.toString())){
            return partialVaccinationsPerCapitaCache.get(date.toString());
        }
        else if("full".equals(option) && fullVaccinationsPerCapitaCache.containsKey(date.toString())){
            return fullVaccinationsPerCapitaCache.get(date.toString());
        }
        // get covid reports
        Map<String, Double> tempMap = new HashMap<>();
        List<CovidReport> covidReports = database.getCovidReportsByDate(date);
        // calculate vaccinations per capita
        for(CovidReport covidReport : covidReports){
            int vaccinated;
            if("partial".equals(option)){
                vaccinated = covidReport.partiallyVaccinated();
            }else{
                vaccinated = covidReport.fullyVaccinated();
            }
            // get population
            Population populationReport = database.getPopulationByZipCode(covidReport.zipCode());
            if(populationReport == null){
                continue;
            }
            int population = populationReport.population();
            // calculate vaccinations per capita
            tempMap.put(covidReport.zipCode(), (double) vaccinated / population);
        }
        // sort map
        LinkedHashMap<String, Double> result = tempMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        // set cache
        if("partial".equals(option)){
            partialVaccinationsPerCapitaCache.put(date.toString(), result);
        }else{
            fullVaccinationsPerCapitaCache.put(date.toString(), result);
        }
        // return result
        return result;
    }

//    3.4 Average Market Value
//    If the user enters a 4 at the main menu, your program should then prompt the user to enter a 5-digit
//    ZIP Code, by printing a question to that effect, followed by the input prompt line specified above.
//    Your program should then display (to the console) the average market value for properties in that
//    ZIP Code, i.e., the total market value for all properties in the ZIP Code divided by the number of
//    properties.
//    Note that you are dividing by the number of properties in that ZIP Code as listed in the property
//    values input file, not by the population of that ZIP Code from the population input file.
//    The average market value that your program displays must be truncated to an integer (not
//    rounded!), and your program should display 0 if there are no properties in that ZIP Code listed in
//    the properties input file.

    /**
     * get average market value
     * @param zipCode   zip code
     * @return  average market value
     */
    public long getAverageMarketValue(String zipCode) {
        // check cache
        if(averageMarketValueCache.getOrDefault(zipCode, null) != null){
            return averageMarketValueCache.get(zipCode);
        }
        // get properties
        List<Property> properties = database.getPropertiesByZipCode(zipCode);
        // if no properties, return 0
        if(properties == null || properties.isEmpty()) {
            averageMarketValueCache.put(zipCode, 0L);
            return 0;
        }
        // calculate average market value
        long totalValue = 0L;
        int count = 0;
        for(Property property : properties){
            // if market value is null, skip
            if(property.marketValue() != null) {
                totalValue += property.marketValue();
                count++;
            }
        }
        // if count is 0, return 0
        if(count == 0){
            averageMarketValueCache.put(zipCode, 0L);
            return 0;
        }
        // set cache
        long result = (totalValue / count);
        averageMarketValueCache.put(zipCode, result);
        return result;
    }

//    3.5 Average Total Livable Area
//    If the user enters a 5 at the main menu, your program should then prompt the user to enter a 5-digit
//    ZIP Code, by printing a question to that effect, followed by the input prompt line specified above.
//    Your program should then display (to the console) the average total livable area for properties in
//    that ZIP Code, i.e., the sum of the total livable areas for all properties in the ZIP Code divided by
//    the number of properties.
//    Note that you are dividing by the number of properties in that ZIP Code as listed in the property
//    values input file, and not the population of that ZIP Code from the population input file.
//    The average total livable area must be displayed as a truncated integer, and your program should
//    display 0 if there are no properties in that ZIP Code listed in the properties input file.
//    Because this part of the assignment is essentially the same as the Average Market Value, with just
//    a minor change, you are expected to use the Strategy design pattern in your implementation,
//    as discussed below in subsection 4.2.

    /**
     * get average total livable area
     * @param zipCode   zip code
     * @return  average total livable area
     */

    public long getAverageTotalLivableArea(String zipCode) {
        // check cache
        if(averageTotalLivableAreaCache.getOrDefault(zipCode, null) != null){
            return averageTotalLivableAreaCache.get(zipCode);
        }
        // get properties
        List<Property> properties = database.getPropertiesByZipCode(zipCode);
        // if no properties, return 0
        if(properties == null || properties.isEmpty()){
            averageTotalLivableAreaCache.put(zipCode, 0L);
            return 0;
        }
        // calculate average total livable area
        long totalLivableArea = 0L;
        int count = 0;
        for(Property property : properties){
            if(property.totalLivableArea() != null){
                totalLivableArea += property.totalLivableArea();
                count++;
            }
        }
        // if count is 0, return 0
        if(count == 0){
            averageTotalLivableAreaCache.put(zipCode, 0L);
            return 0;
        }
        // set cache
        long result = (totalLivableArea / count);
        averageTotalLivableAreaCache.put(zipCode, result);
        // return result
        return result;
    }

//    3.6 Total Market Value Per Capita
//    If the user enters a 6 at the main menu, your program should then prompt the user to enter a 5-digit
//    ZIP Code, by printing a question to that effect, followed by the input prompt line specified above.
//    Your program should then display (to the console) the total market value per capita for that ZIP
//    Code, i.e., the total market value for all properties in the ZIP Code divided by the population of
//    that ZIP Code, as provided in the population input file.
//    The market value of properties per capita must be displayed as a truncated integer, and your
//    program should display 0 if the total market value of properties in the ZIP Code is 0, if the
//    population of the ZIP Code is 0, or if the user enters a ZIP Code that is not listed in the population
//    or properties input file.

    /**
     * get total market value per capita
     * @param zipCode   zip code
     * @return total market value per capita
     */
    public long getTotalMarketValuePerCapita(String zipCode) {
        // check cache
        if(totalMarketValuePerCapitaCache.getOrDefault(zipCode, null) != null){
            return totalMarketValuePerCapitaCache.get(zipCode);
        }
        // get properties
        List<Property> properties = database.getPropertiesByZipCode(zipCode);
        // if no properties, return 0
        if(properties == null || properties.isEmpty()){
            totalMarketValuePerCapitaCache.put(zipCode, 0L);
            return 0;
        }
        long totalValue = 0L;

        // calculate total market value
        for(Property property : properties){
            // if market value is null, skip
            if(property.marketValue() != null){
                totalValue += property.marketValue();
            }
        }
        // get population
        Population population = database.getPopulationByZipCode(zipCode);
        // if population is null, return 0
        if(population == null || population.population() == 0){
            totalMarketValuePerCapitaCache.put(zipCode, 0L);
            return 0;
        }
        // set cache
        long result = (totalValue / population.population());
        totalMarketValuePerCapitaCache.put(zipCode, result);
        // return result
        return result;
    }

//    3.7 Additional Feature
//    If the user enters a 7 at the main menu, your program should then perform a custom operation
//    chosen by your group, and then display the main menu and await the next input.
//    You may do anything you like for this feature as long as it performs some computation involving
//    all three data sets: the population, COVID data, and property values.
//    You are not restricted to only using the fields in those data sets that are described above, but note
//    that they all use ZIP Codes, so that is likely to be the data you use to join them all together.
//    For this feature, you may prompt the user to input one or more values, or you can just perform the
//    operation without any additional inputs.
//    If you have trouble thinking of an additional feature, or are not sure whether your feature satisfies
//    the above requirements, please post a public note in the discussion forum so that the TAs can
//    answer it and so that your classmates can see the reply.
//    Whatever feature you choose to implement, you will have to document the intent of the feature
//            (i.e., what it’s computing) and how you know you have implemented it correctly. See section 5 for
//    more details.

    public double getTotalLivableAreaMultiplyFullyVaccinatedPeopleRatio(String zipCode){
        // get properties
        List<Property> properties = database.getPropertiesByZipCode(zipCode);
        // if no properties, return 0
        if(properties == null || properties.isEmpty()){
            return 0;
        }
        // calculate total livable area
        long totalLivableArea = 0;

        for(Property property : properties){
            // if market value is null, skip
            if(property.totalLivableArea() != null){
                totalLivableArea += property.totalLivableArea();
            }
        }

        // get max fully vaccinated
        int maxFullyVaccinated = 0;

        // get covid reports
        List<CovidReport> covidReports = database.getCovidReportsByZipCode(zipCode);
        // get max fully vaccinated
        for(CovidReport covidReport : covidReports){
            maxFullyVaccinated = Math.max(maxFullyVaccinated, covidReport.fullyVaccinated());
        }
        // get population
        int population = database.getPopulationByZipCode(zipCode).population();
        if(population == 0){
            // if population is 0, return 0
            return 0;
        }
        // return result
        return totalLivableArea * (maxFullyVaccinated * 1.0 / population);

    }




}
