const messageType = {
  error: 'error',
  warning: 'warning'
}
const loadState = {
  show: 'show',
  hide: 'hide'
}
const areaMode = {
  editableMode: 'editable-mode',
  kmlMode: 'kml-mode'
}
const selectedAreaStates = {
  select: 'select',
  edit: 'edit',
  delete: 'delete',
  complete: 'complete',
}

function getHtmlTemplate(name) {
  return fetch(name, {
    method: 'GET',
  })
    .then(response => {
      if (response.ok) {
        return response
      }
      return response.text()
        .then(text => { throw new Error(text) })
    })
    .then(data => data.text())
    .then(jsonResponse => jsonResponse)
    .catch(e => {
      intefaceController
        .messageController
        .showMessage(e, messageType.error)
    })
}