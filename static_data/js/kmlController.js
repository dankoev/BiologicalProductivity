import {modalWindowContainer} from "./modalWindowContainer.js";


export const kmlController = {
	modalWindow: {
		_htmlTemplate: `
      <div class="kml-areas">
        <ul class="kml-areas-list"></ul>
      </div>
      <template id="kml-li-template">
        <li>
          <label>
          <input type="button" hidden>
          </label>
        </li>
      </template>
    `,
		_modalContainer: modalWindowContainer,
		_closeEvent: () => {
		}
	},
}
kmlController
	.modalWindow
	._target = (function () {
	const modalSection = document.createElement("section")
	modalSection.classList.add("kml-window")
	modalSection.innerHTML = this._htmlTemplate.trim()
	return modalSection
}).call(kmlController.modalWindow)

kmlController
	.modalWindow
	.open = async function (callbackAfterLoadArea) {
	new Promise(resolve => resolve(this._addAreas(callbackAfterLoadArea)))
		.then(() => {
			this._modalContainer.openWithContent(this._target)
		})
		.catch(err =>
			this._ICtrl
				.messageController
				.showMessage(err)
		)
}


kmlController
	.modalWindow
	.close = function () {
	this._closeEvent()
	this._modalContainer.close()
}
kmlController
	.modalWindow
	.setCloseEvent = function (callback) {
	this._closeEvent = callback
}

kmlController
	.modalWindow
	._addAreaToList = function (name, link, callbackAfterLoadArea) {
	const kmlAreasList = this._target.querySelector('ul.kml-areas-list')
	const kmlLiTemplate = this._target.querySelector('#kml-li-template')
	const kmlLi = kmlLiTemplate.content.firstElementChild.cloneNode(true)
	const kmlLiInput = kmlLi.querySelector("input")
	kmlLiInput.name = link
	kmlLiInput.onclick = () => {
		this.close()
		console.log('start load kml')
		ymaps.geoXml.load(link)
			.then((res) => this.onGeoXmlLoad(res))
			.then((area) => callbackAfterLoadArea(area))
			.catch(err => {
				this._ICtrl
					.messageController
					.showMessage(err)
			})
	}
	kmlLi.querySelector("label").append(name)
	kmlAreasList.append(kmlLi)
}

kmlController
	.modalWindow
	._addAreas = function (callbackAfterLoadArea) {
	if ("content" in document.createElement("template")) {
	} else {
		throw new Error("Your browser don't support loading kml function")
	}
	if (this._target.querySelector('label')) {
		return
	}
	this._addAreaToList('р.Ия (Тулун)', 'https://drive.google.com/u/0/uc?id=1BZscRohGijQSMfrGDF9Ha2xTwSBTtwSF&export=download', callbackAfterLoadArea)
}

kmlController
	.modalWindow
	.onGeoXmlLoad = function (res) {
	return this._checkGeoObjsContent(res)
}
kmlController
	.modalWindow
	._checkGeoObjsContent = function (res) {
	let polygonCount = 0
	let onlyPolygon;
	res.geoObjects.each((gObj) => {
		if (gObj.geometry.getType() === "Polygon") {
			polygonCount += 1
			onlyPolygon = gObj
		}
	})
	if (polygonCount !== 1) {
		throw new Error("Error load KML file. Number polygon > 1")
	}
	return onlyPolygon;
}
