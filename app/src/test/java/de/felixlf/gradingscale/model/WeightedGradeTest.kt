package de.felixlf.gradingscale.model

import org.junit.Test

class WeightedGradeTest {
  private val wG1 = WeightedGrade(0.5, 5.0)
  private val wG2 = WeightedGrade(0.5, 5.0)
  private val wG3 = WeightedGrade(0.6, 5.0)


  @Test
  fun equal_returnsTrueByIdenticalWGrades(){
    assert(wG1.equals(wG2) )
  }

  @Test
  fun equal_returnsFalseByNonIdenticalWGrades(){
    assert(!wG1.equals(wG3) )
  }



}