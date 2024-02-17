package edu.brown.cs.student.main;

import edu.brown.cs.student.main.server.handlers.CSVHandling.CSVHandling;
import edu.brown.cs.student.main.server.handlers.CSVHandling.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandling.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandling.ViewCSVHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVTesting {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
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

  private String getResponse (HttpURLConnection connection) throws IOException {
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
}
