package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.weather.WeatherHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestServer {

  /** Method sets the Spark port number */
  @BeforeAll
  public static void startSetup() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  // Shared state of currentData for all the tests
  final CurrentData currentData = new CurrentData();

  /**
   * Clears the current data before each test is run and makes the API calls for load,get and
   * weather
   */
  @BeforeEach
  public void setup() {
    currentData.setList(null);
    Spark.get("load", new LoadHandler(currentData));
    Spark.get("get", new GetHandler(currentData));
    Spark.get("weather", new WeatherHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * Stop Spark listening on both endpoints and ensures that it doesn't proceed unless this has
   * happened
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("load");
    Spark.unmap("get");
    Spark.unmap("weather");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestMethod("GET");
    clientConnection.connect();
    return clientConnection;
  }

  // -------------------------------- INTEGRATION TESTING -------------------------------- //

  /**
   * Testing the load API response given no filepath, expected to return an error_bad_json
   *
   * @throws IOException
   */
  @Test
  public void testNoFilepath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "error_bad_json");
    assertEquals(expectedResponse, response);
    clientConnection.disconnect();
  }

  /**
   * Testing the load API response given a non-existent, expected to return an error_datasource
   *
   * @throws IOException
   */
  @Test
  public void testWrongFilepath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filepath=kjdkljkfd");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "error_datasource");
    assertEquals(expectedResponse, response);
    clientConnection.disconnect();
  }

  /**
   * Testing the load API response given a correct filepath, expected to return a success response
   *
   * @throws IOException
   */
  @Test
  public void testLoadSuccess() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "success");
    expectedResponse.put("filepath", "data/stars/ten-star.csv");
    assertEquals(expectedResponse, response);
    clientConnection.disconnect();
  }

  /**
   * Testing the get API response after successfully calling load, expected to return a success
   * response along with the contents of the CSV file
   *
   * @throws IOException
   */
  @Test
  public void testGetSuccess() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("get");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "success");
    List<List<String>> csv = this.currentData.getList();
    expectedResponse.put("data", csv);
    assertEquals(expectedResponse, response);
    clientConnection.disconnect();
  }

  /**
   * Testing the get API response after calling load twice, one on an non-existent file and another
   * on a correct file. expected to return a success response along with contents of the CSV that
   * was previously loaded from the first load request.
   *
   * @throws IOException
   */
  @Test
  public void testGetTwoLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filepath=data/stars/ten-star.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("load?filepath=invalidFilePath");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("get");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "success");
    List<List<String>> csv = this.currentData.getList();
    expectedResponse.put("data", csv);
    assertEquals(expectedResponse, response);
    clientConnection.disconnect();
  }

  /**
   * Testing the get API response without calling load first, expected to return a error_bad_request
   *
   * @throws IOException
   */
  @Test
  public void testGetFailure() throws IOException {
    HttpURLConnection clientConnection = tryRequest("get");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "error_bad_request");
    assertEquals(expectedResponse, response);
    clientConnection.disconnect();
  }

  /**
   * Testing the weather API response given an existent target location, expected to return a
   * success response with the current temperature.
   *
   * @throws IOException
   */
  @Test
  public void testWeatherSuccess() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lat=39.7456&lon=-97.0892");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "success");
    expectedResponse.put("lat", "39.7456");
    expectedResponse.put("lon", "-97.0892");
    assertEquals(expectedResponse.get("result"), response.get("result"));
    assertEquals(expectedResponse.get("lat"), response.get("lat"));
    assertEquals(expectedResponse.get("lon"), response.get("lon"));
    assertTrue(response.get("temperature") instanceof Number);
    clientConnection.disconnect();
  }

  /**
   * Testing the weather API response given a non-existent target location, expected to return an
   * error_datasource
   *
   * @throws IOException
   */
  @Test
  public void testWeatherFailure() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lat=0.00&lon=0.00");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("result", "error_datasource");
    assertEquals(expectedResponse, response);
    clientConnection.disconnect();
  }

  // -------------------------------- END OF INTEGRATION TESTING -------------------------------- //

  /**
   * Test to ensure we are properly formatting the coordinates that users put in to be used based on
   * the requirements outlined in the method.
   */
  @Test
  public void testFormatCoordinates() {
    WeatherHandler weatherHandler = new WeatherHandler();
    String lat1 = "37.40600";
    String lon1 = "96.50400";
    String lat2 = "30.0";
    String lon2 = "96.00";
    assertEquals("37.406", weatherHandler.formatCoordinate(lat1));
    assertEquals("96.504", weatherHandler.formatCoordinate(lon1));
    assertEquals("30", weatherHandler.formatCoordinate(lat2));
    assertEquals("96", weatherHandler.formatCoordinate(lon2));
  }

  /**
   * Test to ensure that the forecast link being obtained matches the one that is seen on the NWS
   * API
   *
   * @throws Exception part of the interface; nothing is thrown in this case
   */
  @Test
  public void testForecastURL() throws Exception {
    WeatherHandler weatherHandler = new WeatherHandler();
    String lat1 = "37.40600";
    String lon1 = "-96.50400";
    String lat2 = "30.0";
    String lon2 = "-96.00";
    assertEquals(
        "https://api.weather.gov/gridpoints/ICT/90,20/forecast",
        weatherHandler.getForecastLink(weatherHandler.makeForecastRequest(lat1, lon1)));
    assertEquals(
        "https://api.weather.gov/gridpoints/HGX/41,107/forecast",
        weatherHandler.getForecastLink(weatherHandler.makeForecastRequest(lat2, lon2)));
  }


  //Testing with mock NWS API responses

  /**
   * Testing the getForecastLink method with a mocked NWS API response
   * @throws IOException
   */
  @Test
  public void testGetForecast() throws IOException {
    WeatherHandler handler = new WeatherHandler();
    Path path = Path.of("src/test/java/edu/brown/cs/student/server/WeatherResponse.json");
    String response = Files.readString(path);
    assertEquals(
        "https://api.weather.gov/gridpoints/ICT/90,20/forecast", handler.getForecastLink(response));
  }

  /**
   * Testing the getTemp method with a mocked NWS API response
   * @throws IOException
   */
  @Test
  public void testGetTemp() throws IOException {
    WeatherHandler handler = new WeatherHandler();
    Path path = Path.of("src/test/java/edu/brown/cs/student/server/ForecastResponse.json");
    String response = Files.readString(path);
    assertEquals(61, handler.getTemp(response));
  }
}
