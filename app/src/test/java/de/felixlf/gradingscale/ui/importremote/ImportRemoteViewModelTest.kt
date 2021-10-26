package de.felixlf.gradingscale.ui.importremote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.felixlf.gradingscale.TestCoroutineRule
import de.felixlf.gradingscale.data.flowdatasources.FlowDataSource
import de.felixlf.gradingscale.data.flowdatasources.ImportDBDataSources
import de.felixlf.gradingscale.data.remote.FakeRemoteDatabase
import de.felixlf.gradingscale.data.remote.FakeRemoteDbData
import de.felixlf.gradingscale.data.repository.FakeRepository
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.data.repository.RemoteSyncRepository
import de.felixlf.gradingscale.data.repository.RemoteSyncRepositoryImpl
import de.felixlf.gradingscale.model.converters.generateGradeInScaleFromRemoteGradeScale
import de.felixlf.gradingscale.ui.importremote.recyclerview.ImportRemoteListItem
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

//https://blog.mindorks.com/unit-testing-viewmodel-with-kotlin-coroutines-and-livedata
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ImportRemoteViewModelTest {

  @get:Rule
  val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

  @get:Rule
  val testCoroutineRule = TestCoroutineRule()

  lateinit var giSRepository: GiSRepository
  lateinit var flowDataSource: FlowDataSource
  lateinit var mockScale: MockScale
  lateinit var remoteDatabase: FakeRemoteDatabase
  lateinit var remoteSyncRepository: RemoteSyncRepository
  lateinit var importDBDataSources: ImportDBDataSources
  lateinit var vM: ImportRemoteViewModel

  val fakeRemoteData = FakeRemoteDbData.generateFakeGradesAndCountries()

  @Before
  fun setup() {

    mockScale = MockScale()
    giSRepository = FakeRepository(mockScale)
    flowDataSource = FlowDataSource(giSRepository)
    remoteDatabase = FakeRemoteDatabase()
    remoteSyncRepository = RemoteSyncRepositoryImpl(remoteDatabase, giSRepository)
    importDBDataSources = ImportDBDataSources(remoteSyncRepository, giSRepository)
    vM =
      ImportRemoteViewModel(importDBDataSources, testCoroutineRule.testCoroutineDispatcher, testCoroutineRule.testCoroutineDispatcher)
  }


  @Test
  fun getListOfCountries() = testCoroutineRule.runBlockingTest {
    val listOfCountriesLD = vM.listOfCountries.value
    val listOfCountries = fakeRemoteData.map { countriesGrades ->
      countriesGrades.countryName
    }
    assertThat(listOfCountriesLD, equalTo(listOfCountries))
  }

  @Test
  fun selectCountry_returnsAFilteredList() = testCoroutineRule.runBlockingTest {
    val countryFilter = "Germany"

    vM.selectCountry(countryFilter)
    advanceTimeBy(1)
    val listOfCountriesAndGradeScales: List<ImportRemoteListItem>? =
      vM.listOfCountriesAndGradeScales.value

    val fakeList = fakeRemoteData
      .filter { it.countryName == countryFilter || countryFilter == "" }
      .map {
        val country = it.countryName
        it.gradeScalesNames.map {
          ImportRemoteListItem.ImportRemoteItem(country, it)
        }
      }.flatten()

    assertThat(listOfCountriesAndGradeScales?.get(0), `is`(ImportRemoteListItem.ImportRemoteHeader))
    listOfCountriesAndGradeScales?.forEachIndexed { index, importRemoteListItem ->
      if (index > 0)
        assertThat(importRemoteListItem, equalTo(fakeList[index - 1]))
    }


  }

  @Test
  fun selectGradeAndScale() = testCoroutineRule.runBlockingTest{
    var country = fakeRemoteData[1].countryName
    var grade = fakeRemoteData[1].gradeScalesNames[0]
    var name = "${country}_${grade}"
    vM.selectGradeAndScale(name)
    var remoteGradeScale = vM.selectedGradeAndScale.first()
    Assert.assertThat(remoteGradeScale,
      IsEqual.equalTo(FakeRemoteDbData.generateFakeMiniGradeScale()))

    country = fakeRemoteData[0].countryName
    grade = fakeRemoteData[0].gradeScalesNames[0]
    name = "${country}_${grade}"
    vM.selectGradeAndScale(name)
    remoteGradeScale = vM.selectedGradeAndScale.first()
    Assert.assertThat(remoteGradeScale,
      IsEqual.equalTo(FakeRemoteDbData.generateFakeGradeScale()))



  }

  @Test
  fun importSelectedGradeAndScale() = testCoroutineRule.runBlockingTest {
    val country = fakeRemoteData[1].countryName
    val grade = fakeRemoteData[1].gradeScalesNames[0]
    val name = "${country}_${grade}"
    vM.selectGradeAndScale(name)

    vM.importSelectedGradeAndScale()

    val remoteGradeScaleName = vM.selectedGradeAndScaleName
    val remoteGradeScaleDef = async{ vM.selectedGradeAndScale.value }
    val remoteGradeScale = remoteGradeScaleDef.await()
    advanceUntilIdle()
    val remoteGradesInScale = generateGradeInScaleFromRemoteGradeScale(remoteGradeScale)
    val gradeScales = giSRepository.getGradesFromScaleFlow().first().last()

    remoteGradesInScale.gradeScale.apply {
      assertThat(gradeScaleName, equalTo(gradeScales.gradeScale.gradeScaleName))
    }

    remoteGradesInScale.grades.apply {
      forEachIndexed { index, grade ->
        assertThat(grade, equalTo(gradeScales.grades[index]))
      }
    }


  }

}