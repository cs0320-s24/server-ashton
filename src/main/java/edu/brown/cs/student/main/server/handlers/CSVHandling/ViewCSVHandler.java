package edu.brown.cs.student.main.server.handlers.CSVHandling;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {

  private final CSVHandling handling;

  public ViewCSVHandler(CSVHandling handling) {
    this.handling = handling;
  }

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
