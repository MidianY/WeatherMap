import React, { Dispatch, RefObject, SetStateAction, useEffect, useRef, useState } from 'react';
import Map, {    
   ViewState, ViewStateChangeEvent,
   MapLayerMouseEvent,
   Source, Layer, MapRef, MapboxGeoJSONFeature } from 'react-map-gl'  

// This won't be pushed to the repo; add your own!
// You'll need to have the file within the 'src' folder, though
// (Make sure it's a "default public" key. I'm just protecting my 
//  key from denial-of-service attack...)
import {myKey} from './private/keyStore'

import {overlayData, geoLayer} from './overlays' 

const TEXT_no_location_selected = "None selected"
const TEXT_location_data_not_found = "No data found for this point"


function computeLocation(ev: MapLayerMouseEvent, mapRef: RefObject<MapRef>, setLocationData: Dispatch<SetStateAction<any>>){

  //we set default responses 
  let name_response = TEXT_location_data_not_found
  let city_response = TEXT_location_data_not_found
  let state_response = TEXT_location_data_not_found

  //Parse GEOJson response
  const map = mapRef.current
  if(map != null){
    console.log(map.queryRenderedFeatures(ev.point))
    const GEOData: MapboxGeoJSONFeature = map.queryRenderedFeatures(ev.point)[0]
    if(GEOData !== undefined) {
      if (GEOData.properties != undefined) {
        if (GEOData.properties.hasOwnProperty("name")) {
          name_response = GEOData.properties.name
        }
        if (GEOData.properties.hasOwnProperty("city")) {
          city_response = GEOData.properties.city
        }
        if (GEOData.properties.hasOwnProperty("state")) {
          state_response = GEOData.properties.state
        }
      }
  }
  }
  setLocationData({state: state_response, city: city_response, name: name_response})
}

type LocationData = {
  state: string,
  city: string,
  name: string
}
export default function Puzzle() {
  // Providence is at {lat: 41.8245, long: -71.4129}

     const [locationData, setLocationData] = useState<LocationData>({
        state: TEXT_no_location_selected,
        city: TEXT_no_location_selected,
        name: TEXT_no_location_selected
      });
    
      const mapRef = useRef<MapRef>(null)

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
        
        ref={mapRef}
         mapboxAccessToken={myKey}
         latitude={viewState.latitude}
         longitude={viewState.longitude}
         zoom={viewState.zoom}
         pitch={viewState.pitch}
         bearing={viewState.bearing}
         padding={viewState.padding}
         onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)} 
         onClick={(ev: MapLayerMouseEvent) => {
          console.log(overlay);
          computeLocation(ev, mapRef, setLocationData)}
         }
         // This is too big, and the 0.9 factor is pretty hacky
         style={{width:window.innerWidth, height:window.innerHeight*0.9}} 
         mapStyle={'mapbox://styles/mapbox/light-v10'}>

          <Source id="geo_data" type="geojson" data={overlay}>
                    <Layer {...geoLayer} />
                  </Source>
        </Map>       
      </div>
      <div className='map-status'>
        {`lat=${viewState.latitude.toFixed(4)},
          long=${viewState.longitude.toFixed(4)},
          zoom=${viewState.zoom.toFixed(4)},
          city: ${locationData.city},
          state: ${locationData.state},
          name: ${locationData.name}`}
      </div>
    </div>
  );
}
