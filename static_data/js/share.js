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

async function requestForBpServer(method, URL, postPatams) {
  return fetch(`/bp-app/${URL}`, {
    method,
    ...postPatams
  })
    .then(response => {
      if (response.ok) {
        return response
      }
      if (response.status === 502) {
        throw new GeneralError('Server Error')
      }
      return response.text()
        .then(text => {
          throw new GeneralError(text)
        })
    })

}