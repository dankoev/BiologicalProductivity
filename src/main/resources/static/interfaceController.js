const leftSizebar = document.querySelector('#left-sizebar')
const messageType = {
  error:'error',
  warning: 'warning'
}
const loadState = {
  show: 'show',
  hide: 'hide'
}
let mapController = undefined
let mapTemplates = {}

function cteateBallonTemplate(){
  mapTemplates.balloonContentLayout = ymaps.templateLayoutFactory.createClass(
    `<div>
      <p id="max"> Max: {{ properties.maxAreaValue|default:"No date" }} </p>
      <p id="min"> Min: {{ properties.minAreaValue|default:"No date" }} </p>
      <p id="average">Average: {{ properties.averageAreaValue|default:"No date" }} </p>
      <button id="delete"> Удалить </button>
    </div>`, {
    build: function () {
        mapTemplates.balloonContentLayout.superclass.build.call(this)
        document.querySelector('#delete').onclick = this.delete      
    },

    clear: function () {

      document.querySelector('#delete').removeEventListener('click',this.delete)
      mapTemplates.balloonContentLayout.superclass.clear.call(this)
    },
    delete: () =>{
      console.log(this)
    },
  })
}


function controlAreaStates(mapController){
  const addCompleteBtn = (targetBtn, targetAction) => {
    targetBtn.disabled = true
    const completeBtn = document.createElement('button')
    completeBtn.textContent = 'Завершить '
    completeBtn.setAttribute('class','complete-btn')
    completeBtn.onclick = () => {
      targetBtn.disabled = false
      targetAction()
      mapController.setSelectState('complete')
      completeBtn.remove()
    }
    leftSizebar.querySelector('#area-btns .optional-btns')
                .appendChild(completeBtn)

  }
  const createBtnWithAct = (text, className, action, ...args) => {
    const btn = document.createElement('button')
    btn.setAttribute('class', className)
    btn.textContent = text
    btn.onclick = function () {
      action(this,...args)
    }
    return btn
  }

  const actionDelBtn = (dBtn, eBtn, sBtn) => {
    eBtn.remove()
    sBtn.hidden = false
    mapController.setSelectState('delete')
    dBtn.remove()
  }

  const actionEditBtn = (eBtn) =>  {
    mapController.setSelectState('edit')
    addCompleteBtn(eBtn,()=>{})
  }

  const actionSelectBtn = (sBtn) => {
    mapController.setSelectState('select')
    addCompleteBtn(sBtn, ()=> {
      if(!mapController.selectedAreaExist()){
        return
      }
      sBtn.hidden = true
      const editBtn = createBtnWithAct("Редактировать",'edit-btn', actionEditBtn)
      const deleteBtn = createBtnWithAct("Удалить", 'delete-btn', actionDelBtn, editBtn, sBtn )
      leftSizebar.querySelector('#area-btns .main-btns')
                  .appendChild(editBtn) 
      leftSizebar.querySelector('#area-btns .main-btns')
                  .appendChild(deleteBtn) 
    })
  }

  const selectBtn = createBtnWithAct("Выбрать", 'select-btn', actionSelectBtn)
  leftSizebar.querySelector('#area-btns .main-btns')
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

function showMessage(messageCont, messageType){
  const messageDiv = document.createElement('div')
  const messageWindow = document.querySelector('#message-window')
  const message = document.createElement('p')

  const messageContent = messageCont.toString()
  if (messageType === 'warning'){
    messageDiv.style.cssText = 'background-color:  #e7c608'
  }
  if (messageType === 'error'){
    messageDiv.style.cssText = 'background-color: red'
  }
  if (messageContent.length > 120){
    message.textContent = "Too much error content. Check console"
    console.log(messageContent)
  } else {
    message.textContent = messageContent
  }
  messageDiv.appendChild(message)
  messageWindow.appendChild(messageDiv)
  const showTime = 4000
  setTimeout(()=>messageDiv.classList.add('smooth-hide'), showTime)
  setTimeout(()=>messageDiv.remove(), showTime + 4000)
  
}
function setLoadState(loadState){
  const loadItem = document.querySelector('#load-item')
  if (loadState === 'show'){
    console.log('here')
    loadItem.style.display = 'inline'
  }
  if (loadState === 'hide'){
    loadItem.style.display = 'none'
  }
  
}
function init() {
  mapController = new YMapController('map', [107.88, 54.99])
  cteateBallonTemplate()
  controlAreaStates(mapController)

}

createArrow()
ymaps.ready(init)
