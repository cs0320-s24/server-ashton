package edu.brown.cs.student.main.server.handlers.broadband;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import edu.brown.cs.student.main.server.handlers.broadband.data.DataReturner;
import spark.Request;
import spark.Response;
import spark.Route;

/** The class that handles the broadband endpoint. */
public class BroadbandHandler implements Route {

  private final DataReturner returner;

  /**
   * The constructor for broadband handler. It handles the initialization of state to state code
   * information using the StateCountyInit object passed in.
   *
   * @param returner
   */
  public BroadbandHandler(DataReturner returner) {
    this.returner = returner;
  }

  /**
   * This handle method is overridden from the route interface to handle a specific call with this
   * endpoint. It populates the response map with appropriate data based on what the user inputs for
   * the broadband endpoint.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> responseMap = new HashMap<>();

    if (state == null || county == null) {
      responseMap.put("result", "failure: state or county not specified");
      return responseMap;
    }

    try {
      String broadbandJson = this.returner.sendRequest(state, county);

      if (broadbandJson == null) {
        responseMap.put("result", "failure: error retrieving data");
        return responseMap;
      }

      if (broadbandJson.equals("Location not found")) {
        responseMap.put("failure", "Location not found");
        return responseMap;
      }

      Broadband broadband = BroadbandAPIUtilities.deserializeCensus(broadbandJson);

      responseMap.put("result", "success");
      responseMap.put("Broadband Access", broadband);

      LocalDateTime currentDateTime = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
      String formattedDateTime = currentDateTime.format(formatter);

      responseMap.put("Time of Query", formattedDateTime);

      return responseMap;
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", e.getClass());
    }

    return responseMap;
  }
}
