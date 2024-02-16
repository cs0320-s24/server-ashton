package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.CSV.CSVParser;
import edu.brown.cs.student.main.CSV.Creator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;


public class LoadCSVHandler implements Route {

  @Override
  public Object handle(Request request, Response response) {
    String filepath = request.queryParams("filepath");
    String numColumns = request.queryParams("numcolumns");
    System.out.println(filepath);
    FileReader fileReader = null;
    try {
      fileReader = new FileReader(filepath);
    } catch (FileNotFoundException e) {
      System.out.println("file not found");
    }
    CSVParser<List<String>> parser = new CSVParser<>(fileReader, new Creator());
    try {
      List<List<String>> parsedData = parser.parse();
      System.out.println(parsedData);
    } catch (Exception e) {
      System.out.println("idk yet");
    }
    return null;
  }
}
