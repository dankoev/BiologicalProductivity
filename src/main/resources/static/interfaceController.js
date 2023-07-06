const leftSizebar = document.querySelector('#left-sizebar')
const messageType = {
  error: 'error',
  warning: 'warning'
}
const loadState = {
  show: 'show',
  hide: 'hide'
}
let mapController = undefined
let mapTemplates = {}

function cteateBallonTemplate() {
  mapTemplates.balloonContentLayout = ymaps.templateLayoutFactory.createClass(
    `<div>
      <p id="max"> Max: {{ properties.areaStatistics.maxSectorValue | default:"No date" }} </p>
      <p id="min"> Min: {{ properties.areaStatistics.minSectorValue | default:"No date" }} </p>
      <p id="average">Average: {{ properties.areaStatistics.averageSectorValue | default:"No date" }} </p>
      <button id="delete-heatmap"> Удалить </button>
    </div>`, {
    build: function () {
      mapTemplates.balloonContentLayout.superclass.build.call(this)
      document.querySelector('#delete-heatmap').onclick = this.delete.bind(this)
    },

    clear: function () {
      document.querySelector('#delete-heatmap').removeEventListener('click', this.delete)
      mapTemplates.balloonContentLayout.superclass.clear.call(this)
    },
    delete: function () {
      mapController.map.geoObjects.remove(this.getData().geoObject)
    },
  })
}

function createLayerController() {

  const layerController = document.querySelector('#layer-controller')
  const typeSelectBtn = layerController.querySelector('.type-title')
  const listTypes = layerController.querySelector('.list-types')
  typeSelectBtn.onclick = () => {
    listTypes.toggleAttribute("hidden")
  }

  addListTypes(layerController)

}
function setLayerType(type) {
  const typeList = document.querySelector('#layer-controller .list-types')
    .getElementsByTagName('input')
  for (const inputWithType of typeList) {
    if (inputWithType.id === type) {
      inputWithType.checked = true;
      break;
    }
  }


}
function addListTypes(layerController) {
  const actionSelectionType = function (e) {
    mapController.showOrHidePoligonsOnTypes(e.target.id)
  }
  const srcLabelsOfTypes = document.querySelector('#heatmap-types')
    .getElementsByTagName('label')
  const dstTypeList = layerController.querySelector('.list-types')

  const createTypeElement = (srcLabel) => {
    const dstInput = document.createElement('input')
    dstInput.id = srcLabel.control.value
    dstInput.type = 'radio'
    dstInput.name = 'heatmap-type'
    dstInput.addEventListener('click', actionSelectionType)

    const dstLabel = document.createElement('label')
    dstLabel.innerText = srcLabel.outerText
    dstLabel.htmlFor = dstInput.id
    const container = document.createElement('div')
    container.appendChild(dstInput)
    container.appendChild(dstLabel)
    return container
  }


  for (const labelType of srcLabelsOfTypes) {
    const typeElement = createTypeElement(labelType)
    dstTypeList.appendChild(typeElement)
  }

}
function controlAreaStates(mapController) {
  const addCompleteBtn = (targetBtn, targetAction) => {
    targetBtn.disabled = true
    const completeBtn = document.createElement('button')
    completeBtn.textContent = 'Завершить '
    completeBtn.setAttribute('class', 'complete-btn')
    completeBtn.onclick = () => {
      targetBtn.disabled = false
      targetAction()
      mapController.setSelectState('complete')
      completeBtn.remove()
    }
    leftSizebar.querySelector('#control-box .optional-btns')
      .appendChild(completeBtn)

  }
  const createBtnWithAct = (text, className, action, ...controlledBtns) => {
    const btn = document.createElement('button')
    btn.setAttribute('class', className)
    btn.textContent = text
    btn.onclick = function () {
      action(this, ...controlledBtns)
    }
    return btn
  }

  const actionDelBtn = (dBtn, eBtn, sBtn) => {
    const completeBtn = leftSizebar.querySelector('#control-box .complete-btn')
    if (completeBtn != null) {
      completeBtn.remove()
    }
    eBtn.remove()
    sBtn.hidden = false
    mapController.setSelectState('delete')
    dBtn.remove()
  }

  const actionEditBtn = (eBtn) => {
    mapController.setSelectState('edit')
    addCompleteBtn(eBtn, () => { })
  }

  const actionSelectBtn = (sBtn) => {
    mapController.setSelectState('select')
    addCompleteBtn(sBtn, () => {
      if (!mapController.selectedAreaExist()) {
        return
      }
      sBtn.hidden = true
      const editBtn = createBtnWithAct("Редактировать", 'edit-btn', actionEditBtn)
      const deleteBtn = createBtnWithAct("Удалить", 'delete-btn', actionDelBtn, editBtn, sBtn)
      leftSizebar.querySelector('#control-box .main-btns')
        .appendChild(editBtn)
      leftSizebar.querySelector('#control-box .main-btns')
        .appendChild(deleteBtn)
    })
  }

  const selectBtn = createBtnWithAct("Выбрать", 'select-btn', actionSelectBtn)
  leftSizebar.querySelector('#control-box .main-btns')
    .appendChild(selectBtn)
}

function createArrow() {
  leftSizebar.querySelector(".down-arrow")
    .onclick = () => {
      leftSizebar.querySelector(".sizebar-content")
        .classList
        .toggle("collapsed")

      leftSizebar.querySelector(".down-arrow")
        .classList
        .toggle("img-flip")
    }
}

function showMessage(messageCont, messageType) {
  const messageDiv = document.createElement('div')
  const messageWindow = document.querySelector('#message-window')
  const message = document.createElement('p')

  const messageContent = messageCont.toString()
  if (messageType === 'warning') {
    messageDiv.style.cssText = 'background-color:  #e7c608'
  }
  if (messageType === 'error') {
    messageDiv.style.cssText = 'background-color: red'
  }
  if (messageContent.length > 120) {
    message.textContent = "Too much error content. Check console"
    console.log(messageContent)
  } else {
    message.textContent = messageContent
  }
  messageDiv.appendChild(message)
  messageWindow.appendChild(messageDiv)
  const showTime = 4000
  setTimeout(() => messageDiv.classList.add('smooth-hide'), showTime)
  setTimeout(() => messageDiv.remove(), showTime + 4000)

}
function setLoadState(loadState) {
  const loadItem = document.querySelector('#load-item')
  if (loadState === 'show') {
    loadItem.style.display = 'inline'
  }
  if (loadState === 'hide') {
    loadItem.style.display = 'none'
  }

}
function init() {
  createArrow()
  createLayerController()
  mapController = new YMapController('map', [107.88, 54.99])
  cteateBallonTemplate()
  controlAreaStates(mapController)
  kmlLoadInit()

}
ymaps.ready(init)
