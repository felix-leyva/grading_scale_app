package de.felixlf.gradingscale.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RemoteDatabaseImplTest {


  lateinit var database: FirebaseDatabase
  lateinit var repository: RemoteDatabaseImpl
  val countriesAndGradesPath = "countriesAndGradesTest"
  val gradeScalesPath = "gradeScalesTest"
  var setupFinished = false


  @ExperimentalCoroutinesApi
  @Before
  fun setUp() {
    database = Firebase.database
    repository = RemoteDatabaseImpl(database)
    repository.countriesAndGradesPath = countriesAndGradesPath
    repository.gradeScalesPath = gradeScalesPath
    setCountriesAndGradeScalesWithFirebase()
    setGradeScalesWithFirebase()

  }

  @ExperimentalCoroutinesApi
  @Test
  fun getCountriesAndGrades_returnsAListOfCountriesAndGradeScales() = runBlocking() {
    val countriesAndGradeScales = repository.getCountriesAndGrades()
    assertThat(countriesAndGradeScales, equalTo(FakeRemoteDbData.generateFakeGradesAndCountries()))
  }

  @ExperimentalCoroutinesApi
  @Test
  fun getGradescaleWithName_returnsAnSpecificGradeScale() = runBlocking {
    val gScales = FakeRemoteDbData.generateFakeGradeScaleList()
    gScales.forEach { gS ->
      val gradeScaleFB = repository.getGradescaleWithName("${gS.country}_${gS.gradeScaleName}")
      assertThat(gradeScaleFB, equalTo(gS))
    }
  }

  @ExperimentalCoroutinesApi
  @Test
  fun countriesAndGradesFlow_returnsAFlowListOfCountriesAndGradeScales() = runBlocking {
    val countriesAndGradeScalesFlow = repository.countriesAndGradesFlow()
    val countriesAndGradeScalesDef = async { countriesAndGradeScalesFlow.first() }
    val countriesAndGradeScales = countriesAndGradeScalesDef.await()

    assertThat(countriesAndGradeScales, equalTo(FakeRemoteDbData.generateFakeGradesAndCountries().sortedBy { it.countryName }))

  }

  @ExperimentalCoroutinesApi
  @Test
  fun getGradescaleWithNameFlow_returnsAnSpecificGradeScale() = runBlocking {
    val gScales = FakeRemoteDbData.generateFakeGradeScaleList()

    gScales.forEach { gS ->
      val gradeScaleFBDef = async { repository.gradescaleWithNameFlow("${gS.country}_${gS.gradeScaleName}").drop(1).first() }
      val gradeScaleFB =  gradeScaleFBDef.await()
      assertThat(gradeScaleFB, equalTo(gS))
    }

  }



  fun setCountriesAndGradeScalesWithFirebase() {
    val ref = database.reference
    val countriesAndGradeScales = FakeRemoteDbData.generateFakeGradesAndCountries()
    val mapCountriesAndGradeScales = countriesAndGradeScales.map { countries ->
      countries.countryName to countries.gradeScalesNames
    }.toMap()
    runBlocking(Dispatchers.IO) {
      delay(1000)
      ref.child(countriesAndGradesPath).setValue(mapCountriesAndGradeScales)
    }
  }


  @ExperimentalCoroutinesApi
  fun setGradeScalesWithFirebase() {
    val ref = database.reference
    val gradeScales = FakeRemoteDbData.generateFakeGradeScaleList()
    runBlocking(Dispatchers.IO) {
      gradeScales.forEach { gS ->
        delay(200)
        ref.child(gradeScalesPath)
          .child("${gS.country}_${gS.gradeScaleName}")
          .setValue(gS)
        delay(200)
      }
    }
  }


}