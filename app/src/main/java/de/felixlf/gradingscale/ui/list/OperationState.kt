package de.felixlf.gradingscale.ui.list


sealed class OperationState(val description: String) {
  object EditGrade: OperationState("EditGrade")
  object AddNewGrade: OperationState("AddNewGrade")
  object DeleteGrade: OperationState("DeleteGrade")
  object AddNewGradeScale: OperationState("AddNewGradeScale")
  object DeleteGradeScale: OperationState("DeleteGradeScale")
  object UpdateGradeScale: OperationState("Update Grade Scale")
  object Opened: OperationState("Opened")
  object LoadFirstGradeScale: OperationState("LoadFirstGradeScale")
  object LoadLastGradeScale: OperationState("LoadLastGradeScale")
  object OperationFinished: OperationState("Finished")
  class UpdateCurrentGradeScaleName(val currentGradeScaleName: String): OperationState("UpdateCurrentGradeScale")
}