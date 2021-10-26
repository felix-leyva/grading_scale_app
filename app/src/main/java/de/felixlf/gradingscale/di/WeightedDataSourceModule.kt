package de.felixlf.gradingscale.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import de.felixlf.gradingscale.data.database.GradeCalculationDao
import de.felixlf.gradingscale.data.repository.GradeWCalculationsRepository
import de.felixlf.gradingscale.data.repository.GradeWCalculationsRepositoryImpl
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
object WeightedDataSourceModule {

  @Provides
  @ViewModelScoped
  fun providesGradeWCalculationRepository(gradeCalculationDao: GradeCalculationDao): GradeWCalculationsRepository {
    return GradeWCalculationsRepositoryImpl(gradeCalculationDao, Dispatchers.IO)
  }

}