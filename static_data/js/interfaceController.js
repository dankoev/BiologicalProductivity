

const intefaceController = {
  layerController: {
    _target: document.querySelector('#layer-controller')
  },
  areaController: {
    _target: document.querySelector('#left-sizebar'),
    _optionBar: document.querySelector('#option-select-bar'),
    _mapController: {},
    _kmlController: kmlController
  },
  messageController: {
    _target: document.querySelector('#message-window')
  }
}

let mapController = undefined
let mapTemplates = {}

function cteateBallonTemplate(mapController) {
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

// LayerController BEGINNING init
intefaceController
  .layerController
  .fillLayerController = function (sizebarTarget, mapController) {

    const typeSelectBtn = this._target.querySelector('.type-title')
    const listTypes = this._target.querySelector('.list-types')

    typeSelectBtn.onclick = () => {
      listTypes.toggleAttribute("hidden")
    }

    const getListTypesFromSizebar = () => {
      return sizebarTarget.querySelector('#heatmap-types')
        .getElementsByTagName('label')
    }

    const CreateListTypesFromSizebar = (() => {
      const actionSelectionType = function (e) {
        mapController.showOrHidePoligonsOnTypes(e.target.id)
      }
      const srcLabelsOfTypes = getListTypesFromSizebar();
      const dstTypeList = this._target.querySelector('.list-types')

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
        container.append(dstInput)
        container.append(dstLabel)
        return container
      }

      for (const labelType of srcLabelsOfTypes) {
        const typeElement = createTypeElement(labelType)
        dstTypeList.append(typeElement)
      }
    })()

  }

intefaceController
  .layerController
  .setLayerType = function (type) {
    const typeList = this._target.querySelector('.list-types')
      .getElementsByTagName('input')
    for (const inputWithType of typeList) {
      if (inputWithType.id === type) {
        inputWithType.checked = true;
        break;
      }
    }

  }

// LayerController END init

// AreaController BEGINNING init
intefaceController
  .areaController
  ._createBtnWithAct = function (text, className, action) {
    const btn = document.createElement('button')
    btn.setAttribute('class', className)
    btn.textContent = text
    btn.onclick = (e) => action(e)
    return btn
  }

intefaceController
  .areaController
  ._addCompleteBtn = function (controlledBtn, targetAction) {
    controlledBtn.disabled = true
    const actionCompliteBtn = (e) => {
      controlledBtn.disabled = false
      targetAction()
      this._mapController.setSelectedAreaState(selectedAreaStates.complete)
      e.target.remove()
    }
    this._completeBtn = this._createBtnWithAct('Завершить', 'complete-btn', actionCompliteBtn)

    this._target.querySelector('#control-box .optional-btns')
      .append(this._completeBtn)
  }

intefaceController
  .areaController
  .setAreaMode = function (areaMode) {
    const selectedOption = this._optionBar
      .querySelector("input.selected")
    if (selectedOption != null && selectedOption.id === areaMode)
      return;
    this._selectBtn.hidden = false
    this._selectBtn.disabled = false
    selectedOption != null ? selectedOption.classList.remove("selected") : ({});
    this._mapController.setSelectedAreaState(selectedAreaStates.delete)
    // this._mapController.
    switch (areaMode) {
      case "editable-mode":

        this._selectBtn.textContent = "Выбрать"
        this._optionBar
          .querySelector("#editable-mode")
          .classList.add('selected')
        this._enableActionsEditableArea()
        break;
      case "kml-mode":
        this._selectBtn.textContent = "Выбрать KML"
        this._optionBar
          .querySelector("#kml-mode")
          .classList.add('selected')
        this._enableActionsKmlArea()
        break;
      default:
        break;
    }
  }
intefaceController
  .areaController
  ._deleteAllOptionalBtns = function () {
    if (this._deleteBtn != null) { this._deleteBtn.remove() }
    if (this._completeBtn != null) { this._completeBtn.remove() }
    if (this._editBtn != null) { this._editBtn.remove() }
  }
intefaceController
  .areaController
  ._enableActionsEditableArea = function () {
    this._deleteAllOptionalBtns()
    const actionDelBtn = () => {
      if (this._completeBtn != null) {
        this._completeBtn.remove()
      }
      this._editBtn.remove()
      this._selectBtn.hidden = false
      this._mapController.setSelectedAreaState(selectedAreaStates.delete)
      this._deleteBtn.remove()
    }

    const actionEditBtn = (e) => {
      this._mapController.setSelectedAreaState(selectedAreaStates.edit)
      this._addCompleteBtn(e.target, () => { })
    }

    const actionSelectBtn = (e) => {
      this._mapController.setSelectedAreaState(selectedAreaStates.select)
      this._addCompleteBtn(e.target, () => {
        if (!this._mapController.selectedAreaExist()) {
          return
        }
        e.target.hidden = true
        this._editBtn = this._createBtnWithAct("Редактировать", 'edit-btn', actionEditBtn)
        this._deleteBtn = this._createBtnWithAct("Удалить", 'delete-btn', actionDelBtn)
        this._target.querySelector('#control-box .main-btns')
          .append(this._editBtn)
        this._target.querySelector('#control-box .main-btns')
          .append(this._deleteBtn)
      })
    }

    this._selectBtn.onclick = actionSelectBtn
  }

