import "./css/heatmapscale.css"
import {getAreaTypeStream} from "./ymapsControl";
import {areaState} from "./share";
import {getMapsInfo} from "./mapsInfoLoader";

const heatmapScale = document.querySelector('#heatmap-scale')

function setSignature(max, min) {
  const signatureList = heatmapScale.querySelectorAll('.signature b')
  if (typeof max === 'number' && typeof min === 'number') {
    signatureList[0].textContent = max
    signatureList[1].textContent = (max - min) / 2
    signatureList[2].textContent = min
  } else {
    setNullSignature()
  }
}

function setNullSignature() {
  const NOT_SPECIFIED = "..."
  const signatureList = heatmapScale.querySelectorAll('.signature b')
  signatureList[0].textContent = NOT_SPECIFIED
  signatureList[1].textContent = NOT_SPECIFIED
  signatureList[2].textContent = NOT_SPECIFIED
}

getAreaTypeStream().subscribe((areaType) => {
  getMapsInfo(areaType)
    .then(info => {
      setSignature(info?.maxValue, info?.minValue)
    })
})