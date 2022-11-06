package edu.brown.cs.student.csv;

import java.io.FileReader;
import java.util.HashMap;

/** Prints out values for word, character, row, and column count for a given csv file. */
public class FileUtility {

  private String fileName;

  /**
   * The File Utility constructor. This calls the Count method.
   *
   * @param fileName the name of a file to be read
   */
  public FileUtility(String fileName) {
    this.fileName = fileName;
    this.count();
  }

  /**
   * This method uses the CSVParser to get a list of values for word, character, row, and column
   * count for a given csv file. It tallies these counts from the list that the parser returns and
   * prints a message indicating the values counted.
   */
  public void count() {

    int words = 0;
    int chars = 0;
    int rows = 0;
    int cols = 0;

    try {
      CSVParser<HashMap<String, Integer>> rowList =
          new CSVParser(new FileReader(this.fileName), new ListCreator(), true);
      for (HashMap<String, Integer> h : rowList) {
        words += h.get("Words");
        chars += h.get("Characters");
        rows += h.get("Rows");
        if (h.get("Columns") > cols) {
          cols = h.get("Columns");
        }
      }
      System.out.println("Words: " + words);
      System.out.println("Characters: " + chars);
      System.out.println("Rows: " + rows);
      System.out.println("Columns: " + cols);

    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
