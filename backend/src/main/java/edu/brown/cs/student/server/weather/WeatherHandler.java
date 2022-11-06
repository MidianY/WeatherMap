package edu.brown.cs.student.server.weather;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.errorRepsonses.BadJsonError;
import edu.brown.cs.student.server.errorRepsonses.DataSourceError;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the weather handling API endpoint.
 *
 * <p>The purpose of this class is to get a response with the current temperature given the location
 * that the user inputs through the latitude and longitude query parameters. Based on the nature of
 * the request either a success or error response will be returned to the user.
 */
public class WeatherHandler implements Route {

  /**
   *  2. Weather (S_DIST)
   *  With caching, we can reduce the number of calls made to our endpoint if
   *  we keep track of the most popular requests make. With our code we can create a
   *  HashMap that stores the request and have that map to the number of times that request
   *  was made.
   *
   *  Hashmap<Requests, Integer> requestCount
   *
   *  This way you can keep track of the most frequently used requests and store those.
   *  Because it is likely that many of the same requests are being made throughout the day, this
   *  method ensures we are keeping track of the requests with the highest counts such that those
   *  are the only ones that are being stored.
   *
   *  This would be working alongside some sort of time handler function that would also be written
   *  in this class. This function would be continuously running to keep track of different temperatures
   *  throughout the day, it is likely that the frequency of certain requests would be dependent on what
   *  time of day it is, because the NWS API gives us access to this data, we can store those requests in
   *  line with the correct time period
   */

  /**
   * Method handles requests that are made through the weather endpoint. This method uses the helper
   * methods throughout the class to obtain the current temperature given a latitude and longitude.
   *
   * <p>If the lat or lon query parameter is not used or if it is incorrectly inputted, an
   * error_bad_json will be returned. If both query parameters are present but the latitude and
   * longitude is poorly formatted or the target location is out of range, an error_datasource will
   * be returned to the user. Otherwise, the user will see a success result along with the
   * temperature of target location.
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    QueryParamsMap qm = request.queryMap();

    String latitude = qm.value("lat");
    String longitude = qm.value("lon");

    try {
      if (!qm.hasKey("lon") || !qm.hasKey("lat")) {
        return new BadJsonError().serialize();
      }

      int temperature =
          this.getTemp(
              this.makeTempRequest(
                  this.getForecastLink(this.makeForecastRequest(latitude, longitude))));
      return new WeatherSuccessResponse(latitude, longitude, temperature).serialize();
    } catch (Exception e) {
      return new DataSourceError().serialize();
    }
  }

  /**
   * Method is responsible for formatting the coordinates that the user inputs into the lat and lon
   * query parameters.
   *
   * <p>The NWS API can only take digits up to 4 decimal places so this method trims down a digit if
   * they are greater than 4. Additionally, the NWS API also expects zeros trimmed off if that is
   * the last digit provided, this method takes care of that and returns the number without the zero
   * at the end.
   *
   * @param coordinate the latitude and longitude coordinates of the target location
   * @return the properly formatted coordinates
   */
  public String formatCoordinate(String coordinate) {
    if (coordinate.contains(".")) { // checks if the coordinate entered has decimal values
      // checks if the decimal length is greater than 4
      if (coordinate.substring(coordinate.indexOf(".")).length() >= 5) {
        // trims the length of the coordinate so that it only has 4 decimal places
        coordinate =
            coordinate.substring(
                0,
                coordinate.length() - (coordinate.substring(coordinate.indexOf(".")).length() - 5));
      }
      // record the length of the string from the decimal point to the end.
      int x = coordinate.substring(coordinate.indexOf(".")).length();
      for (int i = 0; i <= x; i++) {
        // check if the last digit is '0'
        if (coordinate.endsWith("0")) {
          // trim the zero off the end
          coordinate = coordinate.substring(0, coordinate.length() - 1);
          // This statement is reached if every digit after the decimal is a zero
          // (example: 30.00 --> 30.). This will check that the coordinate ends with '.', and trim
          // the '.'. (30. --> 30).
        } else if (coordinate.endsWith(".")) {
          coordinate = coordinate.substring(0, coordinate.length() - 1);
          // breaks out of loop tp avoid IndexOutOfBoundsException in for loop.
          break;
        } else {
          break;
        }
      }
    }
    return coordinate;
  }

  /**
   * This method is responsible for obtaining the forecast link that contains the current
   * temperature of the target location. Because the NWS API nests the response of the temperature
   * within the forecast field, this method finds that link and returns it.
   *
   * <p>Method begins by making an HTTPRequest to the NWS API given the latitude and longitude. The
   * response from this request is then used to obtain the forecast and returns that in serialized
   * form.
   *
   * <p>We have created a WeatherData and WeatherProperties class as a means of obtaining only those
   * pieces of data from the NWS API and serializing them. These two classes are used to return the
   * forecast link which is used to make the temperature request.
   *
   * @param lat latitude of the target location
   * @param lon longitude of the target location
   * @return either an error or a success response depending on the nature of the request
   * @throws Exception part of the interface; nothing is thrown in this case
   */
  public String makeForecastRequest(String lat, String lon) throws Exception {
    lat = this.formatCoordinate(lat);
    lon = this.formatCoordinate(lon);

    HttpRequest weatherRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.weather.gov/points/" + lat + "," + lon))
            .GET()
            .build();
    HttpResponse<String> pointsResponse =
        HttpClient.newBuilder().build().send(weatherRequest, BodyHandlers.ofString());
    return pointsResponse.body();
  }

  public String getForecastLink(String response) throws IOException {
    Moshi weatherMoshi = new Moshi.Builder().build();
    WeatherData serializedWeather = weatherMoshi.adapter(WeatherData.class).fromJson(response);
    String forecast = serializedWeather.properties.forecast;
    return forecast;
  }

  /**
   * This method is responsible for obtaining the current temperature from the forecast link.
   *
   * <p>Method begins by making an HTTPRequest given the link obtained from
   * makeForecastRequestMethod. The response from this request is then used to obtain the current
   * temperature and returns that number in serialized form.
   *
   * <p>Again, because the NWS API nests the data, we have created a ForecastProperties,
   * ForecastPeriods and Temperature class as a means of obtaining only those pieces of data from
   * the NWS API and serializing them.
   *
   * @param forecast uses the forecast to obtain the temperature of the target location
   * @return the current temperature of the target location
   * @throws Exception part of the interface; nothing is thrown in this case
   */
  public String makeTempRequest(String forecast) throws Exception {
    HttpRequest forecastRequest = HttpRequest.newBuilder().uri(new URI(forecast)).GET().build();
    HttpResponse<String> forecastResponse =
        HttpClient.newBuilder().build().send(forecastRequest, BodyHandlers.ofString());
    return forecastResponse.body();
  }

  public int getTemp(String response) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    ForecastProperties serializedForecast =
        moshi.adapter(ForecastProperties.class).fromJson(response);
    int temp = serializedForecast.properties.periods.get(0).temperature;

    return temp;
  }

  /**
   * This is the response object that will be sent in the case of a success. Method is responsible
   * for returning the serialized json response
   */
  public record WeatherSuccessResponse(String latitude, String longitude, int temperature) {

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      HashMap<String, Object> result = new HashMap<>();
      result.put("lat", latitude);
      result.put("lon", longitude);
      result.put("temperature", temperature);
      result.put("result", "success");
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(Map.class).toJson(result);
    }
  }
}
