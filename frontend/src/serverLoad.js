import { GeneralError, ServerError } from "./share"
import { addPolyToMap, createHeatmapContainer } from "./ymapsControl"
import { BP_API_PATH } from "../apiConfig.js"

async function requestForBpServer(method, URL, postParams) {
  return fetch(`${BP_API_PATH}/${URL}`, {
    method,
    ...postParams
  })
    .then(response => {
      if (response.ok) {
        return response
      }
      if (response.status === 502) {
        throw new GeneralError("Server don't work")
      }
      return response.text()
        .then(text => {
          throw new GeneralError(text)
        })
    })
    .catch(err => {
      throw new ServerError(`Server error: ${err.message}`)
    })
}
async function getLastSectorStatistics() {
  return requestForBpServer('GET', 'getLastSectorStatistics')
    .then(data => data.json())
    .then(jsonResponse => jsonResponse)
    .catch(_ => new GeneralError("Getting statistics from server error"))
}
export async function getHeatmapFromServer(areaInfoWithType) {
  return await requestForBpServer('POST', 'getHeatMapOfSector', {
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(areaInfoWithType)
  })
    .then(data => data.blob())
    .then(heatmapBlob => {
      return {
        getBlob: () => heatmapBlob,
        createPolyFromHeatMap: () => createPolyFromHeatMap(heatmapBlob, areaInfoWithType)
      }
    })
    .catch(err => {
      if (err instanceof ServerError) {
        throw err
      }
      throw new GeneralError("Server data corrupted")
    })

}
async function createPolyFromHeatMap(heatmapBlob, areaInfoWithType) {
  const { type, bounds, coords } = areaInfoWithType
  const blobURL = URL.createObjectURL(heatmapBlob)
  const container = createHeatmapContainer(bounds, type, coords, blobURL)
  return {
    getPoly: () => container,
    addToMap: () => addPolyToMap(container),
    addToMapWithStatistic: () => {
      addPolyToMap(container)
      getLastSectorStatistics()
        .then(data => {
          container.properties.set({ areaStatistics: data })
        })
    }
  }
}
