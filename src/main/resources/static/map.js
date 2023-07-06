class YMapController {
  constructor(name, centerCoords) {
    this.zoom = 5
    this.map = new ymaps.Map(
      name,
      {
        center: centerCoords,
        zoom: this.zoom,
        controls: ['typeSelector'],
      })
  }

  setSelectState(state) {
    switch (state) {
      case 'select':
        if (this.selectedArea === undefined) {
          this.selectedArea = new ymaps.Polygon([], {}, {
            editorDrawingCursor: 'crosshair',
            strokeColor: '#0000ff',
            strokeWidth: 2,
          });
          this.map.geoObjects.add(this.selectedArea);
        }

      case 'edit':
        this.selectedArea.editor.startDrawing();
        break;
      case 'delete':
        this.map.geoObjects.remove(this.selectedArea);
        this.selectedArea = undefined
        break;
      case 'complete':
        this.selectedArea.editor.stopDrawing();
        this.selectedArea.editor.stopEditing();
        break;
    }
  }
  selectedAreaExist() {
    console.log(this.selectedArea.geometry.getCoordinates())
    return this.selectedArea.geometry.getCoordinates()[0].length > 1
  }
  showOrHidePoligonsOnTypes(type) {
    this.map.geoObjects.each(gObj => {
      if (gObj.properties.get("sectorType") == undefined) {
        return
      }
      if (gObj.properties.get("sectorType") === type) {
        gObj.options.set('visible', true)
      } else {
        gObj.options.set('visible', false)
      }
    });

  }

}

