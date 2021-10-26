package de.felixlf.gradingscale.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import de.felixlf.gradingscale.data.remote.RemoteDatabase
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.data.repository.RemoteSyncRepository
import de.felixlf.gradingscale.data.repository.RemoteSyncRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
object ImportRemoteModule {

//  @Provides
//  @ViewModelScoped
//  fun providesRemoteDatabase(): RemoteDatabase {
//    return RemoteDatabaseImpl(
//      Firebase.database
//    )
//  }

  @Provides
  @ViewModelScoped
  fun providesRemoteSyncRepository(
    remoteDatabase: RemoteDatabase,
    giSRepository: GiSRepository,
  ): RemoteSyncRepository {
    return RemoteSyncRepositoryImpl(remoteDatabase, giSRepository)
  }

}