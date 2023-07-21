class YMapController {
  constructor(name, centerCoords, ymaps) {
    this.zoom = 5
    this._ymaps = ymaps
    this.map = new this._ymaps.Map(
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
    if (this._selectedArea == null || this._selectedArea == undefined) {
      return false
    }
    return this._selectedArea.geometry.getCoordinates()[0].length > 1
  }

  getBountsSelectedArea() {
    this._checkForSmallArea()
    return this._selectedArea.geometry.getBounds()
  }
  setBoundsForMap(bounds) {
    this.map.setBounds(bounds)
  }
  addObject(geoObject) {
    this.map.geoObjects.add(geoObject);
  }
  getCoordsSelectedArea() {
    this._checkForSmallArea()
    const transformArrayCoords = (coords) => {
      return coords.map(el => [el[0], el[1]])
    }
    const areaCoords = this._selectedArea.geometry.getCoordinates()[0]
    return transformArrayCoords(areaCoords)
  }
  calculateAreaSelectedArea() {
    this._checkForSmallArea()
    const areaSelectedArea = this._ymaps.util.calculateArea(this._selectedArea)
    return (areaSelectedArea / 1e6).toFixed(3);
  }

  existSameArea(coords, type) {
    const comparedGeoObj = { coords, type }


    const isSame = (geoObj, comparedGeoObj) => {
      const gProps = geoObj.properties
      if (comparedGeoObj.type === gProps.get('sectorType')
        && comparedGeoObj.coords.length === gProps.get('areaCoords').length) {
        return JSON.stringify(comparedGeoObj.coords) === JSON.stringify(gProps.get('areaCoords'))
      }
      return false;
    }

    const geoObjectsIter = this.map.geoObjects.getIterator();
    let geoObj = geoObjectsIter.getNext()
    while (Object.keys(geoObj).length !== 0) {
      const areasIsSames = isSame(geoObj, comparedGeoObj)

      if (areasIsSames) return true
      geoObj = geoObjectsIter.getNext()
    }
    return false
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
  _checkForSmallArea() {
    if (!this.selectedAreaExist()
      || this._selectedArea.geometry.getCoordinates()[0].length < 4) {
      throw new GeneralError("Area is too small or not selected")
    }
  }
}

