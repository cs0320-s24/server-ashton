package edu.brown.cs.student.main.server.handlers.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ACSData implements DataReturner {

    private StateCountyInit init;
    private final Map<String, String> stateToCode;

    public ACSData(StateCountyInit init) {
        this.init = init;
        this.stateToCode = init.getMap();
    }

    @Override
    public Map<String, Object> getData(Request request, Response response) {
        // gets the query params for state and county
        String state = request.queryParams("state");
        String county = request.queryParams("county");

        Map<String, Object> responseMap = new HashMap<>();

        if (state == null || county == null) {
            responseMap.put("result", "failure: state or county not specified");
            return responseMap;
        }

        try {
            String broadbandJson = this.sendRequest(state, county);

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

    private String sendRequest(String state, String county)
            throws URISyntaxException, IOException, InterruptedException {
        String stateCode = this.stateToCode.get(state);

        if (stateCode == null) {
            return "Location not found";
        }

        Map<String, String> countyToCode = this.init.parseCountyData(stateCode);
        String countyCode = countyToCode.get(county);

        if (countyCode == null) {
            return "Location not found";
        }

        HttpRequest buildACSApiRequest =
                HttpRequest.newBuilder()
                        .uri(
                                new URI(
                                        "https://api.census.gov/data/2021/acs/acs1/subject/"
                                                + "variables?get=NAME,S2802_C03_022E&for=county:"
                                                + countyCode
                                                + "&in=state:"
                                                + stateCode))
                        .GET()
                        .build();

        HttpResponse<String> sentACSApiResponse =
                HttpClient.newBuilder()
                        .build()
                        .send(buildACSApiRequest, HttpResponse.BodyHandlers.ofString());

        if (!Objects.equals(sentACSApiResponse.body(), "")) {
            return this.convertJson(sentACSApiResponse.body());
        }

        return null;
    }

    /**
     * This takes in the Json object returned from the API call and converts it to the more readable
     * format typically returned. Essentially creating the hashmap like format for a json.
     *
     * @param json
     * @return
     */
    private String convertJson(String json) {
        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<List<List<String>>> adapter =
                    moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));
            List<List<String>> data = adapter.fromJson(json);
            List<Map<String, String>> jsonData = new ArrayList<>();
            if (data != null && data.size() >= 2) {
                List<String> headers = data.get(0);
                for (List<String> row : data.subList(1, data.size())) {
                    Map<String, String> rowMap = new java.util.HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        rowMap.put(headers.get(i), row.get(i));
                    }
                    jsonData.add(rowMap);
                }
            }
            JsonAdapter<List<Map<String, String>>> jsonAdapter =
                    moshi.adapter(Types.newParameterizedType(List.class, Map.class));
            String serializedJson = jsonAdapter.toJson(jsonData);

            if (serializedJson.startsWith("[") && serializedJson.endsWith("]")) {
                serializedJson = serializedJson.substring(1, serializedJson.length() - 1);
            }

            return serializedJson;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
