package edu.brown.cs.student.main.CSV;

import java.util.List;

/**
 * A class that handles turning rows into a list of strings
 */
public class Creator implements CreatorFromRow<List<String>> {

  /**
   * Takes ina  list of strings and returns it as a list of strings while also checking for failures while making it
   * @param row
   * @param numColumns
   * @return
   * @throws FactoryFailureException
   */
  @Override
  public List<String> create(List<String> row, int numColumns) throws FactoryFailureException {
    if (row.size() != numColumns) {
      throw new FactoryFailureException("Bad number of rows.", row);
    }
    return row;
  }
}
