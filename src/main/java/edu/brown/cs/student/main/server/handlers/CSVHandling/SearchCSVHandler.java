package edu.brown.cs.student.main.server.handlers.CSVHandling;

import edu.brown.cs.student.main.CSV.Search;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  private CSVHandling handling;

  public SearchCSVHandler(CSVHandling handling) {
    this.handling = handling;
  }

  @Override
  public Object handle(Request request, Response response) {
    String value = request.queryParams("value");
    String columnSpec = request.queryParams("index");
    String hasHeaders = request.queryParams("hasheaders");
    String numColumns = request.queryParams("numcolumns");

    Map<String, Object> responseMap = new HashMap<>();

    if (hasHeaders == null) {
      responseMap.put("result", "failure: specify whether or not there are headers");
      return responseMap;
    }

    if (!this.handling.isCSVLoaded()) {
      responseMap.put("result", "failure: no CSV loaded");
      return responseMap;
    }

    if (value == null) {
      responseMap.put("result", "failure: no value to search for specified");
      return responseMap;
    }

    boolean hasHeadersBool;
    int numColumnsInt;

    try {
      numColumnsInt = Integer.parseInt(numColumns);
    } catch (NumberFormatException e) {
      responseMap.put("result", "failure: improper integer formatting");
      return responseMap;
    }

    if (hasHeaders.equalsIgnoreCase("true") || hasHeaders.equalsIgnoreCase("false")) {
      hasHeadersBool = Boolean.parseBoolean(hasHeaders);
    } else {
      responseMap.put("result", "failure: improper boolean formatting");
      return responseMap;
    }

    List<List<String>> parsedData = this.handling.getParsedData();

    Search search = new Search(parsedData, value);
    List<String> row =
        search.callSearch(value.toLowerCase(), columnSpec, numColumnsInt, hasHeadersBool);

    responseMap.put("data", row);
    responseMap.put("value", value);
    if (columnSpec != null) {
      responseMap.put("column to search for", columnSpec);
    } else {
      responseMap.put("column to search for", "column not specified");
    }
    responseMap.put("result", "success");
    return responseMap;
  }
}
