package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateCountyInit {

  private Map<String, String> stateToCode;

  public StateCountyInit() {
    try {
      this.stateToCode = this.parseStateData();
    } catch (Exception e) {
      System.out.println("Error parsing state data");
    }
  }

  private Map<String, String> parseStateData()
      throws URISyntaxException, IOException, InterruptedException {
    Map<String, String> stateCodeMap = new HashMap<>();

    String jsonResponse = sendStateRequest();

    Moshi moshi = new Moshi.Builder().build();

    Type listType = Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

    List<List<String>> stateDataList = adapter.fromJson(jsonResponse);

    if (stateDataList != null) {
      for (List<String> stateData : stateDataList) {
        if (stateData.size() >= 2) {
          String stateName = stateData.get(0);
          String stateCode = stateData.get(1);
          stateCodeMap.put(stateName, stateCode);
        }
      }
    }

    return stateCodeMap;
  }

  public Map<String, String> parseCountyData(String state) throws IOException {
    Map<String, String> countyCodeMap = new HashMap<>();

    Moshi moshi = new Moshi.Builder().build();

    String countyResponse;

    try {
      countyResponse = this.sendCountyRequest(state);
    } catch (Exception e) {
      return countyCodeMap;
    }

    Type listType = Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

    List<List<String>> countyDataList = adapter.fromJson(countyResponse);

    if (countyDataList != null) {
      for (List<String> countyData : countyDataList) {
        if (countyData.size() >= 3) {
          String countyName = countyData.get(0);
          String countyCode = countyData.get(2);
          countyCodeMap.put(countyName, countyCode);
        }
      }
    }

    return countyCodeMap;
  }

  private String sendStateRequest() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildACSApiRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    HttpResponse<String> sentACSApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

    return sentACSApiResponse.body();
  }

  private String sendCountyRequest(String state)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildACSApiRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for="
                        + "county:*&in=state:"
                        + state))
            .GET()
            .build();

    HttpResponse<String> sentACSApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

    return sentACSApiResponse.body();
  }

  public Map<String, String> getMap() {
    this.stateToCode.remove("NAME");
    return Map.copyOf(this.stateToCode);
  }
}
