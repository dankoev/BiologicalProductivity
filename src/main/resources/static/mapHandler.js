// function createLayer() {
//   console.log('creating layer...');
//   const tileUrlTemplate = '/hotspot_data/%z/%x/%y';
//   const keyTemplate = 'tile_%c';
//   const imgUrlTemplate = '/img/tile_x_%x_y_%y_z_%z.jpeg';
//   console.log('Init succses');
//   const imgLayer = new ymaps.Layer(imgUrlTemplate, { tileTransparent: true });
//   const objSource = new ymaps.hotspot.ObjectSource(tileUrlTemplate, keyTemplate);
//   const hotspotLayer = new ymaps.hotspot.Layer(objSource, { cursor: 'help' });

//   console.log(hotspotLayer);
//   mapData.map.layers.add(hotspotLayer);
//   mapData.map.layers.add(imgLayer);
//   console.log('Succses creating layer');
// }
let myMap = null;
function init() {
  myMap = new YMap('map', [107.88, 54.99]);
  myMap.setDisplayField('#placemarkCoords');
  myMap.eventCreatePlacemark('click');
}
ymaps.ready(init);
