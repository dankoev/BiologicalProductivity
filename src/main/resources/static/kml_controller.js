
function kmlLoadInit() {
  const loadBtn = document.querySelector("#load-kml-container button")
  loadBtn.addEventListener('click', (e) => {
    console.log('start load kml')
    ymaps.geoXml.load('https://drive.google.com/u/0/uc?id=1BZscRohGijQSMfrGDF9Ha2xTwSBTtwSF&export=download')
      .then(onGeoXmlLoad)
      .catch(e => console.log(e))
    e.target.disabled = true;
  })

  const onGeoXmlLoad = (res) => {
    console.log(res.geoObjects)
    try {
      const polygon = checkGeoObjsContent(res)

      mapController.map.geoObjects.add(polygon);
      mapController.map.setBounds(polygon.geometry.getBounds());

      mapController.selectedArea = polygon;
    } catch (error) {
      showMessage(error.message, messageType.error)
    }
  }
  const checkGeoObjsContent = (res) => {
    let polygonCount = 0
    let onlyPolygon
    res.geoObjects.each((gObj) => {
      if (gObj.geometry.getType() === "Polygon") {
        polygonCount += 1
        onlyPolygon = gObj
      }
    })
    if (polygonCount != 1) {
      throw new Error("Error load KML file. Number polygon > 1")
    }
    return onlyPolygon;
  }
}