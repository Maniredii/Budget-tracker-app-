package fm.mrc.budget

import android.app.Application
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import android.util.Log

class BudgetApplication : Application() {
    
    companion object {
        lateinit var instance: BudgetApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupErrorHandling()
    }

    private fun setupErrorHandling() {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            handleFatalException(throwable)
        }
    }

    private fun handleFatalException(throwable: Throwable) {
        throwable.printStackTrace()
        showToast("An unexpected error occurred. Please restart the app.")
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("BudgetApplication", "Caught exception: ${throwable.localizedMessage}", throwable)
    }
} 