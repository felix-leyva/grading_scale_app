package de.felixlf.gradingscale.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class WeightedGrade(
  var percentage: Double = 0.0,
  var weight: Double = 0.0,
  var scaleId: String = "",
  @PrimaryKey val id: UUID = UUID.randomUUID()
) {
  override fun equals(other: Any?): Boolean {
    return (other is WeightedGrade &&
        (this.percentage == other.percentage) &&
        (this.weight == other.weight) &&
        (this.scaleId == other.scaleId))
  }

  override fun hashCode(): Int {
    var result = percentage.hashCode()
    result = 31 * result + weight.hashCode()
    result = 31 * result + scaleId.hashCode()
    return result
  }


}
