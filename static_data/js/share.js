const LARGE_AREA = 25_000
const loadState = {
	show: 'show',
	hide: 'hide'
}
const areaMode = {
	editableMode: 'editable-mode',
	kmlMode: 'kml-mode'
}
const selectedAreaStates = {
	select: 'select',
	edit: 'edit',
	delete: 'delete',
	complete: 'complete',
}

class GeneralWarning extends Error {
	constructor(message) {
		super(message)
		this.name = "GeneralWarning"

	}
}

class GeneralError extends Error {
	constructor(message) {
		super(message)
		this.name = "GeneralError"

	}
}

async function requestForBpServer(method, URL, postPatams) {
	return fetch(`/bp-app/${URL}`, {
		method,
		...postPatams
	})
		.then(response => {
			if (response.ok) {
				return response
			}
			if (response.status === 502) {
				throw new GeneralError('Server Error')
			}
			return response.text()
				.then(text => {
					throw new GeneralError(text)
				})
		})

}

let mapTemplates = {}

function cteateBallonTemplate(mapController) {
	mapTemplates.balloonContentLayout = ymaps.templateLayoutFactory.createClass(
		`<div>
      <p id="max"> Max: {{ properties.areaStatistics.maxSectorValue | default:"No data"  }} </p>
      <p id="min"> Min: {{ properties.areaStatistics.minSectorValue | default:"No data"  }} </p>
      <p id="average">Average: {{ properties.areaStatistics.averageSectorValue | default:"No data" }} </p>
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