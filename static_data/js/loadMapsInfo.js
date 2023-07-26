function showUpdateMapsInfoFromHTML() {
	fetch('/data/mapsInfosHTML.html', {
		method: 'GET'
	})
		.then(res => res.text())
		.then(htmlText => splitMapsInfos(htmlText))
		.then(htmlTransformInfo => {
			fetch('/data/interfaceInfo.json', {
				method: 'GET'
			})
				.then(res => res.json())
				.then(mapInfoJson => updateDescriptionInJSON(mapInfoJson, htmlTransformInfo))
				.then(data => console.log(data))
		})
		.catch(err => console.error('Error read file with mapsInfos\n' + err))
}

function splitMapsInfos(infoHTMLasText) {
	const spliter = /<!--beginning\s([A-Z]+?)-->(.*?)<!--([A-Z]+?) end-->/gs
	const mapsInfos = infoHTMLasText.matchAll(spliter)
	return new Promise(resolve => resolve(transformData(mapsInfos)))
}

function transformData(mapsInfos) {
	return [...mapsInfos].map(el => {
		if (el[1] === el[3]) {
			return {
				type: el[1],
				description: el[2]
			}
		}
	})
}

function updateDescriptionInJSON(mapsInfosJSON, transformedDataFromHTML) {

	return mapsInfosJSON.map(mapInfoJSON => {
		const newMapInfo = transformedDataFromHTML
			.find(mapInfoHTML => mapInfoJSON.type === mapInfoHTML.type)
		mapInfoJSON.description = newMapInfo?.description ?? "No description"
		return mapInfoJSON
	})
}