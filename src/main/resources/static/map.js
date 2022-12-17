"use strict";
var mapData = { 
    map : null,
    zoom : 5,
    coordinatesCenter : [107.88,54.99],
}
ymaps.ready(init);
function init(){
    createMap();
    addMapEvent();
    
}
function createMap(){
    mapData.map = new ymaps.Map('map', {
        center: mapData.coordinatesCenter,
        zoom: mapData.zoom,
        controls: ['zoomControl', 'typeSelector']
    })
}
function addMapEvent(){
    mapData.map.events.add('click', function (events) {
        let placemark = createPlacemark(events,true)
        addPlacemarkEvent(placemark,'dragend')
        mapData.map.geoObjects.add(placemark)
    })
}
function createPlacemark(events,isDraggable){
    let coords = events.get('coords')
    let placemark = new ymaps.Placemark(
        coords,
        {},
        {preset: "islands#redIcon",
            draggable: isDraggable}
    )
    showPlacemarkCoords(coords)
    return placemark
}
function addPlacemarkEvent(placemark, action){
    placemark.events.add(action, function (e) {
        console.log(this)
        let newCoord = this.geometry.getCoordinates()
        showPlacemarkCoords(newCoord)
    },  placemark)
}
function showPlacemarkCoords(coordinates){
    console.log(coordinates)
    $('#placemarkCoords').text('Долгота: ' + coordinates[0] + '\nШирота: ' + coordinates[1])
}