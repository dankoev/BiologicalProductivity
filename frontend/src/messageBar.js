import "./css/messageBar.css"
import { GeneralWarning, GeneralError } from "./share"

const messageWindow = document.querySelector('#message-window')

export function showMessage(err) {
  const messageDiv = document.createElement('div')
  const message = document.createElement('p')

  if (err instanceof GeneralError) {
    messageDiv.style.cssText = 'background-color: red;'
  }
  else if (err instanceof GeneralWarning) {
    messageDiv.style.cssText = 'background-color: #e7c608;'
  }
  else if (err instanceof Error) {
    messageDiv.style.cssText = 'background-color: red;'
  }

  if (err.message.length > 120) {
    message.textContent = "Too much error content. Check console"
    console.log(err.message)
  } else {
    message.textContent = err.message
  }

  messageDiv.addEventListener('transitionend', function (event) {
    if (event.propertyName !== 'opacity') {
      return;
    }
    if (getComputedStyle(event.target).opacity == 0) {
      event.target.remove()
    }
  });
  messageDiv.append(message)
  messageWindow.append(messageDiv)
  setTimeout(() => {
    messageDiv.classList.add('smooth-hide')
  }, 10);
}