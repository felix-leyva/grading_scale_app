package de.felixlf.gradingscale.model

sealed class ExistingStatus{
  object NonExisting: ExistingStatus()
  object Duplicated: ExistingStatus()
  object ExistingNotSame: ExistingStatus()
}
