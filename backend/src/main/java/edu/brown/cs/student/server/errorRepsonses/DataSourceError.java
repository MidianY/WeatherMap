package edu.brown.cs.student.server.errorRepsonses;

import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the response object that will be sent in the case of datasource error. Method is
 * responsible for returning the serialized json response
 */
public record DataSourceError() {

  /**
   * @return this response, serialized as Json
   */
  public String serialize() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("result", "error_datasource");
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(result);
  }
}
