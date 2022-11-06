package edu.brown.cs.student.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class that keeps track of the current CSV data so that LoadHandler and GetHandler have
 * access to the same data
 */
public class CurrentData {
  private List<List<String>> csvContents;

  public CurrentData() {
    this.csvContents = new ArrayList<>();
  }

  // method sets the csvContents list to the data that has just been loaded
  public void setList(List<List<String>> csv) {
    this.csvContents = csv;
  }

  // method returns the current CSV data
  public List<List<String>> getList() {
    return this.csvContents;
  }
}
