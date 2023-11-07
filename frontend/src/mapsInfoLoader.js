import { GeneralError } from "./share";
import { BP_CONTEXT } from "../apiConfig.js"

const pathToMapsInfo = BP_CONTEXT + "/data/interfaceInfo.json"

async function fetchMapsInfo() {
  return fetch(pathToMapsInfo, {
    method: 'GET'
  })
    .then(data => data.json())
    .catch(err => {
      console.log(err)
      throw new GeneralError("Error loading info's about maps")
    })
}

function mapsInfoMemoizer(func) {
  let cache = {}
  return async (type) => {
    if (Object.keys(cache).length !== 0) {
      return cache[type]
    } else {
      const mapsInfos = await func()
      mapsInfos.forEach(mapInfo => {
        const {type, maxValue, minValue, description} = mapInfo
        cache[type] = {
          maxValue,
          minValue,
          description
        }
      })
      return cache[type]
    }
  }
}

export const getMapsInfo = mapsInfoMemoizer(fetchMapsInfo)

