package edu.brown.cs.student.main.server.handlers.CSVHandling;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** This class handles the viewcsv endpoint */
public class ViewCSVHandler implements Route {

  private final CSVHandling handling;

  /**
   * Takes in a handling object so that it can inform the other classes when the CSV has been parsed
   *
   * @param handling
   */
  public ViewCSVHandler(CSVHandling handling) {
    this.handling = handling;
  }

  /**
   * This puts the appropriate parsed csv into the response map on success or fails if no csv has
   * been loaded
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    if (!this.handling.isCSVLoaded()) {
      responseMap.put("result", "failure: no CSV loaded");
      return responseMap;
    }

    responseMap.put("result", "success");
    responseMap.put("data", this.handling.getParsedData());
    return responseMap;
  }
}
