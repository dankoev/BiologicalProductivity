
async function createPolyWithSector(blobURL, sectorCoords, sectorType) {
  const sectorInfoPromise = getLastSectorStatistics()
  let sectorStatistics
  await sectorInfoPromise.then(data => sectorStatistics = data)
  // console.log(sectorStatistics)
  const poly = new ymaps.Polygon(
    [[sectorCoords[0],
    [sectorCoords[1][0], sectorCoords[0][1]],
    sectorCoords[1],
    [sectorCoords[0][0], sectorCoords[1][1]],
    ]],
    {
      balloonContentHeader: "Area info",
      balloonContent: ``,
      hintContent: "show information",
      areaStatistics: sectorStatistics,
      sectorType: sectorType,

    },
    {
      balloonContentLayout: mapTemplates.balloonContentLayout,
      fillImageHref: blobURL,
      fillMethod: 'stretch',
      opacity: 0.8,
    },
  )
  setLayerType(sectorType)
  mapController.showOrHidePoligonsOnTypes(sectorType)
  mapController.map.geoObjects.add(poly)
  return poly
}

async function createAndShowArea(sectorInfoRequest) {
  await fetch('/getHeatMapOfSector', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(sectorInfoRequest)
  })
    .then(response => {
      if (response.ok) {
        return response
      }
      return response.text()
        .then(text => { throw new Error(text) })
    })
    .then(data => data.blob())
    .then(blob => {
      const blobURL = URL.createObjectURL(blob)
      const { sectorCoords, type } = sectorInfoRequest
      createPolyWithSector(blobURL, sectorCoords, type)
      setLoadState(loadState.hide)
    })
    .catch(e => {
      showMessage(e, messageType.error)
      setLoadState(loadState.hide)

    })
    .finally(console.log(`request heatmap sent`))
}
async function getLastSectorStatistics() {
  return await fetch('/getLastSectorStatistics', {
    method: 'GET',
  })
    .then(response => {
      if (response.ok) {
        return response
      }
      return response.text()
        .then(text => { throw new Error(text) })
    })
    .then(data => data.json())
    .then(jsonResponse => jsonResponse)
    .catch(e => {
      showMessage(e, messageType.error)
    })
}

document.addEventListener('DOMContentLoaded', () => {
  let leftSizebar = document.querySelector('#left-sizebar')
  leftSizebar.querySelector('#calculatePolygon').onclick = () => {
    const transformArrayCoords = (coords) => {
      return coords.map(el => [el[0], el[1]])
    }

    const sectorCoords = mapController.selectedArea.geometry.getBounds()
    const areaCoords = mapController.selectedArea.geometry.getCoordinates()[0]
    const areaCoordsTransform = transformArrayCoords(areaCoords)

    const heatMapType = leftSizebar.querySelector('#heatmap-types input[name="choiceMap"]:checked')
    setLoadState(loadState.show)
    createAndShowArea({
      sectorCoords,
      type: heatMapType.value,
      areaCoords: areaCoordsTransform,
    })
  }

})

