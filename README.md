# Integration

1. Project Details
2. Backend
3. Frontend
4. How to Run the Program
5. Tests 
6. Errors
7. Contributors/Resources


# Project Details 
- At a high level, this project is made up of the `frontend` and `backend` directores. The frontend directory contains the UI where the user can make queries and the backend process API's for different commands. 

- Midian Yoseph (myoseph), Santiago Cortabarria (scortaba)

- Estimated Time : 15 hours

- Github Link: https://github.com/cs0320-f2022/integration-myoseph-scortaba.git


# Backend 
The `backend` directory contains code that was adapted from `Sprint 2` with an additional `geo_data` endpoint. This new endpoint allows the user to filter the redlining data received geographically by providing a bounding box in the query. An example query would look like the following:

`http://localhost:3232/geo_data?minLat=31.7457&maxLat=35.7457&minLon=-86.311&maxLon=-82.311`

# Frontend
The `frontend` directory contains:

### 1. `index.tsc`
This file imports the `App` component inside the `div` with `id=root` which is used in the `index.html` file. This ensures that the components defined in the class can be seen and used as users interact with the webapp.

### 2. `App.tsx`
This file imports the `MapDemo` class which contains most of the logic for the functionality of the Map

### 3. `overlay.tsx`
This file is used to fetch the data from the `geo_data` API in the backend. The function `fetchMapData` returns all redlining data for areas that are provided within the given bounding box of a query.

### 4. `MapDemo.tsx`
This file is responsible for tying all of the frontend components together. 
- It updates the city, state, and name fields of the point on the map that the user most recently clicked on.
- It obtains the data that was fetched from `overlay.tsx` and properly sets the those values to seen on the map. 
 
# How to Run the Program 
To be able to run this project you must have 2 terminals running.

In the first terminal navigate to the `backend` directory and run the `main` method in the `Server` class. Upon doing so you will see a message in the terminal indicating that the "**Server Started**".
You must then write a query to the `geo_data` endpoint to properly recieve the bounding box data to for the map. To do this, one must open a browser and type in "localhost:3232/geo_data?minLat={minimum latitude}&maxLat={maximum latitude}&minLon={minimum longitude}&maxLon={maximum longitude}" inputting the correct ranges of latitude and longitude. 

In the second terminal navigate to the `frontend` directory and first run `npm install` then run `npm start` to launch the map in your browser.

# Tests
Because most of the `backend` code was adapted from a previous sprint, tests for the validity of `get`, `stats`, and `weather` are handled in the that directory of this project. To run those tests navigate to the `backend` directory and run `mvn test`. The tests that we added for our geo_data endpoint are:
1. Tests that check error messages when inputting requests. Check for errors such as incorrect number of arguments, words passed in as arguments, and no input at all.
2. Tests that check how the program handles different requests in succession. We tested if the program reacted as expected when passing a valid input followed by an invalid one and vice versa.
3. Tests that check a valid request to the server.
4. Tests that check the filterFeatures method in the GeoJsonHandler class. These tests check if the filterFeature method filters the correct features using a mock GeoJson. Additionally we test filterFeature with min and max coordinates that are equal.
5. Finally, we have fuzz testing. This test runs 100 times, producing random coordinates and making requests with these coordinates. This is to make sure that our program can handle all possible cases of inputs.

To run the tests, navigate to the `frontend` directory and in the terminal run `npm test`.
1. Tests if title of webapp is present in the app
2. Tests if the state of the map when initially loading the map is at the correct location, one that we set
3. Tests if the city, state, and name fields are undefined when the user loads the map as the user has not interacted with the webapp

# Errors/Bugs
No known errors or bugs

# Contributors/Resources
Collaborators: Ayush Gupta (agupt137) and kmirfakh
