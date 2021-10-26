package de.felixlf.gradingscale.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class GradeScale(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "gradeScaleName") var gradeScaleName: String,
    @ColumnInfo(name = "gradeScaleId") val gradeScaleId: String = gradeScaleName,
)


