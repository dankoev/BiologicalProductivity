import {modalWindowContainer} from "./modalWindowContainer";
import {showMessage} from "./messageBar";
import {GeneralError,} from "./share";
import "./css/kmlWindow.css"
import {addSelectedArea, setBoundsForMap,} from "./ymapsControl";


const kmlTemplate =
  `<div class="kml-areas">
        <input class="kml-areas__search" placeholder="Поиск" name="Поиск области">
        <ul class="kml-areas__list"></ul>
      </div>
      <template id="kml-li__template">
        <li><label class="kml-li__item"><input type="button" hidden></label></li>
      </template>`

const pathToKmlAreasJSON = "/data/kmlAreas.json"

let loadAreaEvent = () => {
}

let startLoadAreaEvent = () => {
  console.log('start load kml')
}

const modalWindow = (() => {
  const modalSection = document.createElement("section")
  modalSection.classList.add("kml-window")
  modalSection.innerHTML = kmlTemplate.trim()
  return modalSection
})()

const searchField = modalWindow.querySelector('.kml-areas__search')

function closeKmlWindow() {
  modalWindowContainer
    .close()
}

function checkGeoObjsContent(res) {
  let polygonCount = 0
  let onlyPolygon;
  res.geoObjects.each((gObj) => {
    if (gObj.geometry.getType() === "Polygon") {
      polygonCount += 1
      onlyPolygon = gObj
    }
  })
  if (polygonCount !== 1) {
    throw new Error("Error load KML file. Number polygon > 1")
  }
  return onlyPolygon;
}

async function onGeoXmlLoad(res) {
  const poly = checkGeoObjsContent(res)
  await addSelectedArea(poly)
  setBoundsForMap(poly.geometry.getBounds())
  loadAreaEvent()
}

function createElemAreaList(name, link) {
  const kmlLi = modalWindow
    .querySelector('#kml-li__template')
    .content
    .firstElementChild
    .cloneNode(true)
  kmlLi.querySelector("label").append(name)
  const kmlLiInput = kmlLi.querySelector("input")
  kmlLiInput.name = link
  kmlLiInput.onclick = () => {
    closeKmlWindow()
    startLoadAreaEvent()
    ymaps.geoXml
      .load(link)
      .then(res => onGeoXmlLoad(res))
      .catch(err => showMessage(err))
  }
  return kmlLi
}

async function createListKmlAreas() {
  return fetch(pathToKmlAreasJSON, {
    method: 'GET'
  })
    .then(data => data.json())
    .then(kmlAreas =>
      kmlAreas.map((area) => {
        const {name, link} = area
        return createElemAreaList(name, link)
      })
    )
    .catch(err => {
      console.log(err)
      throw new GeneralError("Error loading info's about kml areas")
    })
}

function listKmlMemoizer(func) {
  let cache = undefined
  return async () => {
    if (cache !== undefined) {
      console.log("return list from cache", cache)
      return cache
    } else {
      const result = await func()
      cache = result
      console.log("Add list to cache", cache)
      return result
    }
  }
}

const listKmlMemo = listKmlMemoizer(createListKmlAreas)

function openKmlWindow() {
  const kmlAreasList = modalWindow.querySelector('ul.kml-areas__list')
  if (!kmlAreasList.querySelector(".kml-li__item")) {
    listKmlMemo()
      .then(listElems => {
        listElems.forEach(elem => kmlAreasList.append(elem))
      })
  }
  modalWindowContainer
    .openWithContent(modalWindow, "Список kml файлов")

  return {
    setLoadedAreaEvent: (callback) => {
      loadAreaEvent = callback
    },
    setStartLoadAreaEvent: (callback) => {
      startLoadAreaEvent = callback
    }
  }
}

searchField.oninput = (e) => {
  const findedSymbs = e.target.value
  listKmlMemo()
    .then(list => {
      list
        .map(li => {
          const matched = li
            .querySelector('label')
            .textContent
            .includes(findedSymbs)
          return {li, matched}
        })
        .forEach(({li, matched}) => li.hidden = !matched)
    })

}

export {openKmlWindow}
