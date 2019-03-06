@file:Suppress("unused")

package com.rzahr.quicktools.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.rzahr.quicktools.QuickRapidIdler

fun Activity.setFullScreen() {

    // set to full screen
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun ConstraintLayout.addAnimationOnLayout(layoutId: Int, activity: Activity?) {

    try {

        android.os.Handler().postDelayed({

            val constraintSet = ConstraintSet()

            activity?.let { constraintSet.clone(it, layoutId) }

            activity?.let { TransitionManager.beginDelayedTransition(it.findViewById(android.R.id.content), null) }

            constraintSet.applyTo(this)
        }, 250)
    }
    catch(e: Exception){}
}

/**
 * hiding keyboard in a fragment
 */
fun Fragment.hideKeyboard() {

    view?.let { activity!!.hideKeyboard(it) }
}

fun RecyclerView.initializeLinear(linearLayoutManager: LinearLayoutManager, context: Context) {

    this.layoutManager = linearLayoutManager
    this.setHasFixedSize(true)
    this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

/**
 * hiding keyboard in an activity
 */
fun Activity.hideKeyboard() {

    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Activity.lockOrientation(currentOrientation: Int = this.resources.configuration.orientation) {

    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

    else this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
}

fun Activity.unLockOrientation() {

    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

fun Activity.showToolbar() {

    (this as AppCompatActivity).supportActionBar!!.show()
}

/**
 * hiding keyboard anywhere
 */
fun Context.hideKeyboard(view: View) {

    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


/**
 * sets a background color on a material button (backward compatible)
 */
fun MaterialButton.rzBackgroundColor(color: Int) {

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) this.backgroundTintList = android.content.res.ColorStateList.valueOf(color)

    else  this.setBackgroundColor(color)
}


/**
 * show or hide a view
 */
fun View.rzHideShow() {

    if (this.visibility == View.VISIBLE) this.visibility = View.GONE

    else this.visibility = View.VISIBLE
}

/**
 * sets view visibility to gone
 */
fun View.rzSetVisibilityGone() {

    this.visibility = View.GONE
}

/**
 * sets view visibility to invisible
 */
fun View.rzSetVisibilityInvisible() {

    this.visibility = View.INVISIBLE
}

/**
 * returns true if the view is visible
 */
fun View.rzVisible(): Boolean {

    return this.visibility == View.VISIBLE
}

/**
 * sets view visibility to visible
 */
fun View.rzSetVisible() {

    this.visibility = View.VISIBLE
}

/**
 * click listener with a guard to prevent rapid clicks
 */
fun View.rzClickListener(mQuickRapidIdler: QuickRapidIdler, action: () -> Unit) {

    this.setOnClickListener {

        // used to eliminate fast taps on the same row
        mQuickRapidIdler.throttle {

            action()
        }
    }
}



@Suppress("unused")
fun View.rzClickGuard(mQuickRapidIdler: QuickRapidIdler, action: () -> Unit) {
    // used to eliminate fast taps on the same row
    mQuickRapidIdler.throttle {
        action()
    }
}


