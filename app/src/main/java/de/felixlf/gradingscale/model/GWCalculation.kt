package de.felixlf.gradingscale.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class GWCalculation(
  var name: String = "",
  @PrimaryKey var id: UUID = UUID.randomUUID()
)
