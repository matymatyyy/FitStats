package com.app.gimnasio

import android.app.Application
import com.app.gimnasio.data.local.GimnasioDatabase

class GimnasioApplication : Application() {
    val database: GimnasioDatabase by lazy {
        GimnasioDatabase.getInstance(this).also { db ->
            db.seedIfNeeded(this)
        }
    }
}
