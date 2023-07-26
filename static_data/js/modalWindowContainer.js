const modalWindowContainer = {
	_htmlTemplate: `
  <div class="modal-window">
    <div class="modal-window-close"></div>
    <h3></h3>
    <section class="modal-window-content">
    </section>
  </div>
  `
}

modalWindowContainer
	._target = (function () {
	const container = document.createElement('section')
	container.id = "modal-window-container"
	container.innerHTML = this._htmlTemplate.trim()
	const createCloseAction = () => {
		container.addEventListener('click', () => {
			this.close()
		})
		container.querySelector('.modal-window')
			.addEventListener('click', (e) => {
				e.stopPropagation()
			})
		container.querySelector("div.modal-window-close")
			.addEventListener('click', () => {
				this.close()
			})
	}
	createCloseAction()
	return container;
}).call(modalWindowContainer)

modalWindowContainer
	.openWithContent = function (content, textHeader) {
	this._target.querySelector("h3")
		.textContent = textHeader
	this._target
		.querySelector("section.modal-window-content")
		.append(content)
	document.body.append(this._target)
}

modalWindowContainer
	.close = function () {
	const removedNodes = this._target.querySelector('.modal-window-content')
		.childNodes
	console.log(removedNodes)
	Array.from(removedNodes).forEach(el => el.remove())
	this._target.remove()
}
