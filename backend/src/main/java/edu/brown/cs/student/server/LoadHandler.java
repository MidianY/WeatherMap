package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.csv.CreatorFromRow;
import edu.brown.cs.student.csv.ListCreator;
import edu.brown.cs.student.server.errorRepsonses.BadJsonError;
import edu.brown.cs.student.server.errorRepsonses.DataSourceError;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the load handling API endpoint. This class is used to parse and load data from
 * a CSV file, given a filepath, via the handle method. This data is then used by another GET
 * request that is handled in the GetHandler class associated with the /get endpoint. Based on the
 * nature of the request either a success or error response will be shown to the user.
 */
public class LoadHandler implements Route {
  private CurrentData currentData;

  /**
   * Constructor accepts shared state of data
   *
   * @param currentData (the shared state)
   */
  public LoadHandler(CurrentData currentData) {
    this.currentData = currentData;
  }

  /**
   * Method handles requests that are made through the /load endpoint. The logic for obtaining the
   * parsed CSV data and putting it in a list contained in the CurrentData class is handled in this
   * method.
   *
   * <p>If the filepath query parameter given is incorrect an error_bad_json will be returned. If
   * the filepath query parameter is present but the filepath is invalid, an error_datasource will
   * be returned. If both inputs are correct the user will see a success results along with the
   * filepath that they have provided.
   *
   * @param request the request to handle
   * @param response use to modify properties of the response
   * @return an error or success response depending on the users input
   * @throws Exception part of the interface; nothing is thrown in this case .
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    QueryParamsMap qm = request.queryMap();
    String filepath = qm.value("filepath");

    try {
      if (!qm.hasKey("filepath")) {
        return new BadJsonError().serialize();
      }
      FileReader reader = new FileReader(filepath);
      CreatorFromRow creator = new ListCreator();
      CSVParser<List<String>> parser = new CSVParser(reader, creator, true);
      this.currentData.setList(parser.getRows());
      return new LoadSuccessResponse(filepath).serialize();
    } catch (FileNotFoundException e) {
      return new DataSourceError().serialize();
    }
  }

  /**
   * This is the response object that will be sent in the case of a success. Method is responsible
   * for returning the serialized json response
   */
  public record LoadSuccessResponse(String filePath) {

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      HashMap<String, Object> result = new HashMap<>();
      result.put("result", "success");
      result.put("filepath", filePath);
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(Map.class).toJson(result);
    }
  }
}
