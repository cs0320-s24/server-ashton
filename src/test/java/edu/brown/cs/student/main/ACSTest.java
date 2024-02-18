package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.handlers.broadband.data.ACSData;
import edu.brown.cs.student.main.server.handlers.broadband.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.broadband.data.MockACSData;
import edu.brown.cs.student.main.server.handlers.broadband.data.StateCountyInit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class ACSTest {
  @BeforeAll
  public static void setup_before_everything() {
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /** Setup for a real API connection */
  public void setupReal() {
    Spark.get("broadband", new BroadbandHandler(new ACSData(new StateCountyInit())));
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * Setup for a mocked connection
   *
   * @param mockedResponse
   */
  public void setupMock(String mockedResponse) {
    Spark.get("broadband", new BroadbandHandler(new MockACSData(mockedResponse)));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("broadband");
    Spark.awaitStop();
  }

  /**
   * Helper function for making a request to the API
   *
   * @param apiCall
   * @return
   * @throws IOException
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Helper function for converting the response to a string
   *
   * @param connection
   * @return
   * @throws IOException
   */
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

  /**
   * Helper function for getting the time of the query
   *
   * @return
   */
  private String getTime() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
    String formattedDateTime = currentDateTime.format(formatter);
    return formattedDateTime;
  }

  /**
   * Test for testing for successful connection
   *
   * @throws IOException
   */
  @Test
  public void testConnection() throws IOException {
    this.setupReal();
    HttpURLConnection clientConnection = tryRequest("broadband");

    assertEquals(200, clientConnection.getResponseCode());

    clientConnection.disconnect();
  }

  /**
   * Test for testing for unsuccessful connection
   *
   * @throws IOException
   */
  @Test
  public void testFailedConnection() throws IOException {
    this.setupReal();
    HttpURLConnection clientConnection = tryRequest("broad-band");

    assertEquals(404, clientConnection.getResponseCode());

    clientConnection.disconnect();
  }

  /**
   * Test for a basic query
   *
   * @throws IOException
   */
  @Test
  public void testBasicQuery() throws IOException {
    this.setupReal();
    HttpURLConnection clientConnection =
        tryRequest(
            "broadband?state=South%20Carolina&county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, Time of Query="
            + this.getTime()
            + ", Broadband Access=The estimated broadband access in York County, "
            + "South Carolina is 91.6 percent}");
  }

  /**
   * Test for what should happen when the location is not found
   *
   * @throws IOException
   */
  @Test
  public void testLocationNotFound() throws IOException {
    this.setupReal();
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=%20Carolina&county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(this.getResponse(clientConnection), "{failure=Location not found}");
  }

  /**
   * Testing for location not found using teh mocked source
   *
   * @throws IOException
   */
  @Test
  public void testStateAndCountyNotGiven() throws IOException {
    this.setupMock("Location not found");
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=bruh%20Carolina&county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(this.getResponse(clientConnection), "{failure=Location not found}");
  }

  /**
   * Testing the handler using a mock json
   *
   * @throws IOException
   */
  @Test
  public void testHandler() throws IOException {
    this.setupMock(
        "{\"county\":\"091\",\"state\":\"45\",\"S2802_C03_022E\":\"91.6\",\"NAME\":\"York County, "
            + "South Carolina\"}");
    HttpURLConnection clientConnection =
        tryRequest(
            "broadband?state=South%20Carolina&county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, Time of Query="
            + this.getTime()
            + ", Broadband Access=The estimated broadband access in York County, "
            + "South Carolina is 91.6 percent}");
  }

  /**
   * Testing the handler further using a mock json for a different location
   *
   * @throws IOException
   */
  @Test
  public void testHandlerNewLoc() throws IOException {
    this.setupMock(
        "{\"county\":\"037\",\"state\":\"06\",\"S2802_C03_022E\":\"89.9\",\"NAME\":\"Los Angeles County, "
            + "California\"}");
    HttpURLConnection clientConnection =
        tryRequest(
            "broadband?state=South%20Carolina&county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(
        this.getResponse(clientConnection),
        "{result=success, Time of Query="
            + this.getTime()
            + ", Broadband Access=The estimated broadband access in Los Angeles County, "
            + "California is 89.9 percent}");
  }

  /**
   * Testing the handler for returning a null json using the mocked data
   *
   * @throws IOException
   */
  @Test
  public void testNullJson() throws IOException {
    this.setupMock(null);
    HttpURLConnection clientConnection =
        tryRequest(
            "broadband?state=South%20Carolina&county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(this.getResponse(clientConnection), "{result=failure: error retrieving data}");
  }

  /**
   * Testing for no state specified
   *
   * @throws IOException
   */
  @Test
  public void testNoState() throws IOException {
    this.setupMock(null);
    HttpURLConnection clientConnection =
        tryRequest("broadband?county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(
        this.getResponse(clientConnection), "{result=failure: state or county not specified}");
  }

  /**
   * Testing for no county specified
   *
   * @throws IOException
   */
  @Test
  public void testNoCounty() throws IOException {
    this.setupMock(null);
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=South%20Carolina" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(
        this.getResponse(clientConnection), "{result=failure: state or county not specified}");
  }
}
