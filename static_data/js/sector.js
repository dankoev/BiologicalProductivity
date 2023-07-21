const ICtrl = intefaceController

async function createPolyWithSector(blobURL, sectorInfoRequest) {
  const { sectorCoords, type, areaCoords } = sectorInfoRequest
  const sectorStatisticsPromise = getLastSectorStatistics()
  let sectorStatistics
  sectorStatisticsPromise.then(data => sectorStatistics = data)
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
      sectorType: type,
      areaCoords,
    },
    {
      balloonContentLayout: mapTemplates.balloonContentLayout,
      fillImageHref: blobURL,
      fillMethod: 'stretch',
      opacity: 0.8,
    },
  )
  ICtrl.layerController.setLayerType(type)
  mapController.showOrHidePoligonsOnTypes(type)
  mapController.map.geoObjects.add(poly)
  return poly
}

function createAndShowArea(sectorInfoRequest) {
  return fetch('/bp-app/getHeatMapOfSector', {
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
      if (response.status === 405) {
        throw new GeneralError('Server Error')
      }
      return response.text()
        .then(text => { throw new Error(text) })
    })
    .then(data => data.blob())
    .then(blob => {
      const blobURL = URL.createObjectURL(blob)

      createPolyWithSector(blobURL, sectorInfoRequest)

      ICtrl.areaController
        .setLoadState(loadState.hide)
    })
    .catch(err => {
      ICtrl.messageController
        .showMessage(err)
      ICtrl.areaController
        .setLoadState(loadState.hide)

    })
    .finally(console.log(`request heatmap sent`))
}
async function getLastSectorStatistics() {
  return fetch('/bp-app/getLastSectorStatistics', {
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
    .catch(err => {
      ICtrl.messageController
        .showMessage(err)
    })
}

document.addEventListener('DOMContentLoaded', () => {
  const leftSizebar = document.querySelector('#left-sizebar')
  leftSizebar.querySelector('#calculatePolygon').onclick = (e) => {
    e.target.disabled = true
    console.log(e.target.disabled)
    const heatMapType = leftSizebar
      .querySelector('#heatmap-types input[name="choiceMap"]:checked')
      .value
    try {
      const sectorCoords = mapController.getBountsSelectedArea()
      const areaCoords = mapController.getCoordsSelectedArea()

      const alreadyExist = mapController.existSameArea(areaCoords, heatMapType)

      if (alreadyExist) {
        throw new GeneralWarning('Same area already has exist')
      }
      const areaOfArea = mapController.calculateAreaSelectedArea()

      if (areaOfArea > LARGE_AREA) {
        throw new GeneralWarning('Selected area is too large')
      }

      ICtrl.areaController
        .setLoadState(loadState.show)
      createAndShowArea({
        sectorCoords,
        type: heatMapType,
        areaCoords,
      })
        .then(() => e.target.disabled = false)


    } catch (err) {
      ICtrl
        .messageController
        .showMessage(err)
      e.target.disabled = false
    }

  }

})



