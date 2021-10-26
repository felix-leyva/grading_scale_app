package de.felixlf.gradingscale


//class GSViewModelTest {
//
////    private lateinit var cVM: GSViewModel
////    private lateinit var log: Log
////
////    private lateinit var instrumentationContext: Context
////    private lateinit var giSRepImpl: GiSRepositoryImpl
////
////
////    @Before
////    fun setup() {
////        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
////
////
////    }
//
//
////    @Test
////    fun checkGradeNameScale() {
////        assertThat(cVM.gradeScales.value?.get(0)?.gradeScaleName, `is`("E1+"))
////    }
////
////    @Test
////    fun checkGetGradeNamePercentage() {
////        assertThat(cVM.gradesInScale.getGradeName(.72), `is`("E3"))
////    }
//
////    @Test
////    fun checkGetGradeNamePosPercentage() {
////        assertThat(cVM.gradesInScale.getGradeNamePos(1.10), `is`(0))
////    }
////
////    @Test
////    fun checkGetPercentage() {
////        assertThat(cVM.gradesInScale.getPercentage("G45"), `is`(0.0))
////    }
////
////    @Test
////    fun checkGetPointsPercentage() {
////        cVM.gradesInScale.totalPoints = 50.0
////        assertThat(cVM.gradesInScale.getPoints("E4-"), `is`(25.0))
////    }
////
////    @Test
////    fun checkGradeByName() {
////        assertThat(cVM.gradesInScale.gradeByName("E3"), `is`(cVM.gradesInScale.grades[7]))
////    }
////
////    @Test
////    fun checkGradeByPercentage() {
////        assertThat(cVM.gradesInScale.gradeByPercentage(0.99).namedGrade, `is`("E1+"))
////    }
////    @Test
////    fun checkGradeByPointedGrades() {
////        assertThat(cVM.gradesInScale.gradeByPointedGrade(13.0).namedGrade, `is`("E1-"))
////    }
////
////    @Test
////    fun checkGradeByNamed2() {
////        assertThat(cVM.gradesInScale.grade2ByName("6").namedGrade, `is`("G5+"))
////    }
////
////    @Test
////    fun checkGrade2Pos() {
////        assertThat(cVM.gradesInScale.getGrade2NamePos(.49), `is` (12))
////    }
//
//}