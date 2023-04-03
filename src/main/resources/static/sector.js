function createPolyWithSector(blobURL, sectorData) {
  const { name, cornerCoords } = sectorData;
  const myPolygon = new ymaps.Polygon(
    [
      cornerCoords,
    ],
    {
      balloonContent: name,
    },
    {
      fillImageHref: blobURL,
      fillMethod: 'stretch',
      stroke: false,
      opacity: 0.8,
    },
  );
  myMap.map.geoObjects.add(myPolygon);
}
async function loadImg(sectorData, pathToSectors) {
  // fetch()
  console.log(`start load ${sectorData.name}`);
  await fetch('/getHeatMapByPath', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      path: `${pathToSectors}/${sectorData.name}`,
    }),
  })
    .catch((e) => console.error(e))
    .then((response) => response.blob())
    .then((blob) => {
      const blobURL = URL.createObjectURL(blob);
      createPolyWithSector(blobURL, sectorData);
      console.log('URLcreated');
    });
  console.log('load to polygon');
}
async function showSectors(sectorsInfo) {
  const { pathToSectors, sectorsData } = sectorsInfo;

  await sectorsData.map((item) => loadImg(item, pathToSectors));
  console.log('DOne'); // "готово!"
}
function getSectorsInfo(request) {
  fetch('/getSectorsInfoByPolygon', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })
    .then((response) => response.json())
    .then((sectorsInfo) => {
      showSectors(sectorsInfo);
    });
}

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#calculPolygon').onclick = () => {
    const request = {
      coordinates: myMap.poly.geometry.getCoordinates()[0],
      name: 'First',
    };
    getSectorsInfo(request);
  };
});

// const blobURL = URL.createObjectURL(blob);
//       map.poly.options.set('fillImageHref', blobURL);
//       console.log('img appended');
// $('#getHeatMap').click(() => {
//   $.ajax({
//     url: '/getHeatMap',
//     method: 'GET',
//     xhrFields: {
//       responseType: 'blob',
//     },
//     success(data) {
//       const imageUrl = URL.createObjectURL(data);
//       const img = $('<img>').attr({ src: imageUrl, id: 'sector' });
//       $('#imageContainer').append(img);
//     },
//   });
// });
