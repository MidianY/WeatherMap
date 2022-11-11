package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.errorRepsonses.BadJsonError;
import edu.brown.cs.student.server.weather.ForecastProperties;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Handler class for the geo_data handling API endpoint.
 *
 * <p>The purpose of this class is to get a response with the features that contain all coordinates within a set of user
 * inputted coordinates. Based on the nature of the request either a success or error response will be returned to the
 * user.
 */
public class GeoJsonHandler implements Route {
    private FeatureCollection data;

    /**
     * In the constructor, we use the FeatureCollection variable instantiated above to access the GeoJson data.
     * We use this later on in the program t filter the coordinates.
     */
    public GeoJsonHandler(){
        try {
            String redliningData = new String(Files.readAllBytes(Paths.get("data/redlining/redlining.geojson")));
            Moshi moshi = new Moshi.Builder().build();
            this.data = moshi
                    .adapter(FeatureCollection.class)
                    .fromJson(redliningData);
        }catch (Exception e){}
    }

    /**
     * Getter method that returns the contents of data (a FeatureCollection instance variable)
     * @return the contents of this.data
     */
    public FeatureCollection getData(){
        return this.data;
    }

    /**
     * This method sets the contents of this.data to whatever is passed in. Catches generic IOException
     * @param filepath this is what this.data is being set to
     */
    public void setData(String filepath){
        try {
            String redliningData = new String(Files.readAllBytes(Paths.get(filepath)));
            Moshi moshi = new Moshi.Builder().build();
            this.data = moshi
                    .adapter(FeatureCollection.class)
                    .fromJson(redliningData);
        }catch (IOException e){}
    }

    /**
     * This method handles requests that are made through the geo_data endpoint. Uses helpers throughout the class to
     * access the list of Features and return a json containing it. Handles possible errors that can occur with inputs
     * returns an informative message if this happens.
     * @param request this parameter allows us to access all the parameters passed into the endpoint geo_data.
     *                In this case, we can access minLon, maxLon, minLat, and maxLat all through request.
     * @param response this parameter s given to us by the Route interface but is not used
     * @return returns an object which in this case is a serialized json
     */
    @Override
    public Object handle(Request request, Response response){
        Map<String, Object> resp = new HashMap<>();

        QueryParamsMap qm = request.queryMap();
        String minLat = qm.value("minLat");
        String maxLat = qm.value("maxLat");
        String minLon = qm.value("minLon");
        String maxLon = qm.value("maxLon");

        try {
            if (!qm.hasKey("minLat") | !qm.hasKey("maxLat") | !qm.hasKey("minLon") | !qm.hasKey("maxLon") | qm.toMap().keySet().size() != 4) {
                return new BadJsonError().serialize();
            }
            float fMinLat = Float.parseFloat(minLat);
            float fMaxLat = Float.parseFloat(maxLat);
            float fMinLon = Float.parseFloat(minLon);
            float fMaxLon = Float.parseFloat(maxLon);

            List<Features> finalList = this.filterFeatures(fMinLon, fMaxLon, fMinLat, fMaxLat);

            resp.put("data", new FeatureCollection(this.data.type(), finalList));
            return this.success(resp);

        } catch (Exception e) {
            return new BadJsonError().serialize();
        }
    }

    /**
     * This method is used to filter the GeoJson by the coordinates that are passed in.
     * @param minLon this parameter is the minimum longitude
     * @param maxLon this parameter is the maximum longitude
     * @param minLat this parameter is the minimum latitude
     * @param maxLat this parameter is the maximum latitude
     * @return this method returns a list of features that have been filtered down
     */
    public List<Features> filterFeatures(float minLon, float maxLon, float minLat, float maxLat){
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

    /**
     * This method is used to serialize hashmaps into Jsons so that we can display them to the user.
     * @param resp takes in a hashmap of the data or results of the handler
     * @return returns a serialized json from the hashmap
     */
    public String serialize(Map<String, Object> resp) {
        Moshi moshi = new Moshi.Builder().build();
        Type respType = Types.newParameterizedType(Map.class, String.class, Object.class);
        return moshi.adapter(respType).toJson(resp);
    }

    /**
     * This method is called when the input is a success. The method adds a success message to the hashmap and calls the
     * serialize method on it
     * @param resp this method takes in a hashmap of information
     * @return this method serializes the inputted hashmap and returns the json string
     */
    public String success(Map<String, Object> resp) {
        resp.put("result", "success");
        return this.serialize(resp);
    }

    /**
     * The next three records are used throughout the class in order to make our lives easier. They are mostly used
     * to access the information in the GeoJson.
     */
    public record FeatureCollection(String type, List<Features> features) {}

    public record Features(String type, Geometry geometry, Map<String, Object> properties) {}

    public record Geometry(String type, List<List<List<List<Float>>>> coordinates) {}


}



