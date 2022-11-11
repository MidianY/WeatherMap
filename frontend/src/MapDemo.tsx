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

import { geoLayer, fetchMapData} from './overlays' 

const TEXT_no_location_selected = "Nothing is selected"
const TEXT_location_data_not_found = "No data found for this point"


function findLocation(ev: MapLayerMouseEvent, mapRef: RefObject<MapRef>, 
  setLocationData: Dispatch<SetStateAction<any>>){

  //we set default responses 
  let name_response = TEXT_location_data_not_found
  let city_response = TEXT_location_data_not_found
  let state_response = TEXT_location_data_not_found

  //Parse geoData response
  const map = mapRef.current
  if(map != null){
    const geoData: MapboxGeoJSONFeature = map.queryRenderedFeatures(ev.point)[0]
    if(geoData !== undefined) {
      if (geoData.properties != undefined) {
        if (geoData.properties.hasOwnProperty("name")) {
          name_response = geoData.properties.name
        }
        if (geoData.properties.hasOwnProperty("city")) {
          city_response = geoData.properties.city
        }
        if (geoData.properties.hasOwnProperty("state")) {
          state_response = geoData.properties.state
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
    longitude: -84.3110,
    latitude: 33.7457,
    zoom: 10,
    bearing: 0,
    pitch: 0,
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  });  
  
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined)

  useEffect(() => {
    fetchMapData(viewState.latitude - 2, viewState.latitude + 2, viewState.longitude - 2, viewState.longitude + 2)
        .then(overlay => setOverlay(overlay));
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
          findLocation(ev, mapRef, setLocationData)}
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
