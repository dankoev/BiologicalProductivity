const mapData = {
  map: null,
  zoom: 5,
  coordinatesCenter: [107.88, 54.99],
};
function showPlacemarkCoords(coordinates) {
  console.log(coordinates);
  $('#placemarkCoords').text(`Долгота: ${coordinates[0]}\nШирота: ${coordinates[1]}`);
}
function createPlacemark(events, isDraggable) {
  const coords = events.get('coords');
  const placemark = new ymaps.Placemark(
    coords,
    {},
    {
      preset: 'islands#redIcon',
      draggable: isDraggable,
    },
  );
  showPlacemarkCoords(coords);
  return placemark;
}
function addPlacemarkEvent(placemark, action) {
  placemark.events.add(action, function () {
    console.log(this);
    const newCoord = this.geometry.getCoordinates();
    showPlacemarkCoords(newCoord);
  }, placemark);
}
function createMap() {
  mapData.map = new ymaps.Map('map', {
    center: mapData.coordinatesCenter,
    zoom: mapData.zoom,
    controls: ['zoomControl', 'typeSelector'],
  });
}
function addMapEvent() {
  mapData.map.events.add('click', (events) => {
    const placemark = createPlacemark(events, true);
    addPlacemarkEvent(placemark, 'dragend');
    mapData.map.geoObjects.add(placemark);
  });
}
function init() {
  createMap();
  addMapEvent();
}
ymaps.ready(init);
