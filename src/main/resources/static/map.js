"use strict";
var mapData = { 
    map : null,
    zoom : 5,
    coordinatesCenter : [107.88,54.99],
}
ymaps.ready(init);
function init(){
    mapData.map = new ymaps.Map('map', {
        center: mapData.coordinatesCenter,
        zoom: mapData.zoom,
        controls: ['zoomControl', 'typeSelector']
    });
}