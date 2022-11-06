# Sprint 2: API Proxy


Project Details
---
For this project we have created an API server that allows users to see the contents of a
CSV file provided a filepath or obtain the current temperature of a location provided the
longitude.

Team Members: Aaron Freeman and Midian Yoseph (afreema9 and myoseph)

Github Link: https://github.com/cs0320-f2022/sprint-2-afreema9-myoseph.git

Total Time: 12 hours

<br />


How to Run the Program
---
Users must first go the Server class and run the main method, after doing so a message in the terminal 
will appear informing the user that the server has started. After receiving that message you can then
go your web browser and either look at the contents of a csv file or the weather.

To open a csv users must first **load** the csv of their choice by inputting the following into the browser:

`localhost:3232/load?filepath={somefilepath}`

If the filepath is valid, users will receive a message indicating that the loading is successfull.
Next, users can enter this command to **get** the contents of the csv file they just loaded:

`localhost:3232/get`

You can only **get** a csv file if you **load** it first, doing it the other way around will result in an error. 

To receive the current temperature of a location users can make a **weather** request by inputting the latitude 
and longitude of the target location:

`localhost:3232/weather?=lat{somelatitude}&lon={somelongitude}`

<br />

Design Choices
---

Weather:

The weather package contains the WeatherHandler class as well as 5 classes used for the 
deserialization of Json files that were returned by the NWS API. The weather handler makes two 
requests the the NWS API. The first request takes in latitude and longitude coordinates and returns 
a Json file with a "Properties" field that contains a "Forecast" field. This "Forecast" field has a 
link to forecast data. The second request uses the URL found in the "Forecast" field. The NWS API 
returns a Json with a "Properties" field. The "Properties" field contains a "Periods" field which 
contains forecast data for a number of time periods. The makeTempRequest method returns the 
"Temperature" field in the first period field, as this period is always the current period.

<br />

CurrentData:

This class is used to store the contents of a parsed CSV file. We chose to instantiate this object 
in the Server class so that the LoadHandler could populate the List with the parsed CSV, and the 
GetHandler could access the parsed data in the list and return the contents of the CSV file in Json 
form.

<br />

Responses:

We chose to create a public record class for each of the failure responses. This allowed each of the 
handlers to access the same failure responses. We chose to do this because the failure responses 
were the same for every handler. This also makes these common responses accessible to any developer
that wants to add their own datasource. The success responses were written within each individual 
handler because they are unique to their specific endpoints. All the responses are records 
because they are only used for storing data and reformatting it into a Json.

<br />

Error Response: 
The various error responses that we handled were interpreted in this way:

- error_bad_json: if the query parameter is missing or incorrectly typed, for example `filpth` instead of `filepath`. 
- error_bad_request: if the `get` was called before loading a file 
- error_datasource: if the filepath that the user typed does not exist or if the latitude and longitude that the user inputted is outside the NWS API range

<br />

Adding a New Data Source:
---
If a user wanted to add a new data source they would need to move into the server directory and create a new
Handler class that would be associated with handling the response to the new data source. This class would need
to be structured similarly to the handler classes present in this project in that they need to implement
the Routes interface. The logic for the response should be contained in the handle method that the class will inherit.

From here, you can move into the Server class within this directory and add a new endpoint. The format for adding
a new endpoint is similar to how the other three are handled. Have the key word for your endpoint as the
first argument and your newly created class as the second, like so:

`Spark.get("menu", new MenuHandler())`

If you would like to add error responses to your functionality, you can navigate to
the ErrorResponses directory and create a new record that contains the error response that you would like.

<br />

Testing
---
We have put tests for all of our handlers in one test class because Spark does not allow opening
multiple ports in one file.

Integration Testing:

Integration tests examine the behaviour of our API Server. In testing for our error and success
responses we had tested for the accuracy of our ErrorResponse class thus no individual unit tests
were created for those classes. The tests include:

- Test a load API failure response given no filepath
- Test a load API failure response given an incorrect filepath
- Test a successful load API response
- Test a successful get API response
- Test a successful get API response after multiple calls to a load response
- Test a get API failure response when calling get without load
- Test a successful weather API response
- Test a weather API failure given an improper target location

Unit Testing:

These tests covered the functionality of any additional methods we wrote within the handler
class, those include:
- Test to ensure we are properly formatting the coordinates that users put in to be used based on the requirements outlined in the method
- Test to ensure that the forecast link being obtained matches the one that is seen on the NWS API

<br />

Errors/Bugs
---

When the a GET request with endpoint "/weather" is sent to the server, the server an error response
when the request is first entered. If the request is refreshed the server returns the correct 
response for the given latitude and longitude. This seems like an issue with the NWS API because
there is not a mechanism in our request that would create this kind of inconsistency.

<br />
<br />

Contributors/Sources
---
schwalek, ycruztri
