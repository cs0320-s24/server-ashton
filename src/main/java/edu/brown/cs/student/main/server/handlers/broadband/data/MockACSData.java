package edu.brown.cs.student.main.server.handlers.broadband.data;

import edu.brown.cs.student.main.server.handlers.broadband.data.DataReturner;

import java.io.IOException;
import java.net.URISyntaxException;

/** Class for mocking data return */
public class MockACSData implements DataReturner {

  private final String mockedResponse;

  /** Constructor for mock data, takes in the response map to return */
  public MockACSData(String mockedResponse) {
    this.mockedResponse = mockedResponse;
  }

  /**
   * Overriden method used to mock ACS data
   *
   * @return
   */
  @Override
  public String sendRequest(String state, String county)
      throws URISyntaxException, IOException, InterruptedException {
    return this.mockedResponse;
  }
}
