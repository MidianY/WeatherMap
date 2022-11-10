package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import okio.Buffer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestGeoData {

    public TestGeoData() throws IOException {
    }

    /** Method sets the Spark port number */
    @BeforeAll
    public static void startSetup() {
        Spark.port(0);
        Logger.getLogger("").setLevel(Level.WARNING);
    }

    final GeoJsonHandler GeoData = new GeoJsonHandler();

    @BeforeEach
    public void setup() throws IOException {
        Spark.get("geo_data", GeoData);
        Spark.init();
        Spark.awaitInitialization();
    }

    /**
     * Stop Spark listening on endpoint and ensures that it doesn't proceed unless this has
     * happened
     */
    @AfterEach
    public void teardown() {
        Spark.unmap("geo_data");
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

    @Test
    public void testWordInput() throws IOException {
        HttpURLConnection clientConnection = tryRequest("geo_data?minLat=a&maxLat=b&minLon=c&maxLon=d");
        assertEquals(200, clientConnection.getResponseCode());
        Moshi moshi = new Moshi.Builder().build();
        Map response =
                moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "error_bad_json");
        assertEquals(expectedResponse, response);
        clientConnection.disconnect();
    }

    @Test
    public void testNoInput() throws IOException {
        HttpURLConnection clientConnection = tryRequest("geo_data");
        assertEquals(200, clientConnection.getResponseCode());
        Moshi moshi = new Moshi.Builder().build();
        Map response =
                moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "error_bad_json");
        assertEquals(expectedResponse, response);
        clientConnection.disconnect();
    }

    @Test
    public void testInvalidThenValid() throws IOException {
        HttpURLConnection clientConnection = tryRequest("geo_data");
        assertEquals(200, clientConnection.getResponseCode());
        Moshi moshi = new Moshi.Builder().build();
        Map response =
                moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "error_bad_json");
        assertEquals(expectedResponse, response);


        clientConnection = tryRequest("geo_data?minLat=33&maxLat=35&minLon=-86&maxLon=-84");
        assertEquals(200, clientConnection.getResponseCode());
        moshi = new Moshi.Builder().build();
        response =
                moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        expectedResponse.clear();
        expectedResponse.put("result", "success");
        assertEquals(expectedResponse.get("result"), response.get("result"));
        clientConnection.disconnect();
    }

    @Test
    public void testValidThenInvalid() throws IOException {
        HttpURLConnection clientConnection = tryRequest("geo_data?minLat=33&maxLat=35&minLon=-86&maxLon=-84");
        assertEquals(200, clientConnection.getResponseCode());
        Moshi moshi = new Moshi.Builder().build();
        Map response =
                moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "success");
        assertEquals(expectedResponse.get("result"), response.get("result"));


        clientConnection = tryRequest("geo_data");
        assertEquals(200, clientConnection.getResponseCode());
        moshi = new Moshi.Builder().build();
        response =
                moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        expectedResponse.clear();
        expectedResponse.put("result", "error_bad_json");
        assertEquals(expectedResponse, response);
        clientConnection.disconnect();
    }

    /**
     * Testing the load API response given a correct filepath, expected to return a success response
     *
     * @throws IOException
     */
    @Test
    public void testGeoDataSuccess() throws IOException {
        HttpURLConnection clientConnection = tryRequest("geo_data?minLat=33&maxLat=35&minLon=-86&maxLon=-84");
        assertEquals(200, clientConnection.getResponseCode());
        Moshi moshi = new Moshi.Builder().build();
        Map response =
                moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "success");
        assertEquals(expectedResponse.get("result"), response.get("result"));
        clientConnection.disconnect();
    }

    @Test
    public void getSuccessResponse() throws IOException {
        GeoJsonHandler handler = new GeoJsonHandler();
        GeoJsonHandler.FeatureCollection features = handler.getData();
        assertNotNull(features);
        assertNotNull(features.features());
    }

    @Test
    public void filterTestSuccess() throws IOException {
        GeoJsonHandler handler = new GeoJsonHandler();
        GeoJsonHandler.FeatureCollection features = handler.getData();
        List<GeoJsonHandler.Features> filterFeatures = handler.filterFeatures(-86, -84, 33, 35);
        assertNotNull(filterFeatures);
        assertNotNull(features.features());
    }

    @Test
    public void filterFeatures() throws IOException {
        GeoJsonHandler handler = new GeoJsonHandler();
        handler.setData("data/redlining/redlining_mock.geojson");
        GeoJsonHandler.FeatureCollection features = handler.getData();
        List<GeoJsonHandler.Features> filterFeatures = handler.filterFeatures(0, 1000, 0, 1000);
        assertEquals(filterFeatures.size(),3);

        List<GeoJsonHandler.Features> filterFeatures2 = handler.filterFeatures(101, 106, 0, 2);
        assertEquals(filterFeatures2.size(), 2);

        List<GeoJsonHandler.Features> filterFeatures3 = handler.filterFeatures(101, 103, 0, 2);
        assertEquals(filterFeatures3.size(), 1);

        List<GeoJsonHandler.Features> filterFeatures4 = handler.filterFeatures(107, 108, 0, 2);
        assertEquals(filterFeatures4.size(), 0);
    }

    //ensure that it filters out of bounds
    @Test
    public void filterTestFailure() throws IOException {
        GeoJsonHandler handler = new GeoJsonHandler();
        List<GeoJsonHandler.Features> filterFeatures = handler.filterFeatures(0, 0, 0,0);
        assertEquals(filterFeatures.size(), 0);
    }

    @Test
    public void numGenerator() throws IOException {
        for (int i = 0; i < 100; i ++){
            int minLon = (int)(Math.random()* 1000);
            int maxLon = (int)(Math.random()* 1000);
            int minLat = (int)(Math.random()* 1000);
            int maxLat = (int)(Math.random()* 1000);
            HttpURLConnection clientConnection = tryRequest("geo_data?minLat=" + minLat+"&maxLat="+maxLat+"" +
                    "&minLon="+minLon+"&maxLon="+maxLon);
            assertEquals(200, clientConnection.getResponseCode());
            Moshi moshi = new Moshi.Builder().build();
            Map response =
                    moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        }
    }
}
