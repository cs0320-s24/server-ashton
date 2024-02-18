package edu.brown.cs.student.main.server.handlers.broadband.data;

import java.io.IOException;
import java.net.URISyntaxException;

/** An interface that defines how data should be retrieved */
public interface DataReturner {

  // the method that returns the data that must be overridden
  String sendRequest(String state, String county)
      throws URISyntaxException, IOException, InterruptedException;
}
