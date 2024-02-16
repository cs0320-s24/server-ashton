package edu.brown.cs.student.main.server.handlers.broadband;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class BroadbandHandler implements Route {

  @Override
  public Object handle(Request request, Response response) {
    //gets the query params for state and county
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> responseMap = new HashMap<>();
    try {
      String activityJson = this.sendRequest(state, county);
      Broadband broadband = BroadbandAPIUtilities.deserializeActivity(activityJson);

      responseMap.put("result", "success");
      responseMap.put("broadband", broadband);

      return responseMap;
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "Exception");
    }

    return responseMap;
  }

  private String sendRequest(String state, String county)
          throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildACSApiRequest =
            HttpRequest.newBuilder()
                    .uri(new URI("https://api.census.gov/data/2021/acs/acs1/subject/" +
                            "variables?get=NAME,S2802_C03_022E&for=county:" + county + "&in=state:" + state + "="))
                    .GET()
                    .build();

    HttpResponse<String> sentACSApiResponse =
            HttpClient.newBuilder()
                    .build()
                    .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

    System.out.println(sentACSApiResponse);
    System.out.println(sentACSApiResponse.body());

    return sentACSApiResponse.body();
  }
}
