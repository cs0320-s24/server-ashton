package edu.brown.cs.student.main.CSV;

import java.util.ArrayList;
import java.util.List;

public class Search {

  private String target;
  private final List<List<String>> parsedData;

  /**
   * This is the search class. It takes in the parsed data and searches it for a specific target
   * value
   *
   * @param parsedData - the data parsed by the CSVParser
   * @param target - the value to search for
   * @throws ArrayIndexOutOfBoundsException
   */
  public Search(List<List<String>> parsedData, String target)
      throws ArrayIndexOutOfBoundsException {
    this.target = target.toLowerCase();

    this.parsedData = parsedData;
  }

  /**
   * This method handles the actual calling of search depending on the data passed in. There are three different
   * search implementations depending on user specifications, so this decides which to call
   * @param target
   * @param columnSpec
   * @param numColumns
   * @param hasHeaders
   * @return
   */
  public List<String> callSearch(String target, String columnSpec, int numColumns, boolean hasHeaders) {
    this.target = target;
    int columnIndex;
    if (columnSpec != null) {
      try {
        columnIndex = Integer.parseInt(columnSpec);
        return this.search(columnIndex, numColumns);
      } catch (NumberFormatException e) {
        if (hasHeaders) {
          return this.search(columnSpec);
        }
      }
    }
    return this.search();
  }

  /**
   * Used for a basic search implementation with no column specification. This method loops through
   * the parsed data to find the target, printing it if it does or printing that it was not found
   */
  public List<String> search() {
    for (List<String> parsedDatum : this.parsedData) {
      for (int j = 0; j < parsedDatum.size(); j++) {
        if (parsedDatum.get(j).equalsIgnoreCase(this.target)) {
          return parsedDatum;
        }
      }
    }

    List<String> notFound = new ArrayList<>();
    notFound.add("Target was not found");
    return notFound;
  }

  /**
   * This method searches the CSV for a specific value as well, it just searches a specific column
   * index for that value
   *
   * @param columnIndex - the column to search
   * @param numColumns - expected number of columns
   * @throws ArrayIndexOutOfBoundsException
   */
  public List<String> search(int columnIndex, int numColumns)
      throws ArrayIndexOutOfBoundsException {
    if (columnIndex < 0 || columnIndex >= numColumns) {
      throw new ArrayIndexOutOfBoundsException();
    }

    for (List<String> parsedDatum : this.parsedData) {
      if (parsedDatum.get(columnIndex).toLowerCase().equals(this.target)) {
        return parsedDatum;
      }
    }

    List<String> notFound = new ArrayList<>();
    notFound.add("Target was not found");
    return notFound;
  }

  /**
   * Similar to the column index functionality, this just searches a specific header's column
   *
   * @param headerName - the header to search
   */
  public List<String> search(String headerName) {
    int columnIndex = -1;
    for (int i = 0; i < this.parsedData.get(0).size(); i++) {
      if (this.parsedData.get(0).get(i).equalsIgnoreCase(headerName)) {
        columnIndex = i;
        break;
      }
    }

    if (columnIndex == -1) {
      List<String> notFound = new ArrayList<>();
      notFound.add("Header '" + headerName + "' not found");
      return notFound;
    }

    for (int j = 1; j < this.parsedData.size(); j++) {
      if (this.parsedData.get(j).get(columnIndex).equalsIgnoreCase(this.target)) {
        return this.parsedData.get(j);
      }
    }

    List<String> notFound = new ArrayList<>();
    notFound.add("Target was not found");
    return notFound;
  }
}
