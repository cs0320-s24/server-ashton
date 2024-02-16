package edu.brown.cs.student.main.server.handlers.CSVHandling;

import edu.brown.cs.student.main.CSV.CSVParser;
import edu.brown.cs.student.main.CSV.Creator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoadCSVHandler implements Route {

  private CSVHandling handling;
  public LoadCSVHandler(CSVHandling handling) {
    this.handling = handling;
  }

  @Override
  public Object handle(Request request, Response response) {
    String filepath = request.queryParams("filepath");

    FileReader fileReader = null;

    Map<String, Object> responseMap = new HashMap<>();

    try {
      fileReader = new FileReader(filepath);
    } catch (FileNotFoundException e) {
      System.out.println("file not found");
    }
    CSVParser<List<String>> parser = new CSVParser<>(fileReader, new Creator());
    try {
      List<List<String>> parsedData = parser.parse();
      this.handling.setParsedData(parsedData);
    } catch (Exception e) {
      System.out.println("idk yet");
    }

    responseMap.put("result", "success");
    return responseMap;
  }
}
