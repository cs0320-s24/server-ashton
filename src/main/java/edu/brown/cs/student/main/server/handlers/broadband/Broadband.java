package edu.brown.cs.student.main.server.handlers.broadband;

import com.squareup.moshi.Json;

/**
 * A class that moshi uses to convert Json objects into. It stores the necessary info and provides a
 * string response
 */
public class Broadband {
  @Json(name = "NAME")
  private String name;

  @Json(name = "S2802_C03_022E")
  private String value;

  @Json(name = "state")
  private String state;

  @Json(name = "county")
  private String county;

  public Broadband() {}

  /**
   * This method returns a string containing a message about the broadband access based on the input
   * from the moshi deserialization
   *
   * @return
   */
  @Override
  public String toString() {
    return "The estimated broadband access in " + this.name + " is " + this.value + " percent";
  }
}
