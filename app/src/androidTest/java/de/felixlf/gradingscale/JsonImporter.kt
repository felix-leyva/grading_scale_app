package de.felixlf.gradingscale

import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.felixlf.gradingscale.data.remote.RemoteDatabaseImpl
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

//https://bezkoder.com/java-android-read-json-file-assets-gson/
//https://kotlinlang.org/docs/reference/serialization.html
@RunWith(JUnit4::class)
class JsonImporter {


  lateinit var database: FirebaseDatabase
  lateinit var repository: RemoteDatabaseImpl
  val countriesAndGradesPath = "countriesAndGradesTest2"
  val gradeScalesPath = "gradeScalesTest2"
  var setupFinished = false


  @ExperimentalCoroutinesApi
  @Before
  fun setUp() {
    database = Firebase.database
    repository = RemoteDatabaseImpl(database)
    repository.countriesAndGradesPath = countriesAndGradesPath
    repository.gradeScalesPath = gradeScalesPath

  }


  @Test
  fun testReadingScale() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    val jsonString = readStringFromFile("scales.json", appContext)
    val listGrades = jsonString?.let { Json.decodeFromString<List<Grade>>(it) }

    println(listGrades)

  }


  @Test
  fun uploadListsInFB() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val jsonString = readStringFromFile("grade_in_scale.json", appContext)
    val listGrades = jsonString?.let { Json.decodeFromString<List<JsonGradeScale>>(it) }

    val remoteGradeScales = listGrades?.map { it.toGradeScaleRemote() }
    val countriesAndGradeScales = listGrades?.countriesAndGradeScales()

    val ref = database.reference
    runBlocking(Dispatchers.IO) {
      countriesAndGradeScales?.forEach {
        delay(200)
        ref.child("countriesAndGrades")
          .child(it.countryName)
          .setValue(it.gradeScalesNames)
        delay(200)
      }

      remoteGradeScales?.forEach { gS ->
        delay(200)
        ref.child("gradeScales")
          .child("${gS.country}_${gS.gradeScaleName}")
          .setValue(gS)
        delay(200)
      }
    }


  }

  @Test
  fun testReadingScales() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    val jsonString = readStringFromFile("grade_in_scale.json", appContext)
    val listGrades = jsonString?.let { Json.decodeFromString<List<JsonGradeScale>>(it) }


    val gIS = listGrades?.map { simpleGradeToGiS(it) }

    val stringfromGiS = Json.encodeToString(listGrades)


  }

}