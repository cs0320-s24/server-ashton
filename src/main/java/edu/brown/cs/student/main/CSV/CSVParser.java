package edu.brown.cs.student.main.CSV;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CSVParser<T> {
  private final BufferedReader fileReader;
  private final CreatorFromRow<T> creatorFromRow;
  private int numColumns;

  /**
   * This is the CSV parser class that handles the functionality of parsing a CSV into a generic
   * object type
   *
   * @param fileReader - the reader object
   * @param creatorFromRow - the object used to convert each line into a row object
   */
  public CSVParser(Reader fileReader, CreatorFromRow<T> creatorFromRow) {
    this.fileReader = new BufferedReader(fileReader);
    this.creatorFromRow = creatorFromRow;
    this.numColumns = 0;
  }

  /**
   * This method parses the CSV and creates a list of the generic type to return
   *
   * @return
   * @throws IOException
   * @throws FactoryFailureException
   */
  public List<T> parse() throws IOException, FactoryFailureException {
    List<T> result = new ArrayList<>();
    boolean isFirst = true;

    String line;
    try {
      line = this.fileReader.readLine();
      while (line != null) {
        List<String> parsedLine = parseLine(line);
        if (isFirst) {
          this.numColumns = parsedLine.size();
        }
        isFirst = false;
        try {
          T createdObject = this.creatorFromRow.create(parsedLine, this.numColumns);
          if (createdObject != null) {
            result.add(createdObject);
          } else {
            throw new FactoryFailureException(
                "Error creating row object: Factory returned null", parsedLine);
          }
        } catch (FactoryFailureException e) {
          throw new FactoryFailureException(
              "Error creating row object: " + e.getMessage() + " " + parsedLine, parsedLine);
        }
        line = this.fileReader.readLine();
      }
    } catch (IOException e) {
      throw new IOException("Error reading the file: " + e.getMessage());
    } finally {
      try {
        this.fileReader.close();
      } catch (IOException e) {
        throw new IOException("Error closing the file reader: " + e.getMessage());
      }
    }

    return result;
  }

  /**
   * This method takes in an individual line and uses the regex to turn it into a list of strings
   *
   * @param line - the string read from each line in the csv
   * @return
   */
  private List<String> parseLine(String line) {
    List<String> row = new ArrayList<>();
    String[] values =
        Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))").split(line);
    for (String value : values) {
      row.add(value.trim());
    }
    return row;
  }
}
