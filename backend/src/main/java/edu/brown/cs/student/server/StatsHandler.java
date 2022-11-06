package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.errorRepsonses.BadRequestError;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.brown.cs.student.wordcount.Counter;

public class StatsHandler implements Route {
    private CurrentData currentData;

    public StatsHandler(CurrentData currentData){
        this.currentData = currentData;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (this.currentData.getList() == null) {
                return new BadRequestError().serialize();
            } else {
                int rows = Counter.getTotalRowCount(currentData.getList());
                int columns = Counter.getTotalColumnCount(currentData.getList());
                return new StatsHandler.StatsSuccessResponse(rows, columns).serialize();
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
    public record StatsSuccessResponse(int rows, int columns) {
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "success");
            result.put("rows", rows);
            result.put("columns", columns);
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(Map.class).toJson(result);
        }
    }

}
