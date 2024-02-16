package edu.brown.cs.student.main.server.handlers.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class BroadbandAPIUtilities {

    public static Broadband deserializeActivity(String jsonActivity) {
        try {
            Moshi moshi = new Moshi.Builder().build();

            JsonAdapter<Broadband> adapter = moshi.adapter(Broadband.class);

            return adapter.fromJson(jsonActivity);
        }

        catch (IOException e) {
            e.printStackTrace();
            return new Broadband();
        }
    }
}
