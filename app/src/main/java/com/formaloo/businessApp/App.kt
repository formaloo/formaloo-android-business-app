package com.formaloo.businessApp


import android.app.Application
import android.util.Log
import com.formaloo.businessApp.di.appComponent

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.lang.Exception
import android.database.CursorWindow;
import java.lang.reflect.Field;

open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        configureDi()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
//            if (DEBUG_MODE) {
//                e.printStackTrace()
//            }
        }
    }

    //     CONFIGURATION ---
    open fun configureDi() =
        startKoin {
            androidContext(this@App)
            // your modules
            modules(provideComponent())

        }

    //     PUBLIC API ---
    open fun provideComponent() = appComponent

}
