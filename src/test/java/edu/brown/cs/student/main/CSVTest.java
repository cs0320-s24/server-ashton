package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.handlers.CSVHandling.CSVHandling;
import edu.brown.cs.student.main.server.handlers.CSVHandling.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandling.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandling.ViewCSVHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class CSVTest {

  @BeforeAll
  public static void setup_before_everything() {
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() {
    CSVHandling csvHandler = new CSVHandling(null);

    Spark.get("loadcsv", new LoadCSVHandler(csvHandler));
    Spark.get("viewcsv", new ViewCSVHandler(csvHandler));
    Spark.get("searchcsv", new SearchCSVHandler(csvHandler));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.awaitStop();
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  private String getResponse(HttpURLConnection connection) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    return response.toString();
  }

  @Test
  public void testConnection() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("viewcsv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("idk");
    assertEquals(404, clientConnection.getResponseCode());

    clientConnection.disconnect();
  }

  @Test
  public void testLoadBasic() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(this.getResponse(clientConnection), "{result=success}");
  }

  @Test
  public void testInvalidFilepath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/tenstar.csv");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(this.getResponse(clientConnection), "{result=failure: failed to find file}");
  }

  @Test
  public void testNoFilepath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(this.getResponse(clientConnection), "{result=failure: no filepath specified}");
  }

  @Test
  public void testViewNoLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("viewcsv");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(this.getResponse(clientConnection), "{result=failure: no CSV loaded}");
  }

  @Test
  public void testSearchNoLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(this.getResponse(clientConnection), "{result=failure: no CSV loaded}");
  }

  @Test
  public void testViewOnNewLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("viewcsv");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection), "{result=success, data=[[1, 2, 3], [4, 5, 6]]}");
  }

  @Test
  public void testSearchOnNewLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?hasheaders=true&value=1&numcolumns=3");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, data=[1, 2, 3], column to search for=column not specified, value=1}");
  }

  @Test
  public void testSearchWithNoResult() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?hasheaders=true&value=45&numcolumns=3");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, data=[Target was not found], column to search for=column not specified, value=45}");
  }

  @Test
  public void testSearchWithNoHeaderSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?value=45&numcolumns=3");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection),
        "{result=failure: specify whether or not there are headers}");
  }

  @Test
  public void testSearchWithNumColumnsNotSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?value=45&hasheaders=false");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection), "{result=failure: specify the number of columns}");
  }

  @Test
  public void testSearchWithNumValueNotSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?&hasheaders=false&numcolumns=3");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection), "{result=failure: no value to search for specified}");
  }

  @Test
  public void testSearchWithImproperNumColumnsFormat() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?value=4&hasheaders=false&numcolumns=3h&index=0");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection), "{result=failure: improper integer formatting}");
  }

  @Test
  public void testSearchWithImproperBooleanFormat() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?value=4&hasheaders=falte&numcolumns=3&index=0");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection), "{result=failure: improper boolean formatting}");
  }

  @Test
  public void testSearchNotFoundWithColumnSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/simple.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?value=4&hasheaders=false&numcolumns=3&index=1");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, data=[Target was not found], column to search for=1, value=4}");
  }

  @Test
  public void testSearchFoundWithHeaderSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection =
        tryRequest("searchcsv?value=Sol&hasheaders=true&numcolumns=5&index=ProperName");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, data=[0, Sol, 0, 0, 0], column to search for=ProperName, value=Sol}");
  }

  @Test
  public void testSearchNotFoundWithHeaderSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchcsv?value=Sol&hasheaders=true&numcolumns=5&index=StarID");
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, data=[Target was not found], column to search for=StarID, value=Sol}");
  }
}
