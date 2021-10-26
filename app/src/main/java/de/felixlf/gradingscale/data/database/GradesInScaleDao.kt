package de.felixlf.gradingscale.data.database

import androidx.room.*
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import kotlinx.coroutines.flow.Flow

//https://medium.com/androiddevelopers/database-relations-with-room-544ab95e4542
@Dao
interface GradesInScaleDao {
    //DAO for grades from combined grades and scales
    @Transaction
    @Query("SELECT * FROM gradeScale")
    fun getGradesFromScaleFlow(): Flow<List<GradesInScale>>

    @Query("DELETE FROM grade WHERE nameOfScale=(:gradeScaleId)")
    fun deleteGradesFromScale(gradeScaleId: String)

    @Query("DELETE FROM gradeScale WHERE gradeScaleID=(:gradeScaleId)")
    fun deleteGradeScalesFromScale(gradeScaleId: String)

    @Update
    fun updateGrade(grade: Grade)

    @Insert
    fun addGrade(grade: Grade)

    @Delete
    fun deleteGrade(grade: Grade)

    //DAO for Grade Scales
    @Query("SELECT * FROM gradescale")
    fun getGradeScales(): List<GradeScale?>

    @Update
    fun updateGradeScale(gradeScale: GradeScale)

    @Insert
    fun addScale(gradeScale: GradeScale)
}