# Sprint 3: Terminal

1. Project Details
2. Backend
3. Frontend
4. How to Run the Program
5. Registering New Commands
6. Tests 
7. Errors
8. Contributors/Resources


# Project Details 
- At a high level, this project is made up of the `frontend` and `backend` directores. The fronetnd directory contains the UI where the user can make queries and the backend process API's for different commands. 

- Midian Yoseph (myoseph), Oscar McNally (omcnally)

- Estimated Time : 20 hours

- Github Link: https://github.com/cs0320-f2022/sprint-3-myoseph-omcnally.git 


# Backend 
The `backend` direcotry conaints code that was adapted from `Sprint 2` with an additional `stats` endpoint. This new endpoint calculates the number of rows and columns in a csv file. Apart from this, the backend logic remains unchanged and for more details you can follow this link: https://github.com/cs0320-f2022/sprint-2-afreema9-myoseph.git 


# Frontend
 The `frontend` directory contains the code that defines the `React` components for the webapp with the following main parts:

### 1. `index.tsc`
This file imports the `App` component inside the `div` with `id=root` which is used in the `index.html` file. This ensures that the components defined in the class can be seen and used as users interact with the webapp.


### 2. `App.tsx`
This file imports the `Input` class which contains most of the logic for the functionality of the frontend webapp 

### 3 Replfunction.ts
This file acts as a command-processor function for our REPL. If a developer would like to create a new command for the webapp to process, it must be created within this class and added to the `commandFunctionMap`. 

### 4. `Terminal.tsx`
This file contains most of the logic for the webapp where many of the `React` functional components are used to render different parts of the UI. 

- The `OldCommand` function represents a previous command based on our `inputOutput` history, which displays instances of this 
component for each previous command
- The `NewCommand` function handles processing the commands that are stored in `REPLFunction`. It checks the user input and ensures that it is a correct command that the REPL is able to process and updates the output accordingly. 


# How to Run the Program 
To be able to run this project you must have 2 terminals running.

In the first terminal navigate to the `backend` directory and run the `main` method in the `Server` class. Upon doing so you will see a message in the terminal indicating that the "**Server has Started**".

In the second terminal navigate to the `frontend` directory and type `npm start` to launch the fronted in your browser.

Once the window has loaded, users will see a command box prompting them to enter a query. The user can then do the following: 
- To look at the contents of a csv the user can  run `get <filepath>` in the command box. If it is a valid filepath users will see the contents of the csv file they have loaded. 

- To look at the number of rows and columns within a loaded csv file users can run `stats` in the command box. *Note: "stats" can only be ran after running the "get" command above*

- To receive the current temperature of a location users can search `weather <latitude> <longitude>` by inputting the latitude 
and longitude of the target location. If the location is within NWS API, users will see a success output along with the temperature.

# Registering New Commands
If a developer would like to define a new command for the webapp to process they must do the following:

1. Navigate to the `replfunction.ts` class and define a new function that containing the logic for the new command. (*If the developer would like to add an API endpoint into the backend of the code, you can look at this repository link for how to do that: https://github.com/cs0320-f2022/sprint-2-afreema9-myoseph.git*)

2. After defining the logic for your command, add it using the `registerCommand` function that is defined within this class. This stores new commands along with the logic for the command in a map. This map is used in the `Terminal.tsx` class and it is essential you do this in order for your command to be processed. 
    - For Example: if you created a new command that processed the menu count you would initially create a menu function and add it to the map like so:

    ```
    function menuCommand(args : string[]) {
        
        //logic for menu command method

        }
    
    registerCommand('menu', menuCommand)
    
    ```

# Tests
Because most of the `backend` code was adapted from a previous sprint, tests for the validity of `get`, `stats`, and `weather` are handled in the that direcotry of this project. To run those tests navigate to the `backend` directory and run `mvn test`

To run the tests, navigate to the `frontend` directory and in the terminal run `npm test`.

Within our `frontend` we have to main test classes:
- `App.test.tsx` class that tests for the funcitonality of the App class ensuring that all react components are behaving as expected. 

- `Terminal.test.tsx` class that tests the functionality for the `Terminal.tsx` class. In this test class we have the following tests:
    - Checks for the existence of the guess button component 
    - Checks for the existence of the guess input fields 
    - Tests for a correct get command 
    - Tests for 2 different correct get commands and ensures we get the most current data 
    - Tests a get command with no parameters
    - Tests correct stats command 
    - Tests a incorrect stats command 
    - Tests a stats command after get is ran twice 
    - Tests a correct weather command 
    - Tests an incorrect weather command 
    - Tests a weather with missing parameters 
    - Tests the ability to register a command

# Errors/Bugs
- Sometimes weather tests fail due to problems with the NWS API, usually after running the test a few more times the expected response will be seen. 

# Contributors/Resources
- cwill1-ehan31 : figuring out to resolve promises

