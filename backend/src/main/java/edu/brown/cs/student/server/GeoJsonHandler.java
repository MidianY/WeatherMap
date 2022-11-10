package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.errorRepsonses.BadJsonError;
import edu.brown.cs.student.server.weather.ForecastProperties;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoJsonHandler implements Route {
    private FeatureCollection data;

    public GeoJsonHandler() throws IOException {
        try {
            String redliningData = new String(Files.readAllBytes(Paths.get("data/redlining/redlining.geojson")));
            Moshi moshi = new Moshi.Builder().build();
            this.data = moshi
                    .adapter(FeatureCollection.class)
                    .fromJson(redliningData);
        }catch (Exception e){}
    }

    public FeatureCollection getData(){
        return this.data;
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {
        QueryParamsMap qm = request.queryMap();
        String minLat = qm.value("minLat");
        String maxLat = qm.value("maxLat");
        String minLon = qm.value("minLon");
        String maxLon = qm.value("maxLon");

        try {
            if (!qm.hasKey("minLat") | !qm.hasKey("maxLat") | !qm.hasKey("minLon") | !qm.hasKey("maxLon")) {
                return new BadJsonError().serialize();
            }
            float fMinLat = Float.parseFloat(minLat);
            float fMaxLat = Float.parseFloat(maxLat);
            float fMinLon = Float.parseFloat(minLon);
            float fMaxLon = Float.parseFloat(maxLon);

            List<Features> finalList = this.filterFeatures(fMinLon, fMaxLon, fMinLat, fMaxLat);

            return new GeoJsonSuccessResponse(finalList).serialize();

        } catch (Exception e) {
            return new BadJsonError().serialize();
        }
    }

    public List<Features> filterFeatures(float minLon, float maxLon, float minLat, float maxLat) throws IOException {
        List<Features> filteredFeatures = new ArrayList<>();

        outer: for(Features features: this.data.features()){
            if(features.geometry == null){
                continue;
            }
            List<List<Float>> innerBound = features.geometry.coordinates.get(0).get(0);
            for(List<Float> bounds: innerBound){
                float lon = bounds.get(0);
                float lat = bounds.get(1);

                if(minLat>lat || lat>maxLat || minLon>lon || maxLon<lon){
                    continue outer;
                }
            }
            filteredFeatures.add(features);
        }
        return filteredFeatures;

    }

    public record GeoJsonSuccessResponse(List<Features> data) {
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("data", data);
            result.put("result", "success");
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(Map.class).toJson(result);
        }
    }

    public record FeatureCollection(String type, List<Features> features) {}

    public record Features(String type, Geometry geometry, Map<String, Object> properties) {}

    public record Geometry(String type, List<List<List<List<Float>>>> coordinates) {}

    public void setData(String filepath){
        try {
            String redliningData = new String(Files.readAllBytes(Paths.get(filepath)));
            Moshi moshi = new Moshi.Builder().build();
            this.data = moshi
                    .adapter(FeatureCollection.class)
                    .fromJson(redliningData);
        }catch (Exception e){}
    }
}



