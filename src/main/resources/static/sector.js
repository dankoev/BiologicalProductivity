
function createPolyWithSector(blobURL, sectorCoords) {
  const poly = new ymaps.Polygon(
    [[sectorCoords[0],
      [sectorCoords[1][0],sectorCoords[0][1]],
      sectorCoords[1],
      [sectorCoords[0][0],sectorCoords[1][1]],
    ]],
    {
      balloonContentHeader : "Area info",
      balloonContent: `max: \n min: \n average:`,
      hintContent: "fds"
    },
    {
      fillImageHref: blobURL,
      fillMethod: 'stretch',
      opacity: 0.8,
    },
  );
  leftSizebar.querySelector('#area-btns .delete-btn').click();
  mapController.map.geoObjects.add(poly);
}

async function getSector(requestCoords) {
  await fetch('/getHeatSector', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestCoords)
  })
    .then(response => {
      if (response.ok) {
        return response;
      }
      return response.text()
        .then( text => {throw new Error(text)})
    })
    .then(data => data.blob())
    .then(blob => {
      const blobURL = URL.createObjectURL(blob);
      const { sectorCoords, } = requestCoords;
      createPolyWithSector(blobURL, sectorCoords);
      setLoadState(loadState.hide)
    })
    .catch(e => {
      showMessage(e, messageType.error)
      setLoadState(loadState.hide)
    })
  console.log(`request heatmap sent`);
}

document.addEventListener('DOMContentLoaded', () => {
  let leftSizebar = document.querySelector('#left-sizebar');
  leftSizebar.querySelector('#calculatePolygon').onclick = () => {
    const sectorCoords =  mapController.selectedArea.geometry.getBounds();
    const areaCoords =  mapController.selectedArea.geometry.getCoordinates()[0];
    const heatMapType = leftSizebar.querySelector('#heatmap-type input[name="choiceMap"]:checked')
    setLoadState(loadState.show)
    getSector(
      {
        sectorCoords,
        type: heatMapType.value,
        areaCoords
      }
    );
  };
});

