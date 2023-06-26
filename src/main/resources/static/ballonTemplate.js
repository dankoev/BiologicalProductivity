class balloonTemplate{

    constructor(polygon, info){
        this.polygon = polygon
        this.info = info
        createTemplate()
    }
    createTemplate = function(){
     this.balloonContentLayout = ymaps.templateLayoutFactory.createClass(
        `<div>
          <p id="max"> Max: </p>
          <p id="min"> Min: </p>
          <p id="average">Average: </p>
          <button id="delete"> Удалить </button>
        </div>`, {
        build: function () {
            mapTemplates.balloonContentLayout.superclass.build.call(this)
            document.querySelector('#maxAreaValue').append(1)
            document.querySelector('#min').append(1)
            document.querySelector('#average').append(1)
            document.querySelector('#delete').onclick = this.delete      
        },
    
        clear: function () {
    
          document.querySelector('#delete').removeEventListener('click',this.delete)
          mapTemplates.balloonContentLayout.superclass.clear.call(this)
        },
        delete: () =>{
          console.log(this)
        },
      })
    }
}