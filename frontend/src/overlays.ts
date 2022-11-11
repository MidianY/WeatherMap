import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";
import rl_data from "./mockData/fullDownload.json"

/**
 * This function checks wheter the json that is passed in is of type FeatureCollection such that the data can be plotted onto the map
 * @param json 
 * @returns true if json is a FeatureCollection
 */
 function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

const URL = "http://localhost:3232/";

/**
* This function fetches the data from the 'geo_data' enpoint in the backend and returns all redlining data for major United States cities for areas that are provided within the given bounding box.
 * 
 * @param minLat 
 * @param maxLat 
 * @param minLon 
 * @param maxLon 
 * @returns This funciton returns a Promise either containing the data for the map or a undefined value if there are invalid inputs
 */

export function fetchMapData(minLat: number, maxLat: number, minLon: number, maxLon: number): Promise<FeatureCollection | undefined> {
  const url: string =
  URL + `geo_data?minLat=${minLat}&maxLat=${maxLat}&minLon=${minLon}&maxLon=${maxLon}`;
  return fetch(url)
    .then((res) => res.json())
    .then((json) => {
      if (isFeatureCollection(json.data)) {
        {console.log("fulfilled"); return json.data};
      }
      return undefined;
    });
}

////////////////////////////////////

const propertyName = 'holc_grade';

export const geoLayer: FillLayer = {
    id: 'geo_data',
    type: 'fill',
    paint: {
        'fill-color': [
            'match',
            ['get', propertyName],
            'A',
            '#5bcc04',
            'B',
            '#04b8cc',
            'C',
            '#e9ed0e',
            'D',
            '#d11d1d',
            /* other */ '#ccc'
        ],
        'fill-opacity': 0.2
    }
};
