import ymaps from "ymaps"
import {GeneralError, areaState, typeMode} from "./share"
import {Subject, distinctUntilChanged} from "rxjs"
import {getStateStream} from "./sizebar"

const utils = ['util.calculateArea']
const controls = ['typeSelector']
const ymapID = 'map'
const zoom = 5
const centerCoords = [107.88, 54.99]

let selectedArea
let balloonContentLayout

function createBalloonContentLayout() {
  balloonContentLayout = ymaps.templateLayoutFactory.createClass(
    `<div>
        <p id="max"> Max: {{ properties.areaStatistics.maxSectorValue | default:"No data"  }} </p>
        <p id="min"> Min: {{ properties.areaStatistics.minSectorValue | default:"No data"  }} </p>
        <p id="average">Average: {{ properties.areaStatistics.averageSectorValue | default:"No data" }} </p>
        <button id="delete-heatmap"> Удалить </button>
      </div>`, {
      build: function () {
        balloonContentLayout.superclass.build.call(this)
        document.querySelector('#delete-heatmap').onclick = this.delete.bind(this)
      },

      clear: function () {
        document.querySelector('#delete-heatmap').removeEventListener('click', this.delete)
        balloonContentLayout.superclass.clear.call(this)
      },
      delete: function () {
        map.then(mapObj => {
          mapObj.geoObjects.remove(this.getData().geoObject)
        })
      },
    })
}

const map = new Promise(async (resolve, reject) => {
  const requre = {
    require: utils,
    successCallback: () => {
      console.log("Success ymaps loading ")
      createBalloonContentLayout()
    }
  }
  await ymaps.ready(requre)
    .catch(() => reject(new GeneralError("Error load ymaps")))

  resolve(new ymaps.Map(
    ymapID, {
      center: centerCoords,
      zoom,
      controls,
    }))
})

const areaTypeStream$ = new Subject()
  .pipe(distinctUntilChanged())

areaTypeStream$.subscribe((val) => showPoligonsOnTypes(val))

function getAreaTypeStream() {
  return areaTypeStream$
}

function selectedAreaExist() {
  return selectedArea?.geometry.getCoordinates()[0].length > 1 ? true : false

}

function createHeatmapContainer(bounds, type, areaCoords, blobURL) {
  return new ymaps.Polygon(
    [[bounds[0],
      [bounds[1][0], bounds[0][1]],
      bounds[1],
      [bounds[0][0], bounds[1][1]],
    ]],
    {
      balloonContentHeader: "Area info",
      hintContent: "show information",
      sectorType: type,
      areaCoords,
    },
    {
      balloonContentLayout,
      fillImageHref: blobURL,
      fillMethod: 'stretch',
      opacity: 0.8,
    },
  )
}

async function addPolyToMap(polygon) {
  (await map).geoObjects.add(polygon)
  const polyType = polygon.properties.get("sectorType")
  if (polyType) {
    areaTypeStream$.next(polyType)
  }
}

async function addSelectedArea(polygon) {
  addPolyToMap(polygon)
  selectedArea = polygon
}

async function setBoundsForMap(bounds) {
  (await map).setBounds(bounds)
}

function SmallAreaСheck() {
  if (!selectedAreaExist()
    || selectedArea.geometry.getCoordinates()[0].length < 4) {
    throw new GeneralError("Area is too small or not selected")
  }
}

function getAreaInfo() {
  SmallAreaСheck()
  const transformArrayCoords = (coords) => {
    return coords.map(el => [el[0], el[1]])
  }
  const areaCoords = selectedArea.geometry.getCoordinates()[0]
  return {
    bounds: selectedArea.geometry.getBounds(),
    coords: transformArrayCoords(areaCoords)
  }

}

async function showPoligonsOnTypes(type) {
  (await map).geoObjects.each(gObj => {
    if (gObj.properties.get("sectorType") === undefined) {
      return
    }
    if (gObj.properties.get("sectorType") === type) {
      gObj.options.set('visible', true)
    } else {
      gObj.options.set('visible', false)
    }
  });

}

async function setSelectedAreaState(state) {

  switch (state) {
    case areaState.select:
      selectedArea = selectedArea ?? new ymaps.Polygon([], {}, {
        editorDrawingCursor: 'crosshair',
        strokeColor: '#0000ff',
        strokeWidth: 2,
      });
      (await map).geoObjects.add(selectedArea)
    case areaState.edit:
      selectedArea.editor.startDrawing();
      break;
    case areaState.delete:
      if (selectedArea === undefined || selectedArea === null) {
        return
      }
      selectedArea.editor.stopEditing();
      (await map).geoObjects.remove(selectedArea)
      selectedArea = null
      break;
    case areaState.complete:
      selectedArea?.editor.stopEditing();
      break;
  }
}

// set selected area state base on button click in sizebar
getStateStream().subscribe(({mode, state}) => {
  switch (mode) {
    case typeMode.editable:
      setSelectedAreaState(state)
      break;
    case typeMode.kml:
      if (state === areaState.delete) {
        setSelectedAreaState(areaState.delete)
      }
      break;
  }
})


function existSameArea(areaInfoWithType) {
  const isSame = (geoObj, comparedAreaInfo) => {
    const gProps = geoObj.properties
    if (comparedAreaInfo.type !== gProps.get('sectorType')) {
      return false
    }
    const gCoords = gProps.get('areaCoords')
    if (comparedAreaInfo.coords.length === gCoords?.length) {
      return JSON.stringify(comparedAreaInfo.coords) === JSON.stringify(gCoords)
    }
    return false;
  }
  return map.then(mapObj => {
    const geoObjectsIter = mapObj.geoObjects.getIterator();
    let geoObj = geoObjectsIter.getNext()
    while (Object.keys(geoObj).length !== 0) {
      const areasSames = isSame(geoObj, areaInfoWithType)
      if (areasSames) return true

      geoObj = geoObjectsIter.getNext()
    }
    return false
  })
}

function calculateAreaSelectedArea() {
  SmallAreaСheck()
  const areaSelectedArea = ymaps.util.calculateArea(selectedArea)
  return (areaSelectedArea / 1e6).toFixed(3);
}

export {
  getAreaInfo, addPolyToMap, addSelectedArea, setBoundsForMap,
  createHeatmapContainer, selectedAreaExist, getAreaTypeStream,
  calculateAreaSelectedArea, existSameArea
}