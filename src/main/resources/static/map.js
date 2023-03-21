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
function createLayer() {
  console.log('creating layer...');
  const tileUrlTemplate = '/hotspot_data/%z/%x/%y';
  const keyTemplate = 'tile_%c';
  const imgUrlTemplate = '/img/tile_x_%x_y_%y_z_%z.jpeg';
  console.log('Init succses');
  const imgLayer = new ymaps.Layer(imgUrlTemplate, { tileTransparent: true });
  const objSource = new ymaps.hotspot.ObjectSource(tileUrlTemplate, keyTemplate);
  const hotspotLayer = new ymaps.hotspot.Layer(objSource, { cursor: 'help' });

  console.log(hotspotLayer);
  mapData.map.layers.add(hotspotLayer);
  mapData.map.layers.add(imgLayer);
  console.log('Succses creating layer');
}
function createPolyWithIMG() {
  const myPolygon = new ymaps.Polygon(
    [
      [
        [99.5819031614, 56.32701098489841], [103.9162531614001, 56.32701098489841],
        [103.9162531614001, 54.87637711769787], [99.5819031614, 54.87637711769787],
      ],
    ],
    {
      balloonContent: 'Сектор 26',
    },
    {
      fillImageHref: 'img/tile_x_50_y_20_z_6.jpeg',
      fillMethod: 'stretch',
      stroke: false,
      opacity: 0.8,
    },
  );
  mapData.map.geoObjects.add(myPolygon);
}
function init() {
  createMap();
  addMapEvent();
  // createLayer();
  createPolyWithIMG();
}
ymaps.ready(init);
