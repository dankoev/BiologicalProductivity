import ymaps from "ymaps"
import { GeneralError } from "./share"
import { Subject, distinctUntilChanged } from "rxjs"

const utils = ['util.calculateArea']
const controls = ['typeSelector']
const ymapID = 'map'
const zoom = 5
const centerCoords = [107.88, 54.99]

let selectedArea

export const areaState = {
  select: 'select',
  edit: 'edit',
  delete: 'delete',
  complete: 'complete',
}
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

export function getAreaTypeStream() {
  return areaTypeStream$
}

export function selectedAreaExist() {
  return selectedArea?.geometry.getCoordinates()[0].length > 1 ? true : false

}

export function createHeatmapContainer(bounds, type, areaCoords, blobURL) {
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

export async function addPolyToMap(polygon) {
  (await map).geoObjects.add(polygon)
  areaTypeStream$.next(polygon.properties.get("sectorType"))
}

function SmallAreaСheck() {
  if (!selectedAreaExist()
    || selectedArea.geometry.getCoordinates()[0].length < 4) {
    throw new GeneralError("Area is too small or not selected")
  }
}

export function getAreaInfo() {
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

export async function showPoligonsOnTypes(type) {
  (await map).geoObjects.each(gObj => {
    if (gObj.properties.get("sectorType") == undefined) {
      return
    }
    if (gObj.properties.get("sectorType") === type) {
      gObj.options.set('visible', true)
    } else {
      gObj.options.set('visible', false)
    }
  });

}
export async function setSelectedAreaState(state) {

  switch (state) {
    case 'select':
      selectedArea = selectedArea ?? new ymaps.Polygon([], {}, {
        editorDrawingCursor: 'crosshair',
        strokeColor: '#0000ff',
        strokeWidth: 2,
      });
      (await map).geoObjects.add(selectedArea)
    case 'edit':
      selectedArea.editor.startDrawing();
      break;
    case 'delete':
      if (selectedArea === undefined || selectedArea === null) {
        return
      }
      selectedArea.editor.stopEditing();
      (await map).geoObjects.remove(selectedArea)
      selectedArea = null
      break;
    case 'complete':
      selectedArea?.editor.stopEditing();
      break;
  }
}