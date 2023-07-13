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

  setSelectedAreaState(state) {
    switch (state) {
      case 'select':
        if (this._selectedArea === undefined) {
          this._selectedArea = new ymaps.Polygon([], {}, {
            editorDrawingCursor: 'crosshair',
            strokeColor: '#0000ff',
            strokeWidth: 2,
          });
          this.map.geoObjects.add(this._selectedArea);
        }

      case 'edit':
        this._selectedArea.editor.startDrawing();
        break;
      case 'delete':
        this.map.geoObjects.remove(this._selectedArea);
        this._selectedArea = undefined
        break;
      case 'complete':
        this._selectedArea.editor.stopDrawing();
        this._selectedArea.editor.stopEditing();
        break;
    }
  }
  setSelectedArea(area) {
    this._selectedArea = area
  }
  getSelectedArea() {
    return this._selectedArea
  }
  selectedAreaExist() {
    return this._selectedArea.geometry.getCoordinates()[0].length > 1
  }

  getBountsSelectedArea() {
    return this._selectedArea.geometry.getBounds()
  }
  setBoundsForMap(bounds) {
    this.map.setBounds(bounds)
  }
  addObject(geoObject) {
    this.map.geoObjects.add(geoObject);
  }
  getCoordsSelectedArea() {
    const transformArrayCoords = (coords) => {
      return coords.map(el => [el[0], el[1]])
    }
    const areaCoords = this._selectedArea.geometry.getCoordinates()[0]
    return transformArrayCoords(areaCoords)
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

