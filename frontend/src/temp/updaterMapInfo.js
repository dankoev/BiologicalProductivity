const {readFile, writeFile} = require('fs/promises')
const path = require('path')
const pathToInterfaceInfo = path.resolve('./src/data/interfaceInfo.json')
const interfaceInfo = require(pathToInterfaceInfo)

console.log(pathToInterfaceInfo)

async function readHtml(path) {
  return await readFile(path, 'utf8')
}

function transformData(mapsInfos) {
  return [...mapsInfos].map(el => {
    if (el[1] === el[3]) {
      return {
        type: el[1],
        description: el[2]
      }
    }
  })
}

function updateDescriptionInJSON(interfaceInfo, transformedDataFromHTML) {
  const updatedInterfaceInfo = interfaceInfo
    .map(mapInfoJSON => {
      const newMapInfo = transformedDataFromHTML
        .find(mapInfoHTML => mapInfoJSON.type === mapInfoHTML.type)
      mapInfoJSON.description = newMapInfo?.description ?? "No description"
      return mapInfoJSON
    })
  writeFile(pathToInterfaceInfo, JSON.stringify(updatedInterfaceInfo, null, 2))
    .catch(err => console.log(err))
}

function splitMapsInfos(infoHTMLasText) {
  const spliter = /<!--beginning\s([A-Z]+?)-->(.*?)<!--([A-Z]+?) end-->/gs
  const mapsInfos = infoHTMLasText.matchAll(spliter)
  return new Promise(resolve => resolve(transformData(mapsInfos)))
}


readHtml(path.resolve(__dirname, "mapsInfosTemplate.html"))
  .then(htmlText => splitMapsInfos(htmlText))
  .then(htmlTransformInfo => updateDescriptionInJSON(interfaceInfo, htmlTransformInfo))
  .catch(err => console.error('Error read file with mapsInfos\n' + err))

