package edu.brown.cs.student.server;

import static spark.Spark.after;

import edu.brown.cs.student.server.weather.WeatherHandler;
import spark.Spark;

import java.io.IOException;

/**
 * Top-level class for project. Contains the main() method which starts Spark and runs the various
 * handlers. We have five endpoints in this server, three of which share a share state of
 * (currentData).
 */
public class Server {
  public static void main(String[] args) throws IOException {
    CurrentData currentData = new CurrentData();
    currentData.setList(null);
    Spark.port(3232);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /load, /get, and /weather endpoints.
    Spark.get("load", new LoadHandler(currentData));
    Spark.get("get", new GetHandler(currentData));
    Spark.get("stats", new StatsHandler(currentData));
    Spark.get("weather", new WeatherHandler());
    Spark.get("geo_data", new GeoJsonHandler());
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
