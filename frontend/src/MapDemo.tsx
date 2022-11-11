import React, { Dispatch, RefObject, SetStateAction, useEffect, useRef, useState } from 'react';
import Map, {    
   ViewState, ViewStateChangeEvent,
   MapLayerMouseEvent,
   Source, Layer, MapRef, MapboxGeoJSONFeature } from 'react-map-gl'  

import {myKey} from './private/keyStore'
import {geoLayer, fetchMapData} from './overlays' 
export const TEXT_no_location_selected = "Nothing is selected"
export const TEXT_location_data_not_found = "No data found for this point"

/**
 * This function finds the current location on the map based on where the user has clicked the screen. It access the GEOJson data and checks the properties and sets the value of the city, name, and state based on whether ti can access these valies. 
 * @param ev 
 * @param mapRef 
 * @param setLocationData 
 */
function findLocation(ev: MapLayerMouseEvent, mapRef: RefObject<MapRef>, 
  setLocationData: Dispatch<SetStateAction<any>>){

  //Responses set for the city, state and name
  let name_response = TEXT_location_data_not_found
  let city_response = TEXT_location_data_not_found
  let state_response = TEXT_location_data_not_found
  
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
  
  //Sets the location data based on responses of if statements above
  setLocationData({state: state_response, city: city_response, name: name_response})
}


type LocationData = {
  state: string,
  city: string,
  name: string
}

/**
 * Function contains most of the logic for the map. 
 * It initially sets the city, name, and state to indicate that nothing has been selected
 * It obtains the data that has been fetched from overlay.ts and sets the state to the data such that the map can reflect this data
 * 
 * @returns 
 */
export default function MapDemo() {

  const [locationData, setLocationData] = useState<LocationData>({
    state: TEXT_no_location_selected,
    city: TEXT_no_location_selected,
    name: TEXT_no_location_selected
  });

  const mapRef = useRef<MapRef>(null)

  //Initially sets the location to Atlanta 
  const [viewState, setViewState] = useState<ViewState>({
    longitude: -84.3110,
    latitude: 33.7457,
    zoom: 10,
    bearing: 0,
    pitch: 0,
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  });  
  
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined)

  //obtains the fetched data and reflects it on the map
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
