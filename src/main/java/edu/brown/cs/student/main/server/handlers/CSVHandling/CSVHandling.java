package edu.brown.cs.student.main.server.handlers.CSVHandling;

import java.util.List;

/** This is a utility class to tell the csv related endpoints about the status of the CSV */
public class CSVHandling {

  private List<List<String>> parsedData;

  /**
   * This constructor takes in the parsed data and stores it
   *
   * @param parsedData
   */
  public CSVHandling(List<List<String>> parsedData) {
    this.parsedData = parsedData;
  }

  /**
   * This method tells whether or not a CSV has been loaded by determining whether or not the parsed
   * data is null
   *
   * @return
   */
  public boolean isCSVLoaded() {
    return this.parsedData != null;
  }

  /**
   * This allows the loadCSV class to alter the value of parsed data once a csv has been loaded
   *
   * @param parsedData
   */
  public void setParsedData(List<List<String>> parsedData) {
    this.parsedData = parsedData;
  }

  /**
   * This returns a copy of the parsed data so that it can be searched or viewed, but not modified
   *
   * @return
   */
  public List<List<String>> getParsedData() {
    List<List<String>> parsedDataCopy = this.parsedData;
    return parsedDataCopy;
  }
}
