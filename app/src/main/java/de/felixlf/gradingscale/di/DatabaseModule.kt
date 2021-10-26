package de.felixlf.gradingscale.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.felixlf.gradingscale.data.database.GradeCalculationDao
import de.felixlf.gradingscale.data.database.GradeDatabase
import de.felixlf.gradingscale.data.database.GradesInScaleDao
import de.felixlf.gradingscale.data.remote.RemoteDatabase
import de.felixlf.gradingscale.data.remote.RemoteDatabaseImpl
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.data.repository.GiSRepositoryImpl
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

private const val DATABASE_NAME = "grade_in_scale_database"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideGradeDatabase(@ApplicationContext appContext: Context): GradeDatabase {
    return Room.databaseBuilder(
      appContext.applicationContext,
      GradeDatabase::class.java,
      DATABASE_NAME
    ).fallbackToDestructiveMigration()
      .createFromAsset("database/grade_in_scale_database")
      .build()
  }

  @Provides
  @Singleton
  fun provideGradesInScaleDao(gradeDatabase: GradeDatabase): GradesInScaleDao {
    return gradeDatabase.gradesInScaleDao()
  }


  @Provides
  @Singleton
  fun provideGISRepository(gradesInScaleDao: GradesInScaleDao): GiSRepository {
    return GiSRepositoryImpl(gradesInScaleDao, Dispatchers.IO)
  }

  @Provides
  @Singleton
  fun provideGradeCalculationDao(gradeDatabase: GradeDatabase): GradeCalculationDao {
    return gradeDatabase.gradeCalculationDao()
  }

  @Provides
  @Singleton
  fun providesRemoteDatabase(): RemoteDatabase {
    return RemoteDatabaseImpl(
      Firebase.database.also { it.setPersistenceEnabled(true) }
    )
  }



}