intefaceController
  .areaController
  ._enableActionsKmlArea = function () {
    this._deleteAllOptionalBtns()
    const actionDelBtn = () => {
      this._selectBtn.disabled = false
      this._mapController.setSelectedAreaState(selectedAreaStates.delete)
      this._deleteBtn.remove()
    }

    const actionSelectBtn = (e) => {
      this._kmlController.modalWindow.setCloseEvent(() => {
        e.target.disabled = true
        this.setLoadState(loadState.show)

      })
      this._kmlController.modalWindow.open((area) => {
        this._mapController.addObject(area);
        this._mapController.setBoundsForMap(area.geometry.getBounds());
        this._mapController.setSelectedArea(area)

        this._deleteBtn = this._createBtnWithAct("Удалить", 'delete-btn', actionDelBtn)
        this._target.querySelector('#control-box .main-btns')
          .append(this._deleteBtn)
        this.setLoadState(loadState.hide)
      })
    }

    this._selectBtn.onclick = actionSelectBtn
  }

intefaceController
  .areaController
  ._createArrow = function () {
    this._target
      .querySelector(".down-arrow")
      .onclick = () => {
        this._target.querySelector(".sizebar-content")
          .classList
          .toggle("collapsed")

        this._target.querySelector(".down-arrow")
          .classList
          .toggle("img-flip")
      }
  }

intefaceController
  .areaController
  .controlAreaStates = function (mapController) {
    this._mapController = mapController

    const optionInputs = this._optionBar.getElementsByTagName('input')
    for (const input of optionInputs) {
      input.onclick = (e) => this.setAreaMode(e.target.id)
    }
    this._selectBtn = this._createBtnWithAct("Выбрать", 'select-btn', () => { })
    this.setAreaMode(areaMode.editableMode)

    this._target.querySelector('#control-box .main-btns')
      .append(this._selectBtn)

    this._createArrow()
  }
intefaceController
  .areaController
  .setLoadState = function (loadState) {
    const loadItem = document.querySelector('#load-item')
    if (loadState === 'show') {
      loadItem.style.display = 'inline'
    }
    if (loadState === 'hide') {
      loadItem.style.display = 'none'
    }

  }
// AreaController END init

// MessageController BEGINNING init
intefaceController
  .messageController
  .showMessage = function (err) {
    const messageDiv = document.createElement('div')
    const message = document.createElement('p')

    switch (err.constructor) {
      case GeneralWarning:
        messageDiv.style.cssText = 'background-color: #e7c608;'
        break;
      case GeneralError:
      case Error:
        messageDiv.style.cssText = 'background-color: red;'
        break;

      default:
        messageDiv.style.cssText = 'background-color: #e8e9a4;'
        break;
    }

    if (err.message.length > 120) {
      message.textContent = "Too much error content. Check console"
      console.log(err.message)
    } else {
      message.textContent = err.message
    }

    const showTime = 4000
    let removeTimeout;
    const hideAndDelAction = () => {
      setTimeout(() => messageDiv.classList.add('smooth-hide'), showTime)
      removeTimeout = setTimeout(() => messageDiv.remove(), showTime + 4000)
    }

    messageDiv.addEventListener('mouseover', (e) => {
      e.target.classList.remove('smooth-hide')
      clearTimeout(removeTimeout)
    })
    messageDiv.addEventListener('mouseout', (e) => {
      hideAndDelAction()
    })

    hideAndDelAction()
    messageDiv.append(message)
    this._target.append(messageDiv)
  }

// MessageController END init 

function init() {
  mapController = new YMapController('map', [107.88, 54.99], ymaps)
  intefaceController
    .layerController
    .fillLayerController(document.querySelector('#left-sizebar'), mapController)
  intefaceController
    .areaController
    .controlAreaStates(mapController)
  cteateBallonTemplate(mapController)
  intefaceController
    .areaController
    ._kmlController
    .init(intefaceController)
}
ymaps.ready({
  require: ['util.calculateArea'],
  successCallback: init
})
