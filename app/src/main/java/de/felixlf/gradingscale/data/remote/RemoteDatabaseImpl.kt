package de.felixlf.gradingscale.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.felixlf.gradingscale.model.firebase.CountriesAndGradeScales
import de.felixlf.gradingscale.model.firebase.GradeRemote
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import de.felixlf.gradingscale.utils.tryCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class RemoteDatabaseImpl @Inject constructor(
  val db: FirebaseDatabase,
) : RemoteDatabase {

  var countriesAndGradesPath = "countriesAndGrades"
  var gradeScalesPath = "gradeScales"

  @ExperimentalCoroutinesApi
  override suspend fun getCountriesAndGrades(): List<CountriesAndGradeScales> {
    val ref = db.getReference(countriesAndGradesPath)
    var countriesAndGradeScales = mutableListOf<CountriesAndGradeScales>()
    try {
      val countriesMaps = ref.get().await().value
      countriesAndGradeScales = castIntoList(countriesMaps)
    } catch (ex: Exception) {
      ex.printStackTrace()
    }
    return countriesAndGradeScales.toList()
  }


  override suspend fun getGradescaleWithName(name: String): GradeScaleRemote {
    val ref = db.getReference(gradeScalesPath)
    val testScaleRef = ref.child(name)
    val waitingGradeScale = GradeScaleRemote(LoadingStatus.LOADING.name,
      grades = mutableListOf(GradeRemote()))

    return try {
      withTimeout(10000) {
        testScaleRef.get().await().getValue(GradeScaleRemote::class.java) ?: waitingGradeScale
      }
    } catch (ex: Exception) {
      ex.printStackTrace()
      GradeScaleRemote(LoadingStatus.ERROR_CONNECTION.name)
    }

  }

  @ExperimentalCoroutinesApi
  override fun countriesAndGradesFlow(): Flow<List<CountriesAndGradeScales>> = callbackFlow {
    val ref = db.getReference(countriesAndGradesPath)

    val listener = object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        try {
          val countriesMaps = snapshot.value
          castIntoList(countriesMaps).sortedBy { it.countryName }
        } catch (ex: Exception) {
          ex.printStackTrace()
          emptyList()
        }.let { offer(it) }

      }
      override fun onCancelled(error: DatabaseError) {
        error.toException().printStackTrace()
        offer((listOf(CountriesAndGradeScales(LoadingStatus.ERROR_CONNECTION.name))))
      }
    }

    ref.addValueEventListener(listener)
    awaitClose { ref.removeEventListener(listener) }
  }


  @ExperimentalCoroutinesApi
  override fun gradescaleWithNameFlow(name: String): Flow<GradeScaleRemote> = flow {
    val waitingGradeScale = GradeScaleRemote(country = LoadingStatus.LOADING.name,
      gradeScaleName = LoadingStatus.LOADING.name,
      grades = mutableListOf(GradeRemote()))
    emit(waitingGradeScale)

    val gradeScaleRemote = getGradescaleWithName(name)
    emit(gradeScaleRemote)
  }


  private fun castIntoList(countriesMaps: Any?): MutableList<CountriesAndGradeScales> {
    val countriesAndGradeScales: MutableList<CountriesAndGradeScales> = mutableListOf()
    countriesMaps.tryCast<HashMap<String, List<String>>> {
      forEach { (countryName, grades) ->
        countriesAndGradeScales.add(CountriesAndGradeScales(countryName, grades))
      }
    }
    return countriesAndGradeScales
  }


}