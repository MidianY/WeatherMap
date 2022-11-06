package edu.brown.cs.student.csv;

import java.util.List;

/**
 * Implements the method 'create' From creatorFromRow interface. Creates a hashmap with word,
 * character, row, and column count for a given row.
 */
public class ListCreator implements CreatorFromRow<List<String>> {

  /**
   * This method takes each value in the given list and counts the characters, words, columns, and
   * rows. It stores these values in a hash map.
   *
   * @param row a list of strings that will have word
   * @return a Hashmap with keys 'Words', 'Characters', 'Columns', and 'Rows' mapped to counts for
   *     each of these attributes of the row.
   * @throws FactoryFailureException Thrown when the creator cannot process the rows passed in
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    try {
      return row;
    } catch (Exception e) {
      throw new FactoryFailureException(row);
    }
  }
}
