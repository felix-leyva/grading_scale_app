package de.felixlf.gradingscale.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.felixlf.gradingscale.TestCoroutineRule
import de.felixlf.gradingscale.data.remote.FakeRemoteDatabase
import de.felixlf.gradingscale.data.remote.FakeRemoteDbData
import de.felixlf.gradingscale.model.converters.generateGradeInScaleFromRemoteGradeScale
import de.felixlf.gradingscale.model.converters.generateRemoteGradesFromGradeInScale
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RemoteSyncRepositoryImplTest {

  @get:Rule
  val testCoroutineRule = TestCoroutineRule()

  @get:Rule
  val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()


  lateinit var remoteSyncRepository: RemoteSyncRepository
  lateinit var giSRepository: FakeRepository
  lateinit var mockScale: MockScale
  lateinit var remoteDatabase: FakeRemoteDatabase

  @Before
  fun setUp() {
    mockScale = MockScale()
    giSRepository = FakeRepository(mockScale)
    remoteDatabase = FakeRemoteDatabase()
    remoteSyncRepository = RemoteSyncRepositoryImpl(remoteDatabase, giSRepository)
  }


  @Test
  fun importGradeScaleIntoLocalDb_AddsGradeInScale() = runBlockingTest{
    val remoteGradeScaleToAdd = FakeRemoteDbData.generateFakeGradeScaleList()[0]
    remoteSyncRepository.importGradeScaleIntoLocalDb(remoteGradeScaleToAdd)
    val remoteGradeScaleToGiS = generateGradeInScaleFromRemoteGradeScale(remoteGradeScaleToAdd)
    assertThat(giSRepository.gradesInScaleList.last().gradeScale.gradeScaleName, `is`(equalTo(remoteGradeScaleToGiS.gradeScale.gradeScaleName)))

    for( pos: Int in 1 until giSRepository.gradesInScaleList.last().grades.size){
      val gradeInRepo = giSRepository.gradesInScaleList.last().grades[pos]
      val gradeToAdd = remoteGradeScaleToGiS.grades[pos]
      assertThat(gradeInRepo, equalTo(gradeToAdd))
    }
  }

  @Test
  fun generateGradeInScale_andBackToRemoteGrade_isTheSame(){
    val remoteGradeScaleToAdd = FakeRemoteDbData.generateFakeGradeScaleList()[0]
    val remoteGradeScaleToGiS = generateGradeInScaleFromRemoteGradeScale(remoteGradeScaleToAdd)
    val convertedRemoteGradeBackToGiS = generateRemoteGradesFromGradeInScale(remoteGradeScaleToGiS, "Mexico")
    assertThat(remoteGradeScaleToAdd, equalTo(convertedRemoteGradeBackToGiS))
  }


}















