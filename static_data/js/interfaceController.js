import {YMapController} from "./map.js";
import {modalWindowContainer} from "./modalWindowContainer.js";
import {kmlController} from "./kmlController.js";

import {showUpdateMapsInfoFromHTML} from "./loadMapsInfo.js";
import {createAndShowArea} from "./sectorController.js";

export const intefaceController = {
	heatmapScale: {
		_target: document.querySelector('#heatmap-scale'),
	},
	layerController: {
		_target: document.querySelector('#layers'),
		_mapsInfos: [],
		_modalContainer: modalWindowContainer,
	},
	areaController: {
		_target: document.querySelector('.sizebar'),
		_optionBar: document.querySelector('#option-select-bar'),
		_mapController: {},
		_kmlController: kmlController,
	},
	messageController: {
		_target: document.querySelector('#message-window')
	},
	sectorController: {
		_controlledBnt: document.querySelector('#calculatePolygon'),
		_leftSizebar: document.querySelector('.sizebar'),
	}

}

// HeatMapScale BEGINNING init
intefaceController
	.heatmapScale
	.setSignature = function (max, min) {
	const signatureList = this._target.querySelectorAll('.signature b')
	if (typeof max === 'number' && typeof min === 'number') {
		signatureList[0].textContent = max
		signatureList[1].textContent = (max - min) / 2
		signatureList[2].textContent = min
	} else {
		this.setNullSignature()
	}
}
intefaceController
	.heatmapScale
	.setNullSignature = function () {
	const NOT_SPECIFIED = "..."
	const signatureList = this._target.querySelectorAll('.signature b')
	signatureList[0].textContent = NOT_SPECIFIED
	signatureList[1].textContent = NOT_SPECIFIED
	signatureList[2].textContent = NOT_SPECIFIED
}

// HeatMapScale END init

// LayerController BEGINNING init
intefaceController
	.layerController
	._heatmapScale = intefaceController.heatmapScale
intefaceController
	.layerController
	._changeScaleSignature = function (setedType) {
	const findedMapInfo = this._mapsInfos
		.find(info => info.type === setedType)
	if (findedMapInfo != null || findedMapInfo !== undefined) {
		const {maxValue, minValue} = findedMapInfo;
		this._heatmapScale.setSignature(maxValue, minValue)
	} else {
		this._heatmapScale.setNullSignature()
	}

}

intefaceController
	.layerController
	._showMapInfo = function (mapType) {
	const findedMapInfo = this._mapsInfos
		.find(info => info.type === mapType)
	if (findedMapInfo != null || findedMapInfo !== undefined) {
		const {description} = findedMapInfo;
		const container = document.createElement('div')
		container.innerHTML = description
		modalWindowContainer.openWithContent(container, "Информация о карте")
	} else {
		modalWindowContainer.openWithContent("Нет информации")
	}
}

intefaceController
	.layerController
	.fillLayerController = function (sizebarTarget, mapController) {

	const typeSelectBtn = this._target.querySelector('.layers-title')
	const listTypes = this._target.querySelector('.layers-list-types')

	typeSelectBtn.onclick = () => {
		listTypes.toggleAttribute("hidden")
	}

	const getListTypesFromSizebar = () => {
		return sizebarTarget.querySelectorAll('#heatmap-types label')
	}

	const CreateListTypesFromSizebar = function () {
		const actionSelectionType = (e) => {
			this._changeScaleSignature(e.target.id)
			mapController.showPoligonsOnTypes(e.target.id)
		}
		const actionShowMapInfo = (e) => {
			this._showMapInfo(e.target.parentElement.querySelector('input').id)
		}
		const srcLabelsOfTypes = getListTypesFromSizebar();
		const dstTypeList = this._target.querySelector('.layers-list-types')

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

		for (const labelType of srcLabelsOfTypes) {
			const typeElement = createTypeElement(labelType)
			dstTypeList.append(typeElement)
		}
	}
	CreateListTypesFromSizebar.call(this)
}

