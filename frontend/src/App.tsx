import React from 'react';
import MapDemo from './MapDemo'
import './App.css';
import CreateMap from './MapDemo';

function App() {
  return (
    <div className="App">
      <p className = "header">
        Redlining Data in the United States
      </p>
      <CreateMap />      
    </div>
  );
}

export default App;
