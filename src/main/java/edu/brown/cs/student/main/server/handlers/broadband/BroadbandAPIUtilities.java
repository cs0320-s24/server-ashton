package edu.brown.cs.student.main.server.handlers.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;

public class BroadbandAPIUtilities {

  /**
   * This method handles the deserialization of the census data into the Broadband class
   * @param broadbandJson
   * @return
   */
  public static Broadband deserializeCensus(String broadbandJson) {
    try {
      Moshi moshi = new Moshi.Builder().build();

      JsonAdapter<Broadband> adapter = moshi.adapter(Broadband.class);

      return adapter.fromJson(broadbandJson);
    } catch (IOException e) {
      e.printStackTrace();
      return new Broadband();
    }
  }
}
