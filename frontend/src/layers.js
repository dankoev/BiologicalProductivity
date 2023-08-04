import "./css/layers.css"
import {getListTypes} from "./sizebar"
import {getAreaTypeStream, showPoligonsOnTypes} from "./ymapsControl"

const layers = document.querySelector(".layers")
const typeSelectBtn = layers.querySelector('.layers__title')
const listTypes = layers.querySelector('.layers__list')

// click action on layer type
const actionSelectionType = (e) => {
  showPoligonsOnTypes(e.target.id)
}

// click action on "info" icon
const actionShowMapInfo = (e) => {
  // this._showMapInfo(e.target.parentElement.querySelector('input').id)
}

// layer element creation function
const createTypeElement = (srcLabel) => {
  const dstInput = document.createElement('input')
  dstInput.id = srcLabel.control.value
  dstInput.type = 'radio'
  dstInput.name = 'heatmap-type'
  dstInput.addEventListener('click', actionSelectionType)

  const dstLabel = document.createElement('label')
  dstLabel.innerText = srcLabel.outerText
  dstLabel.htmlFor = dstInput.id

  const mapInfo = document.createElement('span')
  mapInfo.textContent = " info"
  mapInfo.className = "map-info"
  mapInfo.addEventListener('click', actionShowMapInfo)

  const container = document.createElement('div')
  container.append(dstInput)
  container.append(dstLabel)
  container.append(mapInfo)
  return container
}

// list of layers creation function
function createListTypes() {
  const srcLabelsOfTypes = getListTypes();

  for (const labelType of srcLabelsOfTypes) {
    const typeElement = createTypeElement(labelType)
    listTypes.append(typeElement)
  }
}

createListTypes()


// show/hide list layers
typeSelectBtn.onclick = () => {
  listTypes.toggleAttribute("hidden")
}

// change cheacked layer
getAreaTypeStream().subscribe((type) => {
  listTypes
    .querySelector(`input[id=${type}]`)
    .checked = true
})