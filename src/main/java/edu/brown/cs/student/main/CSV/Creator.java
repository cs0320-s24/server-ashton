package Parse;

import java.util.List;

public class Creator implements CreatorFromRow<List<String>> {

  @Override
  public List<String> create(List<String> row, int numColumns) throws FactoryFailureException {
    if (row.size() != numColumns) {
      throw new FactoryFailureException("Bad number of rows.", row);
    }
    return row;
  }
}