intefaceController
	.layerController
	.setLayerType = function (type) {
	const typeList = this._target.querySelectorAll('.layers-list-types input')
	for (const inputWithType of typeList) {
		if (inputWithType.id === type) {
			inputWithType.click()
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

	this._target.querySelector('.sizebar-content-control .optional-btns')
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
	if (this._deleteBtn != null) {
		this._deleteBtn.remove()
	}
	if (this._completeBtn != null) {
		this._completeBtn.remove()
	}
	if (this._editBtn != null) {
		this._editBtn.remove()
	}
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
		this._addCompleteBtn(e.target, () => {
		})
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
			this._target.querySelector('.sizebar-content-control .main-btns')
				.append(this._editBtn)
			this._target.querySelector('.sizebar-content-control .main-btns')
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
			this._target.querySelector('.sizebar-content-control .main-btns')
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
			.toggle("activate")
	}
}

intefaceController
	.areaController
	.controlAreaStates = function (mapController) {
	this._mapController = mapController

	const optionInputs = this._optionBar.querySelectorAll('input')
	for (const input of optionInputs) {
		input.onclick = (e) => this.setAreaMode(e.target.id)
	}
	this._selectBtn = this._createBtnWithAct("Выбрать", 'select-btn', () => {
	})
	this.setAreaMode(areaMode.editableMode)

	this._target.querySelector('.sizebar-content-control .main-btns')
		.append(this._selectBtn)

	this._createArrow()
}
intefaceController
	.areaController
	.setLoadState = function (loadState) {
	const loadItem = document.querySelector('.load-item')
	if (loadState === 'show') {
		loadItem.classList.add('activate')
	}
	if (loadState === 'hide') {
		loadItem.classList.remove('activate')
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

// SectorController BEGINNUNG init
intefaceController
	.sectorController
	._areaController = intefaceController.areaController

intefaceController
	.sectorController
	._messageController = intefaceController.messageController

intefaceController
	.sectorController
	.listenCalcEvent = function (mapController) {
	this._controlledBnt.onclick = (e) => {
		e.target.disabled = true
		const heatMapType = this._leftSizebar
			.querySelector('#heatmap-types input[name="choiceMap"]:checked')
			.value
		try {
			const sectorCoords = mapController.getBountsSelectedArea()
			const areaCoords = mapController.getCoordsSelectedArea()

			const alreadyExist = mapController.existSameArea(areaCoords, heatMapType)

			if (alreadyExist) {
				throw new GeneralWarning('Same area already has exist')
			}
			const areaOfArea = mapController.calculateAreaSelectedArea()
			if (areaOfArea > LARGE_AREA) {
				throw new GeneralWarning('Selected area is too large')
			}

			this._areaController
				.setLoadState(loadState.show)
			createAndShowArea({
				sectorCoords,
				type: heatMapType,
				areaCoords,
			}, mapController)
				.then(() => e.target.disabled = false)

		} catch (err) {
			this._messageController
				.showMessage(err)
			e.target.disabled = false
		}

	}
}

// SectorController END init

function getInfoAboutMapsFromBPApp() {
	return requestForBpServer('GET', 'getInterfaceInfo')
		.then(data => data.json())
		.then(jsonResponse => jsonResponse)
		.catch(err => {
			ICtrl.messageController
				.showMessage(err)
		})
}

function getInfoAboutMaps() {
	return fetch('/data/interfaceInfo.json', {
		method: 'GET'
	})
		.then(data => data.json())
		.then(jsonResponse => jsonResponse)
		.catch(err => {
			ICtrl.messageController
				.showMessage(err)
		})
}

function init() {
	const mapController = new YMapController('map', [107.88, 54.99], ymaps)
	intefaceController
		.layerController
		.fillLayerController(document.querySelector('.sizebar'), mapController)

	intefaceController
		.areaController
		.controlAreaStates(mapController)
	cteateBallonTemplate(mapController)

	intefaceController
		.sectorController
		.listenCalcEvent(mapController)

	getInfoAboutMaps()
		.then(data => {
			console.log(`MapsInfos loaded`)
			console.log(data)
			return data
		})
		.then(res => intefaceController.layerController._mapsInfos = res)
//	showUpdateMapsInfoFromHTML()

}

ymaps.ready({
	require: ['util.calculateArea'],
	successCallback: init
})
