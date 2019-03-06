package com.rzahr.quicktools.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.extensions.addWithId
import com.rzahr.quicktools.extensions.setFullScreen

class QuickSplashScreenActivity(private val layout: Int, private val tutorialActivity: Class<Any>?, private val mainActivity: Class<Any>?, private val delay: Long = 1500)  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()

        setContentView(layout)

        if (QuickInjectable.quickPref().getBoolean("SHOW_ON_BOARDING")) {

            Handler().postDelayed({
                val i = Intent(this@QuickSplashScreenActivity, mainActivity)
                startActivity(i)
                finish()
            }, 1500)
        }

        else {

            true.addWithId("SHOW_ON_BOARDING")
            Handler().postDelayed({
                val i = Intent(this@QuickSplashScreenActivity, tutorialActivity)
                startActivity(i)
                finish()
            }, delay)
        }
    }
}