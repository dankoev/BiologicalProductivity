import "./css/modeBar.css"
import {Mode} from "./sizebar"

const editableMode = document.querySelector("#editable-mode")
const kmlMode = document.querySelector("#kml-mode")

editableMode.onclick = () => {
  editableMode.classList.add('active')
  kmlMode.classList.remove('active')
  Mode.enableEditableMode()
}
kmlMode.onclick = () => {
  kmlMode.classList.add('active')
  editableMode.classList.remove('active')
  Mode.enableKmlMode()
}