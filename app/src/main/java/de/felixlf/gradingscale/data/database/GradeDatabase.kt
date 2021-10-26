package de.felixlf.gradingscale.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.WeightedGrade

@Database(
    entities = [Grade::class, GradeScale::class, GWCalculation::class, WeightedGrade::class],
    version = 2
)
@TypeConverters(GradeTypeConverters::class)
abstract class GradeDatabase : RoomDatabase() {
  abstract fun gradesInScaleDao(): GradesInScaleDao
  abstract fun gradeCalculationDao(): GradeCalculationDao
}