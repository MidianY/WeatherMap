package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import edu.brown.cs.student.stars.Star;
import edu.brown.cs.student.stars.StarFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/** Class for Testing of CSVParser and associated classes. */
public class TestCSVParser {

  /**
   * This test makes sure that the number of rows in a csv file is counted correctly with a
   * CSVParser taking in a FileReader and a ListCreator.
   *
   * @throws FileNotFoundException Thrown when the file cannot be found
   */
  @Test
  public void numRows() throws FileNotFoundException {
    ListCreator creator = new ListCreator();
    CSVParser csvParser = new CSVParser<>(new FileReader("data/stars/ten-star.csv"), creator, true);
    Assert.assertEquals(10, csvParser.getRows().size());
  }

  /** This test makes sure that the CSVParser object works with a StringReader. */
  @Test
  public void diffReader() {
    ListCreator creator = new ListCreator();
    String s = """
          a,
          b,
          c,
          d,
          """;
    CSVParser csvParser = new CSVParser<>(new StringReader(s), creator, false);
    Assert.assertEquals(4, csvParser.getRows().size());
  }

  /**
   * This test makes sure that a csv file of stars is correctly parsed using the StarFactory creator
   * and a FileReader
   *
   * @throws FileNotFoundException FileNotFoundException Thrown when the file cannot be found
   */
  @Test
  public void valCheck() throws FileNotFoundException {
    StarFactory sFactory = new StarFactory();
    CSVParser parser = new CSVParser<>(new FileReader("data/stars/ten-star.csv"), sFactory, true);
    Star expected = new Star(71454, "Rigel Kentaurus B", -0.50359, -0.42128, -1.1767);
    Star actual = (Star) parser.getRows().get(6);
    Assert.assertEquals(expected.name(), actual.name());
    Assert.assertEquals(expected.id(), actual.id());
    Assert.assertEquals((int) (expected.x() * 1000000), (int) (actual.x() * 1000000));
    Assert.assertEquals((int) (expected.y() * 1000000), (int) (actual.y() * 1000000));
    Assert.assertEquals((int) (expected.z() * 1000000), (int) (actual.z() * 1000000));
  }

  /**
   * This test makes sure that the StarFactory throws a FactoryFailureException when the row passed
   * in is not formatted correctly.
   */
  @Test
  public void rowFormTest() {
    StarFactory sFactory = new StarFactory();
    String s = "1,2,3";
    String[] row = s.split(",");
    Assert.assertThrows(FactoryFailureException.class, () -> sFactory.create(List.of(row)));
  }

  /** Tests the whether the parser responds correctly to its hasHeaderRow parameter. */
  @Test
  public void headerTest() {
    ListCreator creator = new ListCreator();
    String s = """
          a,
          b,
          c,
          d,
          """;
    CSVParser headerParser = new CSVParser<>(new StringReader(s), creator, true);
    CSVParser noHeaderParser = new CSVParser<>(new StringReader(s), creator, false);
    Assert.assertEquals(4, noHeaderParser.getRows().size());
    Assert.assertEquals(3, headerParser.getRows().size());
  }

  public void randomCSV() {
    String csv = "";
    int numVals = (int) (Math.random() * 10);
    for (int i = 0; i < numVals; i++) {
      int valLength = (int) (Math.random() * 10);
      String randomString = "";
      for (int j = 0; j < valLength; j++) {
        String allChars =
            "QWERTYUIOPASDFGHJKLZXCVBNM" + "qwertyuiopasdfghjklzxcvbnm" + "1234567890" + ".><?/;:|";
        int randIndex = (int) (allChars.length() * Math.random());
        randomString = randomString + allChars.charAt(randIndex);
      }
      if (i != numVals - 1) {
        csv = csv + randomString + ",";
      } else {
        csv = csv + randomString;
      }
    }
    CSVParser parser = new CSVParser(new StringReader(csv), new ListCreator(), false);
  }

  @Test
  public void fuzzTestParser() {
    for (int i = 0; i < 1000; i++) {
      assertDoesNotThrow(() -> this.randomCSV());
    }
  }
}
