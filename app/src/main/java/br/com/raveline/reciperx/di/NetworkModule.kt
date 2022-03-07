package br.com.raveline.reciperx.di

import br.com.raveline.reciperx.listeners.NetworkListeners
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkListener():NetworkListeners = NetworkListeners()
}