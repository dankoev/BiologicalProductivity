import {Subject, map} from "rxjs";
import "./css/sizebar.css"
import {areaState, getAreaInfo, selectedAreaExist, setSelectedAreaState} from "./ymapsControl";
import {getHeatmapFromServer} from "./serverLoad";
import {showMessage} from "./messageBar";

const sizebar = document.querySelector(".sizebar")
const deleteBtn = sizebar.querySelector('.sizebar__control .delete')
const selectBtn = sizebar.querySelector('.sizebar__control .select')
const editBtn = sizebar.querySelector('.sizebar__control .edit')
const completeBtn = sizebar.querySelector('.sizebar__control .complete')
const calculateBtn = sizebar.querySelector('#calculatePolygon')
const loadItem = sizebar.querySelector(".load-item")

// hide/ show sizebar
sizebar
  .querySelector('.down-arrow')
  .addEventListener('click', (e) => {
    sizebar
      .querySelector('.sizebar__content')
      .classList
      .toggle("collapsed")
    e.target
      .classList
      .toggle("active")
  })

// Area control logic
const typeMode = {
  editable: 'editable',
  kml: 'kml'
}

function show(...bnts) {
  bnts.forEach(bnt => {
    bnt.disabled = false
    bnt.style.display = "block"
  })
}

function hide(...bnts) {
  bnts.forEach(bnt => {
    bnt.disabled = false
    bnt.style.display = "none"
  })
}

function resetToInitial() {
  hide(editBtn, deleteBtn, completeBtn)
  show(selectBtn)
}

export class Mode {
  static currentMode = typeMode.editable

  static enableKmlMode() {
    deleteBtn.click()
    selectBtn.textContent = "Выбрать kml"
    Mode.currentMode = typeMode.kml
    return typeMode.kml
  }

  static enableEditableMode() {
    deleteBtn.click()
    selectBtn.textContent = "Выбрать область"
    Mode.currentMode = typeMode.editable
    return typeMode.editable
  }

  static get currentMode() {
    return currentMode
  }
}

const statesStream$ = new Subject()
  .pipe(
    map(state => {
      return {
        mode: Mode.currentMode,
        state,
      }
    })
  )

const editableModeControl = val => {
  setSelectedAreaState(val)
  switch (val) {
    case areaState.select:
    case areaState.edit:
      show(completeBtn)
      break;
    case areaState.complete:
      if (selectedAreaExist()) {
        show(editBtn, deleteBtn)
        hide(selectBtn, completeBtn)
      } else {
        resetToInitial()
      }
      break;
    case areaState.delete:
      resetToInitial()
      break;
    default:
      break;
  }
}
const kmlModeControl = val => {
  switch (val) {
    case areaState.select:
      // await loadKMl()
      show(deleteBtn)
      break;
    case areaState.delete:
      setSelectedAreaState(val)
      hide(deleteBtn)
      show(selectBtn)
      break;
    default:
      break;
  }
}
const modeSwitch = ({mode, state}) => {
  console.log(mode, state)
  switch (mode) {
    case typeMode.editable:
      editableModeControl(state)
      break;
    case typeMode.kml:
      kmlModeControl(state)
      break;
    default:
      break;
  }
}
const editableModeListener = statesStream$.subscribe(modeSwitch)


selectBtn
  .addEventListener('click', (e) => {
    e.target.disabled = true
    statesStream$
      .next(areaState.select)
  })


completeBtn
  .addEventListener('click', (e) => {
    e.target.disabled = true
    statesStream$
      .next(areaState.complete)
  })

editBtn
  .addEventListener('click', (e) => {
    e.target.disabled = true
    statesStream$
      .next(areaState.edit)
  })

deleteBtn
  .addEventListener('click', (e) => {
    e.target.disabled = true
    statesStream$
      .next(areaState.delete)
  })

// show/hide load icon
function showLoadIcon() {
  loadItem.classList.add('active')
}

function hideLoadIcon() {
  loadItem.classList.remove('active')
}

// calculate heatmap control logic 
async function calculateAction(e) {
  e.target.disabled = true
  try {
    const areaInfo = getAreaInfo()
    const mapType = sizebar
      .querySelector(".sizebar__types input[name='choiceMap']:checked")
      .value
    showLoadIcon()
    await getHeatmapFromServer({
      ...areaInfo,
      type: mapType
    })
      .then(heatmap => heatmap.createPolyFromHeatMap())
      .then(heatmapPoly => heatmapPoly.addToMapWithStatistic())
      .catch(err => showMessage(err))
    hideLoadIcon()
  } catch (err) {
    showMessage(err)
  } finally {
    e.target.disabled = false
  }
}

calculateBtn
  .addEventListener('click', (e) => {
    calculateAction(e)
  })