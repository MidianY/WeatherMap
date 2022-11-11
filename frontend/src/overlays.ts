import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";

// Import the raw JSON file
import rl_data from "./mockData/fullDownload.json"


interface LoadMapResponse {
  result: string
  data: string
}


async function getMap(): Promise<string> {
  const SERVER = `http://localhost:3232`
  return fetch(`${SERVER}/geo_data?minLat=33&maxLat=35&minLon=-86&maxLon=-84`)
  .then(loadResponse => loadResponse.json())
  .then((loadResponseJson: LoadMapResponse) => {
      if (loadResponseJson.result == `success`) {
          console.log("response " + loadResponseJson.result);
          console.log("points: " + loadResponseJson.data);
          const mapData = loadResponseJson.data
          return mapData.toString()
      // if get fail, return get result (i don't think this would happen)
      } else {
        return 'unable to load map overlay data'
      }
      })
  }

   const rl_data1 = getMap()



// Type predicate for FeatureCollection
function isFeatureCollection(json: any): json is FeatureCollection {
    return json.type === "FeatureCollection"
}

export function overlayData(): GeoJSON.FeatureCollection | undefined {
  if(isFeatureCollection(rl_data))
    return rl_data
  return undefined
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

// import { FeatureCollection } from "geojson";
// import { FillLayer } from "react-map-gl";

// // Import the raw JSON file
// import rl_data from "./mockData/fullDownload.json"

// // Type predicate for FeatureCollection
// function isFeatureCollection(json: any): json is FeatureCollection {
//     return json.type === "FeatureCollection"
// }

//  let result: GeoJSON.FeatureCollection

// export async function overlayData(): Promise<GeoJSON.FeatureCollection | undefined> {
//   await fetch('http://localhost:3232/geo_data')
//   .then(response => {return response.json()}).then(responseObject => {result = (responseObject.data)})
//   if(isFeatureCollection(result))
//     return result
//   return undefined
// }
// ////////////////////////////////////

// const propertyName = 'holc_grade';

// export const geoLayer: FillLayer = {
//     id: 'geo_data',
//     type: 'fill',
//     paint: {
//         'fill-color': [
//             'match',
//             ['get', propertyName],
//             'A',
//             '#5bcc04',
//             'B',
//             '#04b8cc',
//             'C',
//             '#e9ed0e',
//             'D',
//             '#d11d1d',
//             /* other */ '#ccc'
//         ],
//         'fill-opacity': 0.2
//     }
// };
