package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.handlers.broadband.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.broadband.StateCountyInit;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class ACSTest {
  @BeforeAll
  public static void setup_before_everything() {
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() {
    Spark.get("broadband", new BroadbandHandler(new StateCountyInit()));
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
    HttpURLConnection clientConnection = tryRequest("broad-band");

    assertEquals(404, clientConnection.getResponseCode());

    clientConnection.disconnect();
  }

  @Test
  public void testBasicQuery() throws IOException {
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

  @Test
  public void testLocationNotFound() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=%20Carolina&county=York" + "%20County,%20South%20Carolina");
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(this.getResponse(clientConnection), "{failure=Location not found}");
  }
}
