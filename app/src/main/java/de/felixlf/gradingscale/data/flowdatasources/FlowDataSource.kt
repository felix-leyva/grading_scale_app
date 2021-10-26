package de.felixlf.gradingscale.data.flowdatasources

import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FlowDataSource @Inject constructor(private val giSRepository: GiSRepository) {


  fun flowGradeScaleNames() = giSRepository.getGradesFromScaleFlow()
    .map { gIS -> gIS.map { it.gradeScale.gradeScaleName } }

  fun flowGradeScaleSelection(selectedGradeScaleFlow: MutableStateFlow<String>) =
    giSRepository.getGradesFromScaleFlow()
      .combineTransform(selectedGradeScaleFlow) { gIS, mFilter ->
        val filteredList = try {
          gIS.map { it.grades }.flatten().filter { it.nameOfScale == mFilter }
        } catch (ex: NoSuchElementException) {
          //ex.printStackTrace()
          gIS[0].grades
        }

        emit(filteredList)
      }

  fun flowGradeInScaleSelection(selectedGradeScaleFlow: MutableStateFlow<String>) =
    giSRepository.getGradesFromScaleFlow()
      .combineTransform(selectedGradeScaleFlow) { gIS, mFilter ->
        val filteredGIS = try {
          gIS.first { it.gradeScale.gradeScaleName == mFilter }
        } catch (ex: NoSuchElementException) {
          //ex.printStackTrace()
          GradesInScale(GradeScale(gradeScaleName = ""), mutableListOf(Grade()))
        //gIS[0]
        }
        filteredGIS.grades.sortByDescending { it.percentage }
        emit(filteredGIS)
      }



}