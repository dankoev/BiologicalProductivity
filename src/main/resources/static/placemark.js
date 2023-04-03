class YPlacemark {
  constructor(name, options) {
    this.options = options;
    this.map = new ymaps.Map(name, options);
  }

  showPlacemarkCoords(coordinates) {
    $('#placemarkCoords').text(`Долгота: ${coordinates[0]}\nШирота: ${coordinates[1]}`);
  }

  addPlacemarkEvent(placemark, action) {
    placemark.events.add(action, () => {
      console.log(this);
      const newCoord = this.geometry.getCoordinates();
      showPlacemarkCoords(newCoord);
    }, placemark);
  }

  createPlacemark(events, isDraggable) {
    const coords = events.get('coords');
    const placemark = new ymaps.Placemark(
      coords,
      {},
      {
        preset: 'islands#redIcon',
        draggable: isDraggable,
      },
    );
    return placemark;
  }

  createMapEvent(mapEvent, action) {
    this.map.events.add(mapEvent, (events) => {
      const placemark = createPlacemark(events, true);
      addPlacemarkEvent(placemark, placemarkEvent);
      this.map.geoObjects.add(placemark);
    });
  }
}
