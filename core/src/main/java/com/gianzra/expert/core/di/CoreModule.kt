package com.gianzra.expert.core.di

import android.annotation.SuppressLint
import androidx.room.Room
import com.gianzra.expert.core.data.MovieAppRepository
import com.gianzra.expert.core.data.source.local.LocalDataSource
import com.gianzra.expert.core.data.source.local.room.MovieDatabase
import com.gianzra.expert.core.data.source.remote.RemoteDataSource
import com.gianzra.expert.core.data.source.remote.network.ApiService
import com.gianzra.expert.core.domain.repository.IMovieAppRepository
import com.gianzra.expert.core.utils.AppExecutors
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    factory {
        get<MovieDatabase>().movieDao()
    }
    single {
        Room.databaseBuilder(
            androidContext(),
            MovieDatabase::class.java,
            "movie.db"
        ).fallbackToDestructiveMigration().build()
    }
}

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            )
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/discover/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create(ApiService::class.java)
    }
}

@SuppressLint("VisibleForTests")
val repositoryModule = module {
    single {
        LocalDataSource(get())
    }
    single {
        RemoteDataSource(get())
    }
    factory {
        AppExecutors()
    }
    single<IMovieAppRepository> {
        MovieAppRepository(
            get(),
            get(),
            get()
        )
    }
}