//function createPolyWithSector(blobURL, sectorData) {
//  const { name, cornerCoords } = sectorData;
//  const myPolygon = new ymaps.Polygon(
//    [
//      cornerCoords,
//    ],
//    {
//      balloonContent: name,
//    },
//    {
//      fillImageHref: blobURL,
//      fillMethod: 'stretch',
//      stroke: false,
//      opacity: 0.8,
//    },
//  );
//  myMap.map.geoObjects.add(myPolygon);
//}
//async function loadImg(sectorData, pathToSectors) {
//  // fetch()
//  console.log(`start load ${sectorData.name}`);
//  await fetch('/getHeatMapByPath', {
//    method: 'POST',
//    headers: {
//      'Content-Type': 'application/json',
//    },
//    body: JSON.stringify({
//      path: `${pathToSectors}/${sectorData.name}`,
//    }),
//  })
//    .catch((e) => console.error(e))
//    .then((response) => response.blob())
//    .then((blob) => {
//      const blobURL = URL.createObjectURL(blob);
//      createPolyWithSector(blobURL, sectorData);
//      console.log('URLcreated');
//    });
//  console.log('load to polygon');
//}
//async function showSectors(sectorsInfo) {
//  const { pathToSectors, sectorsData } = sectorsInfo;
//
//  await sectorsData.map((item) => loadImg(item, pathToSectors));
//  console.log('DOne'); // "готово!"
//}
//function getSectorsInfo(request) {
//  fetch('/getSectorsInfoByPolygon', {
//    method: 'POST',
//    headers: {
//      'Content-Type': 'application/json',
//    },
//    body: JSON.stringify(request),
//  })
//    .then((response) => response.json())
//    .then((sectorsInfo) => {
//      showSectors(sectorsInfo);
//    });
//}
function createPolyWithSector(blobURL, sectorCoords) {
  const myPolygon = new ymaps.Polygon(
    [
      [sectorCoords[0],
       [sectorCoords[1][0],sectorCoords[0][1]],
       sectorCoords[1],
       [sectorCoords[0][0],sectorCoords[1][1]],]
    ],
    {
      balloonContent: name,
    },
    {
      fillImageHref: blobURL,
      fillMethod: 'stretch',
      strokeWidth: 2,
      opacity: 0.8,
    },
  );
  myMap.map.geoObjects.add(myPolygon);
}

async function getSector(requestCoords) {
  console.log(`request sector `);

  await fetch('/getHeatSector', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestCoords)
  })
    .catch((e) => console.error(e))
    .then((data) => data.blob())
    .then((blob) => {
      const blobURL = URL.createObjectURL(blob);
      const { sectorCoords, } = requestCoords;
      createPolyWithSector(blobURL, sectorCoords);
    });
}

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#calculPolygon').onclick = () => {
    const sectorCoords = [[100.3918,56.3898],
                         [104.1271,54.9368]];
//    getSectorsInfo(request);
    getSector(
      {
        sectorCoords,
        type: 'H',
      }
    );
  };
});

