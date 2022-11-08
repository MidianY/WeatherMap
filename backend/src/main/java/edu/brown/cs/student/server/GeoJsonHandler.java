package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.errorRepsonses.BadJsonError;
import edu.brown.cs.student.server.weather.ForecastProperties;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoJsonHandler implements Route {
    public String data;

    public GeoJsonHandler() {

    }
    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        this.data = new String(Files.readAllBytes(Paths.get("src/main/java/edu/brown/cs32/sprint2/Handlers/fullDownload copy.geojson.json")));
        QueryParamsMap qm = request.queryMap();
        String minlat = qm.value("minlat");
        String maxlat = qm.value("maxlat");
        String minlon = qm.value("minlon");
        String maxlon = qm.value("maxlon");

        String filepath = "data/redlining/redlining.geojson";

        try {
            if (!qm.hasKey("minlat") | !qm.hasKey("maxlat") | !qm.hasKey("minlon") | !qm.hasKey("maxlon")) {
                return new BadJsonError().serialize();
            }
            float minLat = Float.parseFloat(minlat);
            float maxLat = Float.parseFloat(maxlat);
            float minLon = Float.parseFloat(minlon);
            float maxLon = Float.parseFloat(maxlon);



        } catch (Exception e) {
            return new BadJsonError().serialize();
        }
        return null;

    }

    public void filterData(float minLat, float maxLat, float minLon, float maxLon){

//        for (Feature feature : this.data){
//
//        }

    }



//    public void getFeatures(){
//        Moshi moshi = new Moshi.Builder().build();
//        ForecastProperties serializedForecast =
//                moshi.adapter(ForecastProperties.class).fromJson(response);
//        int temp = serializedForecast.properties.periods.get(0).temperature;
//    }

    public record GeoJsonSuccessResponse(String minLat, String maxLat, String minLon, String maxLon,
                                         List<List<String>> data) {
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("minlat", minLat);
            result.put("maxlat", maxLat);
            result.put("minlon", minLon);
            result.put("maxlon", maxLon);
            result.put("data", data);
            result.put("result", "success");
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(Map.class).toJson(result);
        }
    }

    public record Feature(String type, Geometry geometry, Map<String, Object> properties){}
    public record Geometry(String type, List<List<List<List<Float>>>> coordinates) {}
}



