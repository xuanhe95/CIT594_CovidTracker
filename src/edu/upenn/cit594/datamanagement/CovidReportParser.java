package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.util.CovidReport;

import java.util.List;

/**
 * @author Xuanhe Zhang
 */
public abstract class CovidReportParser {
    // filename of the file to be parsed
    final String filename;
    // Constructor
    public CovidReportParser(String filename){
        this.filename = filename;
    }
    // parse the file
    abstract public List<CovidReport> parse() throws Exception;
}
