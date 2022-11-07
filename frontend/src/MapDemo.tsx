//import './Puzzle.css';
import React, { useEffect, useState } from 'react';
import Map, {    
   ViewState, ViewStateChangeEvent,
   MapLayerMouseEvent,
   Source, Layer } from 'react-map-gl'  

// This won't be pushed to the repo; add your own!
// You'll need to have the file within the 'src' folder, though
// (Make sure it's a "default public" key. I'm just protecting my 
//  key from denial-of-service attack...)
import {myKey} from './private/keyStore'; 

import {overlayData, geoLayer} from './overlays' 

export default function Puzzle() {
  // Providence is at {lat: 41.8245, long: -71.4129}

  const [viewState, setViewState] = useState<ViewState>({
    longitude: -71.4129,
    latitude: 41.8245,
    zoom: 10,
    bearing: 0,
    pitch: 0,
    // This isn't required if we look at the docs...
    // https://visgl.github.io/react-map-gl/docs/api-reference/types
    // Unfortuntely, that seems to have changed. See:
    // https://docs.mapbox.com/mapbox-gl-js/api/properties/#paddingoptions
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  });  
  
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined)

  // Run this _once_, and never refresh (empty dependency list)
  useEffect(() => {
    setOverlay(overlayData)
  }, []) 

  return (
    <div className="map-demo">
      <div className="map-demo-map">   
        {/* We could use {...viewState} for the 6 viewState fields, 
            but "spread" syntax wasn't covered in class. */}
        <Map 
         mapboxAccessToken={myKey}
         latitude={viewState.latitude}
         longitude={viewState.longitude}
         zoom={viewState.zoom}
         pitch={viewState.pitch}
         bearing={viewState.bearing}
         padding={viewState.padding}
         onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)} 
         onClick={(ev: MapLayerMouseEvent) => console.log(ev.lngLat.lat, ev.lngLat.lng)}
         // This is too big, and the 0.9 factor is pretty hacky
         style={{width:window.innerWidth, height:window.innerHeight*0.9}} 
         mapStyle={'mapbox://styles/mapbox/dark-v10'}>

          <Source id="geo_data" type="geojson" data={overlay}>
                    <Layer {...geoLayer} />
                  </Source>
        </Map>       
      </div>
      <div className='map-status'>
        {`lat=${viewState.latitude.toFixed(4)},
          long=${viewState.longitude.toFixed(4)},
          zoom=${viewState.zoom.toFixed(4)}`}
      </div>
    </div>
  );
}
