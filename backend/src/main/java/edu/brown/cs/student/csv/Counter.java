package edu.brown.cs.student.wordcount;

import java.util.List;

/**
 * A class providing static methods that return counts for rows, columns, words, and charactors in a
 * parsed input (in the form of a List of Lists of Strings).
 */
public class Counter {

  /**
   * Returns the number of columns in the entire provided CSV input.
   *
   * @param parsedFile the parsed input
   * @return the number of columns
   */
  public static int getTotalColumnCount(List<List<String>> parsedFile) {
    if (parsedFile.size() != 0) {
      int highestColumnCount =
          0; // because the number of columns may be different for each row (if a value is missing),
      // checks for the highest number of elements in a row
      for (List<String> parsedRow : parsedFile) {
        if (parsedRow.size() > highestColumnCount) {
          highestColumnCount = parsedRow.size();
        }
      }
      return highestColumnCount;
    } else {
      return 0;
    }
  }

  /**
   * returns the number of characters in the entire provided CSV input.
   *
   * @param parsedFile the parsed input
   * @return the number of characters
   */
  public static int getTotalCharacterCount(List<List<String>> parsedFile) {
    int totalCharacterCount = 0;
    if (parsedFile.size() != 0) {
      for (List<String> parsedRow : parsedFile) {
        for (String cell : parsedRow) {
          totalCharacterCount += cell.length();
        }
        totalCharacterCount +=
            getTotalColumnCount(parsedFile)
                - 1; // adds back in the commas that were cut out by the parser
      }
    }
    return totalCharacterCount;
  }

  /**
   * returns the number of words in the entire provided CSV input.
   *
   * @param parsedFile the parsed input
   * @return the number of words
   */
  public static int getTotalWordCount(List<List<String>> parsedFile) {
    int totalWordCount = 0;
    if (parsedFile.size() != 0) {
      for (List<String> parsedRow : parsedFile) {
        for (String cell : parsedRow) {
          if (cell != "") {
            totalWordCount += cell.split(" ").length;
          }
        }
      }
    }
    return totalWordCount;
  }

  /**
   * returns the number of rows in the entire provided CSV input.
   *
   * @param parsedFile the parsed input
   * @return the number of rows
   */
  public static int getTotalRowCount(List<List<String>> parsedFile) {
    int totalRowCount = parsedFile.size();
    return totalRowCount;
  }
}
