
function kmlLoadInit() {
    console.log('sas')
    const loadBtn = document.querySelector("#load-kml-container button")
    loadBtn.addEventListener('click', (e) => {
        console.log('start load kml')
        ymaps.geoXml.load('/test.kml')
            .then(onGeoXmlLoad)
            .catch(e => console.log(e))
        e.target.disabled = true;
    })

    const onGeoXmlLoad = (res) => {
        console.log("Adding from kml")
        mapController.map.geoObjects.add(res.geoObjects);
        if (res.mapState) {
            res.mapState.applyToMap(myMap);
        }
        else {
            mapController.map.setBounds(res.geoObjects.getBounds());
        }
    }
}