package edu.brown.cs.student.main.server.handlers.broadband;

import com.squareup.moshi.Json;

import javax.swing.plaf.TableHeaderUI;

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

    @Override
    public String toString() {
        return "The estimated broadband access in " + this.name + " is " + this.value + " percent";
    }
}
