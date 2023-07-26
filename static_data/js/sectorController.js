const ICtrl = intefaceController

function createPolyWithSector(blobURL, sectorInfoRequest) {
	const {sectorCoords, type, areaCoords} = sectorInfoRequest
	const sectorStatisticsPromise = getLastSectorStatistics()

	const poly = new ymaps.Polygon(
		[[sectorCoords[0],
			[sectorCoords[1][0], sectorCoords[0][1]],
			sectorCoords[1],
			[sectorCoords[0][0], sectorCoords[1][1]],
		]],
		{
			balloonContentHeader: "Area info",
			balloonContent: ``,
			hintContent: "show information",
			sectorType: type,
			areaCoords,
		},
		{
			balloonContentLayout: mapTemplates.balloonContentLayout,
			fillImageHref: blobURL,
			fillMethod: 'stretch',
			opacity: 0.8,
		},
	)

	sectorStatisticsPromise.then(data => {
		poly.properties.set({areaStatistics: data})
	})

	ICtrl.layerController.setLayerType(type)
	mapController.map.geoObjects.add(poly)
	return poly;
}

async function createAndShowArea(sectorInfoRequest) {
	return requestForBpServer('POST', 'getHeatMapOfSector', {
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(sectorInfoRequest)
	})
		.then(data => data.blob())
		.then(blob => {
			const blobURL = URL.createObjectURL(blob)
			createPolyWithSector(blobURL, sectorInfoRequest)
			ICtrl.areaController
				.setLoadState(loadState.hide)
		})
		.catch(err => {
			ICtrl.messageController
				.showMessage(err)
			ICtrl.areaController
				.setLoadState(loadState.hide)

		})
		.finally(console.log(`request heatmap sent`))
}

async function getLastSectorStatistics() {
	return requestForBpServer('GET', 'getLastSectorStatistics')
		.then(data => data.json())
		.then(jsonResponse => jsonResponse)
		.catch(err => {
			ICtrl.messageController
				.showMessage(err)
		})
}

document.addEventListener('DOMContentLoaded', () => {
	const leftSizebar = document.querySelector('.sizebar')
	leftSizebar.querySelector('#calculatePolygon').onclick = (e) => {
		e.target.disabled = true
		const heatMapType = leftSizebar
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
			console.log(areaOfArea)
			if (areaOfArea > LARGE_AREA) {
				throw new GeneralWarning('Selected area is too large')
			}

			ICtrl.areaController
				.setLoadState(loadState.show)
			createAndShowArea({
				sectorCoords,
				type: heatMapType,
				areaCoords,
			})
				.then(() => e.target.disabled = false)


		} catch (err) {
			ICtrl
				.messageController
				.showMessage(err)
			e.target.disabled = false
		}

	}

})



