// /* eslint-disable */

class YMap {
  constructor(name, centerCoords) {
    this.state = 0;
    this.zoom = 5;
    this.dispayField = '';
    this.placemarks = [];
    this.poly = new ymaps.Polygon([], {}, {
      editorDrawingCursor: 'crosshair',
      strokeColor: '#0000ff',
      strokeWidth: 2,
    });
    this.map = new ymaps.Map(
      name,
      {
        center: centerCoords,
        zoom: this.zoom,
        controls: ['zoomControl', 'typeSelector'],
      },
    );
    this.addControls();
  }

  addControls() {
    const myButton = new ymaps.control.Button({
      data: {
        content: 'Выбор',
        title: 'Состояние выделения области',
      },
    });
    myButton.events
      .add('click', () => { console.log('Смена состояния'); })
      .add('select', () => { this.polyLayer(1); })
      .add('deselect', () => { this.polyLayer(0); });
    this.map.controls.add(myButton);
  }

  polyLayer(state) {
    if (state === 0) {
      this.poly.editor.stopDrawing();
      this.poly.editor.stopEditing();
      return;
    }
    this.map.geoObjects.add(this.poly);
    const myButton = new ymaps.control.Button({
      data: {
        content: 'Удалить ',
      },
    });
    myButton.events.add('click', () => { console.log('Удалить выбранную область'); });
    this.map.controls.add(myButton);

    this.poly.editor.startDrawing();
  }

  setDisplayField(querySelector) {
    this.dispayField = querySelector;
  }

  showPlacemarkCoords(coordinates) {
    const field = document.querySelector(this.dispayField);
    field.textContent = `Долгота: ${coordinates[0]}\nШирота: ${coordinates[1]}`;
  }

  createPlacemark(events, isDraggable) {
    const coords = events.get('coords');
    const placemark = new ymaps.Placemark(
      coords,
      {
        balloonContent: '<a href="">Удалить<a>',
      },
      {
        preset: 'islands#redIcon',
        draggable: isDraggable,
      },
    );
    return placemark;
  }

  eventCreatePlacemark(mapEvent) {
    this.map.events.add(mapEvent, (events) => {
      this.poly.coordinates.push(events.get('coords'));
    });
  }
}
// this.map.events.add(mapEvent, (events) => {
//   const placemark = this.createPlacemark(events, true);
//   placemark.events.add('dragend', () => {
//     const newCoord = placemark.geometry.getCoordinates();
//     console.log(this);
//     this.showPlacemarkCoords(newCoord);
//   });
//   this.placemarks.push(placemark);
//   this.map.geoObjects.add(placemark);
// });
