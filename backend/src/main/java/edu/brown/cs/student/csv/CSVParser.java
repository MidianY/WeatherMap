package edu.brown.cs.student.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class takes a given set of data and parses it into a list of objects with the parse method.
 *
 * @param <T> Returns object of type T
 */
public class CSVParser<T> implements Iterable<T> {
  private BufferedReader reader;
  private CreatorFromRow<T> creator;
  private List<T> rows;
  private boolean hasHeaderRow;

  /**
   * This method constructs a CSVParser and calls the parse method.
   *
   * @param reader an object of type Reader.
   * @param creator an object of type CreatorFromRow.
   */
  public CSVParser(Reader reader, CreatorFromRow<T> creator, Boolean hasHeaderRow) {
    this.reader = new BufferedReader(reader);
    this.creator = creator;
    this.hasHeaderRow = hasHeaderRow;
    this.parse();
  }

  /**
   * This method uses the Reader and CreatorFromRow to create objects of type T out of a list
   * generated by the given Reader. It returns these objects in a List.
   *
   * @return the list of rows as objects type T after parsing
   */
  public List<T> parse() {
    String line;
    this.rows = new ArrayList();
    try {
      while ((line = reader.readLine()) != null) {
        if (!this.hasHeaderRow) {
          this.rows.add(creator.create(List.of(line.split(","))));
        } else {
          this.hasHeaderRow = false;
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return Collections.unmodifiableList(this.rows);
  }

  /**
   * @return the List of rows that have been parsed.
   */
  public List<T> getRows() {
    return this.rows;
  }

  @Override
  public Iterator<T> iterator() {
    return this.rows.iterator();
  }
}
