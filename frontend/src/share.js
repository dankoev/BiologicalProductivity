const areaState = {
  select: 'select',
  edit: 'edit',
  delete: 'delete',
  complete: 'complete',
}
const typeMode = {
  editable: 'editable',
  kml: 'kml'
}

const LARGE_AREA = 25_000

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

class ServerError extends GeneralError {
  constructor(message) {
    super(message)
    this.name = "ServerError"
  }
}

export {GeneralError, GeneralWarning, ServerError, areaState, typeMode, LARGE_AREA}

