import {intefaceController as ICtrl} from "./interfaceController.js";


function createPolyWithSector(blobURL, sectorInfoRequest, mapController) {
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

export async function createAndShowArea(sectorInfoRequest, mapController) {
	return requestForBpServer('POST', 'getHeatMapOfSector', {
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(sectorInfoRequest)
	})
		.then(data => data.blob())
		.then(blob => {
			const blobURL = URL.createObjectURL(blob)
			createPolyWithSector(blobURL, sectorInfoRequest, mapController)
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



