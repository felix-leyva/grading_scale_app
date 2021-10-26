package de.felixlf.gradingscale.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Entity
data class Grade(
  @ColumnInfo(name = "namedGrade") var namedGrade: String = "",
  @ColumnInfo(name = "percentage") var percentage: Double = 0.0, //0 to 1
  @ColumnInfo(name = "pointedGrade") var pointedGrade: Double = 0.0,
  @ColumnInfo(name = "namedGrade2") var namedGrade2: String = "",
  @ColumnInfo(name = "nameOfScale") var nameOfScale: String = "",
  @Contextual @PrimaryKey val id: UUID = UUID.randomUUID()
) {
  override fun equals(other: Any?): Boolean {
    return (other is Grade &&
        namedGrade == other.namedGrade &&
        percentage == other.percentage &&
        pointedGrade == other.pointedGrade &&
        namedGrade2 == other.namedGrade2 &&
        nameOfScale == other.nameOfScale)
  }

  override fun hashCode(): Int {
    var result = namedGrade.hashCode()
    result = 31 * result + percentage.hashCode()
    result = 31 * result + pointedGrade.hashCode()
    result = 31 * result + namedGrade2.hashCode()
    result = 31 * result + nameOfScale.hashCode()
    return result
  }


}