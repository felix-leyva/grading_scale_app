package de.felixlf.gradingscale.utils

import android.content.Context
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.model.firebase.CountriesAndGradeScales
import de.felixlf.gradingscale.model.firebase.GradeRemote
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import kotlinx.serialization.Serializable
import java.io.IOException

//https://bezkoder.com/java-android-read-json-file-assets-gson/
//https://kotlinlang.org/docs/reference/serialization.html
fun simpleGradeToGiS(jsonGradeScale: JsonGradeScale): GradesInScale {
  val nameOfScale = "${jsonGradeScale.country} - ${jsonGradeScale.gradeScaleName}"
  val grades = jsonGradeScale.grades.map { simpleGrade ->
    Grade(
      namedGrade = simpleGrade.namedGrade,
      percentage = simpleGrade.percentage,
      nameOfScale = nameOfScale
    )
  }.toMutableList()
  val gradeScale = GradeScale(gradeScaleName = nameOfScale)
  return GradesInScale(gradeScale, grades)
}


fun readStringFromFile(fileName: String, appContext: Context): String? {
  return try {
    val inputStream = appContext.assets.open(fileName)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    String(buffer, Charsets.UTF_8)
  } catch (ex: IOException) {
    ex.printStackTrace()
    null
  }

}



fun JsonGradeScale.toGradeScaleRemote(): GradeScaleRemote {
  return GradeScaleRemote(
    gradeScaleName = this.gradeScaleName,
    country = this.country,
    grades = this.grades.map { GradeRemote(gradeName = it.namedGrade, percentage = it.percentage) }.toMutableList()
  )
}

fun List<JsonGradeScale>.countriesAndGradeScales() : List<CountriesAndGradeScales> {
  val countries = mutableMapOf<String, MutableList<String>>()

  this.forEach { jSon ->
    countries.getOrPut(jSon.country){ mutableListOf()}.add(jSon.gradeScaleName)
  }
  return countries.map { (country, grades) ->
    CountriesAndGradeScales(country, grades)
  }
}

@Serializable
data class JsonGradeScale(
  val country: String,
  val gradeScaleName: String,
  val grades: List<SimpleGrade>
)

@Serializable
data class SimpleGrade(
  val namedGrade: String,
  val percentage: Double
)