const modalWindowContainer = {
  _target: function () {
    const container = document.createElement('section')
    container.id = "modal-window-container"
    return container;
  }(),
  _content: ""

}
modalWindowContainer
  .init = function () {
    this._initPromise = getHtmlTemplate('htmlTemplates/modalWindow.html')
      .then(modalWindowHtml => this._target.insertAdjacentHTML("afterbegin", modalWindowHtml))
    return this
  }

modalWindowContainer
  .open = async function () {
    this._target
      .querySelector("div.modal-window-close")
      .addEventListener('click', () => {
        this.close()
      })
    this._target
      .querySelector("section.modal-window-content")
      .append(this._content)
    document.body.append(this._target)
  }
modalWindowContainer
  .close = function () {
    this._target.remove()
  }
modalWindowContainer
  .setContent = function (content) {
    this._content = content
  }
