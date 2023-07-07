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

const intefaceController = {
  layerController: {
    target: document.querySelector('#layer-controller')
  },
  oprionController: {
    target: document.querySelector('#option-select-bar')
  },
  areaController: {
    target: document.querySelector('#left-sizebar')
  },
  messageController: {
    target: document.querySelector('#message-window')
  }
}


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

intefaceController
  .layerController
  .fillLayerController = function (sizebarTarget, mapController) {

    const typeSelectBtn = this.target.querySelector('.type-title')
    const listTypes = this.target.querySelector('.list-types')

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
      const dstTypeList = this.target.querySelector('.list-types')

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
    })()

  }

intefaceController
  .layerController
  .setLayerType = function (type) {
    const typeList = this.target.querySelector('.list-types')
      .getElementsByTagName('input')
    for (const inputWithType of typeList) {
      if (inputWithType.id === type) {
        inputWithType.checked = true;
        break;
      }
    }

  }
intefaceController
  .areaController
  .controlAreaStates = function (mapController) {
    const createBtnWithAct = (text, className, action) => {
      const btn = document.createElement('button')
      btn.setAttribute('class', className)
      btn.textContent = text
      btn.onclick = function () {
        action(btn)
      }
      return btn
    }

    const addCompleteBtn = (controlledBtn, targetAction) => {
      controlledBtn.disabled = true
      const actionCompliteBtn = (completeBtn) => {
        controlledBtn.disabled = false
        targetAction()
        mapController.setSelectState('complete')
        completeBtn.remove()
      }
      this.completeBtn = createBtnWithAct('Завершить', 'completeBtn', actionCompliteBtn)

      this.target.querySelector('#control-box .optional-btns')
        .appendChild(this.completeBtn)

    }

    const actionDelBtn = (delBtn) => {
      if (this.completeBtn != null) {
        this.completeBtn.remove()
      }
      this.editBtn.remove()
      this.selectBtn.hidden = false
      mapController.setSelectState('delete')
      this.deleteBtn.remove()
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
        this.editBtn = createBtnWithAct("Редактировать", 'edit-btn', actionEditBtn)
        this.deleteBtn = createBtnWithAct("Удалить", 'delete-btn', actionDelBtn)
        this.target.querySelector('#control-box .main-btns')
          .appendChild(this.editBtn)
        this.target.querySelector('#control-box .main-btns')
          .appendChild(this.deleteBtn)
      })
    }

    this.selectBtn = createBtnWithAct("Выбрать", 'select-btn', actionSelectBtn)
    this.target.querySelector('#control-box .main-btns')
      .appendChild(this.selectBtn)
  }

intefaceController
  .areaController
  .createArrow = function () {
    this.target
      .querySelector(".down-arrow")
      .onclick = () => {
        this.target.querySelector(".sizebar-content")
          .classList
          .toggle("collapsed")

        this.target.querySelector(".down-arrow")
          .classList
          .toggle("img-flip")
      }
  }
intefaceController
  .messageController
  .showMessage = function (messageCont, messageType) {
    const messageDiv = document.createElement('div')
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
    messageDiv.appendChild(message)
    this.target.appendChild(messageDiv)
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
  mapController = new YMapController('map', [107.88, 54.99])
  intefaceController
    .areaController
    .createArrow()
  intefaceController
    .layerController
    .fillLayerController(document.querySelector('#left-sizebar'), mapController)
  intefaceController
    .areaController
    .controlAreaStates(mapController)
  cteateBallonTemplate()
  kmlLoadInit()

}
ymaps.ready(init)
