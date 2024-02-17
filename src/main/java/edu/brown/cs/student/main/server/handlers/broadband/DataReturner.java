package edu.brown.cs.student.main.server.handlers.broadband;

import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * An interface that defines how data should be retrieved
 */
public interface DataReturner {

    //the method that returns the data that must be overridden
    Map<String, Object> getData(Request request, Response response);
}
