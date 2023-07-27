import ymaps from "ymaps"

let init = () => {
  const map = new ymaps.Map(
    'map',
    {
      center: [107.88, 54.99],
      zoom: 5,
      controls: ['typeSelector'],
    })
  map.geoO
}
ymaps.ready({
  require: ['util.calculateArea'],
  successCallback: init
})


import "./css/styles.css"
//import {fromEvent, throttleTime, scan} from 'rxjs';
//
//fromEvent(document, 'click')
//  .pipe(
//    throttleTime(1000),
//    scan((count) => count + 1, 0)
//  )
//  .subscribe((count) => console.log(`Clicked ${count} times`));
