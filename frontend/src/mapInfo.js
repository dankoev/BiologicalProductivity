import "./css/mapInfo.css"
import {getMapsInfo} from "./mapsInfoLoader";
import {modalWindowContainer} from "./modalWindowContainer";

const mapInfoContainer = document.createElement('section')
mapInfoContainer.classList.add("map-info-container")

export function showMapInfo(type) {
  getMapsInfo(type)
    .then(info => {
      mapInfoContainer.innerHTML = info.description
      modalWindowContainer.openWithContent(mapInfoContainer, `Информация о карте ${type}`)
    })
}