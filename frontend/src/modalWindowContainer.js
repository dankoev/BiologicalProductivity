import "./css/modalWindowContainer.css"

const modalWindowTemplate =
  `<div class="modal-window">
    <div class="modal-window__close"></div>
    <h3></h3>
    <section class="modal-window__content">
    </section>
  </div>`

const modalWindowContainer = (() => {
  const container = document.createElement('section')
  container.id = "modal-window-container"
  container.innerHTML = modalWindowTemplate.trim()
  return {
    target: container,
    openWithContent,
    close
  }
})()

function openWithContent(content, textHeader) {
  modalWindowContainer
    .target
    .querySelector("h3")
    .textContent = textHeader
  modalWindowContainer
    .target
    .querySelector("section.modal-window__content")
    .append(content)
  document.body.append(modalWindowContainer.target)
}

function close() {
  const removedNodes = modalWindowContainer
    .target
    .querySelector('.modal-window__content')
    .childNodes
  Array.from(removedNodes).forEach(el => el.remove())
  modalWindowContainer.target.remove()
}

modalWindowContainer
  .target
  .addEventListener('click', () => {
    close()
  })

modalWindowContainer
  .target
  .querySelector('.modal-window')
  .addEventListener('click', (e) => {
    e.stopPropagation()
  })

modalWindowContainer
  .target
  .querySelector("div.modal-window__close")
  .addEventListener('click', () => {
    close()
  })


export {modalWindowContainer}