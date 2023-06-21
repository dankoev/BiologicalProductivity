class YMapController {
  constructor(name, centerCoords) {
    this.zoom = 5;

    var BalloonContentLayout = ymaps.templateLayoutFactory.createClass(
      `<div>
      <button id="delete">Удалить</button>
      <button id="edit">Редактировать</button>
      </div>`, {
      build: function () {
          BalloonContentLayout.superclass.build.call(this);
          document.querySelector('#delete').onclick = this.delete;
          document.querySelector('#edit').onclick = this.edit;
          
      },

      clear: function () {
        document.querySelector('#delete').removeEventListener('click',this.delete)
        document.querySelector('#edit').removeEventListener('click',this.edit)
        BalloonContentLayout.superclass.clear.call(this);
      },
      delete: () => {
        this.setSelectState('delete');
      },
      edit: () => {
        this.setSelectState('edit');
       console.log()
      }
    });

    var map = new ymaps.Map(
      name,
      {
        center: centerCoords,
        zoom: this.zoom,
        controls: [ 'typeSelector'],
      }),
      BalloonContentLayout;
    this.balloonContentLayout = BalloonContentLayout
    this.map = map

  }
  

  setSelectState(state) {
    switch (state){
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
  selectedAreaExist(){
    console.log(this.selectedArea.geometry.getCoordinates())
    return this.selectedArea.geometry.getCoordinates()[0].length > 1
  }


}

