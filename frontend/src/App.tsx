import React from 'react';
import MapDemo from './MapDemo'
import './App.css';

/**
 * High level structure of the program
 * @returns html div with a header that instructs the user to input commands with
 *      the Terminal component displayed below
 */
function App() {
  return (
    <div className="App">
      <p className = "header">
        Redlining Data in the United States
      </p>
      <MapDemo />      
    </div>
  );
}

export default App;
