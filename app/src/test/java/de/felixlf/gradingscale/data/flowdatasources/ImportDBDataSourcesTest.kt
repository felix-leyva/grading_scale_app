package de.felixlf.gradingscale.data.flowdatasources

import de.felixlf.gradingscale.TestCoroutineRule
import de.felixlf.gradingscale.data.remote.FakeRemoteDatabase
import de.felixlf.gradingscale.data.remote.FakeRemoteDbData
import de.felixlf.gradingscale.data.repository.FakeRepository
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.data.repository.RemoteSyncRepository
import de.felixlf.gradingscale.data.repository.RemoteSyncRepositoryImpl
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.model.converters.generateGradeInScaleFromRemoteGradeScale
import de.felixlf.gradingscale.ui.importremote.recyclerview.ImportRemoteListItem
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ImportDBDataSourcesTest {

  @ExperimentalCoroutinesApi
  @get: Rule
  val coroutineRule = TestCoroutineRule()

  lateinit var giSRepository: GiSRepository
  lateinit var flowDataSource: FlowDataSource
  lateinit var mockScale: MockScale
  lateinit var remoteDatabase: FakeRemoteDatabase
  lateinit var remoteSyncRepository: RemoteSyncRepository
  lateinit var importDBDataSources: ImportDBDataSources

  val fakeRemoteData = FakeRemoteDbData.generateFakeGradesAndCountries()

  @Before
  fun setup() {

    mockScale = MockScale()
    giSRepository = FakeRepository(mockScale)
    flowDataSource = FlowDataSource(giSRepository)
    remoteDatabase = FakeRemoteDatabase()
    remoteSyncRepository = RemoteSyncRepositoryImpl(remoteDatabase, giSRepository)
    importDBDataSources = ImportDBDataSources(remoteSyncRepository, giSRepository)

  }


  @Test
  fun getListOfScales_returnsAListOfTheRemoteDatabaseScalesNames() = runBlockingTest {
    val remoteGrades = importDBDataSources.getListOfScales()
    val listOfNames = fakeRemoteData.map { countriesGrades ->
      val countryName = countriesGrades.countryName
      countriesGrades.gradeScalesNames.map { "${countryName}_${it}" }
    }.flatten()
    assertThat(remoteGrades, equalTo(listOfNames))
  }

  @ExperimentalCoroutinesApi
  @Test
  fun getListOfCountries_returnsAListOfCountries() = coroutineRule.runBlockingTest {
    val remoteGradeF = importDBDataSources.getListOfCountries()
    val remoteGrades = remoteGradeF.first()
    val listOfCountries = fakeRemoteData.map { countriesGrades ->
      countriesGrades.countryName
    }
    assertThat(remoteGrades, equalTo(listOfCountries))

  }

  @Test
  fun getRemoteGradeScaleWithName() = runBlockingTest {
    val country = fakeRemoteData[1].countryName
    val grade = fakeRemoteData[1].gradeScalesNames[0]
    val name = "${country}_${grade}"
    val remoteGradeScale = importDBDataSources.getRemoteGradeScaleWithName(name)
    assertThat(remoteGradeScale, equalTo(FakeRemoteDbData.generateFakeMiniGradeScale()))
  }

  @Test
  fun checkIsNameDuplicated() = runBlockingTest {
    val country = fakeRemoteData[1].countryName
    val grade = fakeRemoteData[1].gradeScalesNames[0]
    val name = "${country}_${grade}"
    val remoteGradeScale = importDBDataSources.getRemoteGradeScaleWithName(name)

    val giSInMockScale = mockScale.gradesInScalesList
      .firstOrNull { it.gradeScale.gradeScaleName == remoteGradeScale.gradeScaleName }

    val existsInMockScale = giSInMockScale != null
    var existInRepo = importDBDataSources.isNameDuplicated(mockScale.gradeScaleList,
      remoteGradeScale.gradeScaleName)

    assertEquals(existsInMockScale, existInRepo)
    assertTrue(existInRepo)
    existInRepo = importDBDataSources.isNameDuplicated(mockScale.gradeScaleList, "fakeName")

    assertFalse(existInRepo)

  }

  @Test
  fun checkFindUntilNoRepeated() {
    val fakeGradeScales = listOf(
      GradesInScale(GradeScale(gradeScaleName = "Test"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(1)"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(2)"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(3)"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(4)"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(5)"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(6)"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(7)"), mutableListOf()),
      GradesInScale(GradeScale(gradeScaleName = "Test(8)"), mutableListOf()),
    )
    val validName =
      importDBDataSources.findUntilNoRepeated(fakeGradeScales.map { it.gradeScale }, "Test")
    assertThat(validName, equalTo("Test(9)"))

  }


  @Test
  fun importIntoDbRemoteGradeScale_checksIfData() = runBlockingTest {
    val country = fakeRemoteData[1].countryName
    val grade = fakeRemoteData[1].gradeScalesNames[0]
    val name = "${country}_${grade}"
    val remoteGradeScale = importDBDataSources.getRemoteGradeScaleWithName(name)
    val remoteGradesInScale = generateGradeInScaleFromRemoteGradeScale(remoteGradeScale)


    importDBDataSources.importIntoDbRemoteGradeScale(remoteGradeScale)
    val gradeScales = giSRepository.getGradesFromScaleFlow().first().last()

    remoteGradesInScale.gradeScale.apply {
      assertEquals(gradeScaleName, gradeScales.gradeScale.gradeScaleName)
    }

    remoteGradesInScale.grades.apply {
      forEachIndexed { index, grade ->
        assertEquals(grade, gradeScales.grades[index])
      }
    }

  }

  @Test
  fun flowOfListOfScales_returnsAFlowOfListOfScales() = runBlockingTest {
    val remoteGradesFlow = importDBDataSources.flowOfListOfScales()
    val listOfScales = fakeRemoteData.map { countriesGrades ->
      val countryName = countriesGrades.countryName
      countriesGrades.gradeScalesNames.map { "${countryName}_${it}" }
    }.flatten()

    val remoteGrades = remoteGradesFlow.first()
    assertThat(remoteGrades, equalTo(listOfScales))

  }

  @Test
  fun filteredListOfScales_returnsAFilteredList() = runBlockingTest {
    val selectedCountry = MutableStateFlow("")
    val filteredListsOfScalesFlow = importDBDataSources.filteredListOfScales(selectedCountry)
    var filteredListOfScales = filteredListsOfScalesFlow.first()

    val listOfScales = fakeRemoteData.map { countriesGrades ->
      val countryName = countriesGrades.countryName
      countriesGrades.gradeScalesNames.map { "${countryName}_${it}" }
    }.flatten()

    assertThat(filteredListOfScales, equalTo(listOfScales))

    val countryFilter = "Germany"
    selectedCountry.value = countryFilter
    filteredListOfScales = filteredListsOfScalesFlow.first()
    val listFiltered = listOfScales.filter { it.substringBefore("_") == countryFilter }

    assertThat(filteredListOfScales, equalTo(listFiltered))


  }

  @Test
  fun filteredImportRemoteListItems_returnsAFilteredList() = runBlockingTest {
    val countryFilter = "Mexico"

    val selectedCountry = MutableStateFlow(countryFilter)
    val filteredListsOfScalesFlow =
      importDBDataSources.filteredImportRemoteListItems(selectedCountry)
    var filteredListOfScales = filteredListsOfScalesFlow.first()


    val listFiltered = filteredListOfScales.filter { remoteListItem ->
      when (remoteListItem) {
        is ImportRemoteListItem.ImportRemoteHeader -> true
        is ImportRemoteListItem.ImportRemoteItem -> {
          remoteListItem.country == countryFilter || countryFilter == ""
        }
      }
    }

    assertThat(filteredListOfScales, equalTo(listFiltered))

  }


}