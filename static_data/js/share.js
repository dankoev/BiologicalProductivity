const LARGE_AREA = 25_000
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

class GeneralWarning extends Error {
  constructor(message) {
    super(message)
    this.name = "GeneralWarning"

  }
}
class GeneralError extends Error {
  constructor(message) {
    super(message)
    this.name = "GeneralError"

  }
}

function getHtmlTemplate(name) {
  return fetch(`/htmlTemplates/${name}`, {
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
    .catch(err => {
      intefaceController
        .messageController
        .showMessage(err)
    })
}