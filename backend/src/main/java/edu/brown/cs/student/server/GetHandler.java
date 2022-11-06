package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.errorRepsonses.BadRequestError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the get handling API endpoint. This class takes CSV data that has been
 * previously loaded via the /load endpoint API and returns it to the user. Based on the nature of
 * the request either a success or error response will be shown to the user.
 */
public class GetHandler implements Route {
  private CurrentData currentData;

  /**
   * Constructor accepts shared state of data
   *
   * @param currentData (the shared state)
   */
  public GetHandler(CurrentData currentData) {
    this.currentData = currentData;
  }

  /**
   * Method handles requests that are made through the get endpoint. This method takes the CSV data
   * that is stored in the CurrentData class and returns it to the user if the proper requests are
   * made.
   *
   * <p>If the user attempts to /get a csv file without previously loading it first an
   * error_bad_request will be returned. Because this API does not have any query parameters the
   * error handling for that is not contained within this class. If the user properly loads a CSV
   * file and then attempts to /get that data they will see a success response along contents of
   * that csv File
   *
   * @param request the request to handle
   * @param response use to modify properties of the response
   * @return an error or success response depending on the users input
   * @throws Exception part of the interface; nothing is thrown in this case
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      if (this.currentData.getList() == null) {
        return new BadRequestError().serialize();
      } else {
        return new GetSuccessResponse(currentData.getList()).serialize();
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * This is the response object that will be sent in the case of a success. Method is responsible
   * for returning the serialized json response
   */
  public record GetSuccessResponse(List<List<String>> data) {
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      HashMap<String, Object> result = new HashMap<>();
      result.put("result", "success");
      result.put("data", data);
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(Map.class).toJson(result);
    }
  }
}
