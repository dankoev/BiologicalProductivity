import { BP_API_PATH } from "../apiConfig";
import { GeneralError, ServerError } from "./share";
import { addPolyToMap, createHeatmapContainer } from "./ymapsControl";

async function requestForBpServer(method, URL, postParams) {
  return fetch(`${BP_API_PATH}/${URL}`, {
    method,
    ...postParams,
  })
    .then((response) => {
      if (response.ok) {
        return response;
      }
      if (response.status === 502) {
        throw new GeneralError("Server don't work");
      }
      return response.text().then((text) => {
        throw new GeneralError(text);
      });
    })
    .catch((err) => {
      throw new ServerError(`Server error: ${err.message}`);
    });
}
export async function getHeatmapFromServer(areaInfoWithType) {
  return await requestForBpServer("POST", "getHeatMapOfSector", {
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(areaInfoWithType),
  })
    .then(async (data) => [await data.blob(), data.headers])
    .then(([heatmapBlob, headers]) => {
      return {
        getBlob: () => heatmapBlob,
        createPolyFromHeatMap: () =>
          createPolyFromHeatMap(heatmapBlob, areaInfoWithType, headers),
      };
    })
    .catch((err) => {
      if (err instanceof ServerError) {
        throw err;
      }
      throw new GeneralError("Server data corrupted");
    });
}
async function createPolyFromHeatMap(heatmapBlob, areaInfoWithType, headers) {
  const { type, bounds, coords } = areaInfoWithType;
  const blobURL = URL.createObjectURL(heatmapBlob);
  const container = createHeatmapContainer(bounds, type, coords, blobURL);
  const areaStatistics = [...headers.entries()]
    .filter(([key, _]) => key.slice(0, 4) == "info")
    .reduce((acc, [infoKey, infoVal]) => {
      acc[infoKey] = infoVal;
      return acc;
    }, {});

  return {
    getPoly: () => container,
    addToMap: () => addPolyToMap(container),
    addToMapWithStatistic: () => {
      addPolyToMap(container);
      container.properties.set({ areaStatistics });
    },
  };
}
