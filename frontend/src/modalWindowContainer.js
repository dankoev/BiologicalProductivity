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
  return container
})()

function close() {
  const removedNodes = modalWindowContainer
    .querySelector('.modal-window__content')
    .childNodes
  Array.from(removedNodes).forEach(el => el.remove())
  modalWindowContainer.remove()
}

modalWindowContainer
  .addEventListener('click', () => {
    close()
  })

modalWindowContainer
  .querySelector('.modal-window')
  .addEventListener('click', (e) => {
    e.stopPropagation()
  })

modalWindowContainer
  .querySelector("div.modal-window__close")
  .addEventListener('click', () => {
    close()
  })


function openWithContent(content, textHeader) {
  modalWindowContainer
    .querySelector("h3")
    .textContent = textHeader
  modalWindowContainer
    .querySelector("section.modal-window__content")
    .append(content)
  document.body.append(modalWindowContainer)
}

export {openWithContent}