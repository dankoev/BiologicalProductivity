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
export { GeneralError, GeneralWarning, ServerError }

