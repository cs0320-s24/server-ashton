package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.handlers.broadband.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.broadband.StateCountyInit;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testConnection() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband");

    assertEquals(200, clientConnection.getResponseCode());

    clientConnection.disconnect();
  }

  @Test
  public void testFailedConnection() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broad-band");

    assertEquals(404, clientConnection.getResponseCode());

    clientConnection.disconnect();
  }
}
