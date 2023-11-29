import { BP_CONTEXT } from "../../apiConfig.js";
import { readFileSync } from "fs";
import { readFile, writeFile } from "fs/promises";
import * as path from "path";
const pathToInterfaceInfo = path.resolve("./src/data/interfaceInfo.json");
const interfaceInfo = JSON.parse(readFileSync(pathToInterfaceInfo));

console.log(pathToInterfaceInfo);

async function readHtml(path) {
  return readFile(path, "utf8");
}

function transformData(mapsInfos) {
  const imageFinder = /(<img.*src=['"])(.*['"])>/;
  return [...mapsInfos].map((el) => {
    if (el[1] === el[3]) {
      el[2] = el[2].replace(imageFinder, `$1${BP_CONTEXT}$2`);
      return {
        type: el[1],
        description: el[2],
      };
    }
  });
}

function updateDescriptionInJSON(interfaceInfo, transformedDataFromHTML) {
  const updatedInterfaceInfo = interfaceInfo.map((mapInfoJSON) => {
    const newMapInfo = transformedDataFromHTML.find(
      (mapInfoHTML) => mapInfoJSON.type === mapInfoHTML.type,
    );
    mapInfoJSON.description = newMapInfo?.description ?? "No description";
    return mapInfoJSON;
  });
  writeFile(
    pathToInterfaceInfo,
    JSON.stringify(updatedInterfaceInfo, null, 2),
  ).catch((err) => console.log(err));
}

function splitMapsInfos(infoHTMLasText) {
  const spliter =
    /<!--beginning\s([A-Z,a-z]+)-->(.*?)<!--([A-Z,a-z]+) end-->/gs;
  const mapsInfos = infoHTMLasText.toString().matchAll(spliter);
  return new Promise((resolve) => resolve(transformData(mapsInfos)));
}

readHtml(path.resolve("./src/temp/", "mapsInfosTemplate.html"))
  .then((htmlText) => splitMapsInfos(htmlText))
  .then((htmlTransformInfo) =>
    updateDescriptionInJSON(interfaceInfo, htmlTransformInfo),
  )
  .catch((err) => console.error("Error read file with mapsInfos\n" + err));
