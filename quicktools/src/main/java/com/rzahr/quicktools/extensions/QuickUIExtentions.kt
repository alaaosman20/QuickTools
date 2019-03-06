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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.google.android.material.button.MaterialButton
import com.rzahr.quicktools.QuickRapidIdler

/**
 * @author Rashad Zahr
 */


/**
 * used to set the activity to full screen
 */
fun Activity.setFullScreen() {

    // set to full screen
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * used to show a bubble like view as a hint on a view
 * @param title: the title displayed
 * @param id: the id of this bubble to be displayed only once
 * @param activity: the activity of this view
 * @param description: optional description
 * @param backgroundColorId: the background color of the bubble
 */
fun View.addShowCase(title: String, id: String, activity: Activity, description: String = "", backgroundColorId: Int): BubbleShowCaseBuilder {

    val aas:List<BubbleShowCase.ArrowPosition> = emptyList()
    return BubbleShowCaseBuilder(activity) //Activity instance
        .title(title) //Any title for the bubble view
        .description(description)
        .arrowPosition(aas)
        .showOnce(id) //Id to show only once the BubbleShowCase
        .listener(object: BubbleShowCaseListener {

            override fun onBackgroundDimClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }

            override fun onBubbleClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }

            override fun onCloseActionImageClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }

            override fun onTargetClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }
        })
        .targetView(this) //View to point out
        .backgroundColor(ContextCompat.getColor(activity, backgroundColorId))
}

/**
 * used to add animation to a constraint layout
 * @param layoutId: the layout id that the animation will change to
 * @param activity: the activity
 */
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
 * tool used to hide the keyboard from a fragment
 */
fun Fragment.hideKeyboard() {

    view?.let { activity!!.hideKeyboard(it) }
}

/**
 * creates the activity toolbar
 *
 * @param title the toolbar title
 * @param subTitle the toolbar subtitle
 * @param withNavigation boolean value specifying if the activity supports back
 * @param withLogo boolean value specifying if the activity has a logo
 */
fun AppCompatActivity.createToolbar(title: String, subTitle: String, withNavigation: Boolean?, withLogo: Boolean?, backgroundColorId: Int? = null, toolbarId: Int, titleId: Int, subtitleId: Int, logoImage: Int, icon: Int? =null) {

    val toolbar = this.findViewById(toolbarId) as Toolbar
    this.setSupportActionBar(toolbar)

    backgroundColorId?.let { toolbar.setBackgroundResource(it) }

    if (title.isNotEmpty()) {

        val titleTextView = toolbar.findViewById(titleId) as TextView
        titleTextView.text = title
        titleTextView.visibility = View.VISIBLE
    }

    if (subTitle.isNotEmpty()) {

        val subTitleTextView = toolbar.findViewById(subtitleId) as TextView
        subTitleTextView.text = subTitle
        subTitleTextView.visibility = View.VISIBLE
    }

    if (withNavigation!! && this.supportActionBar != null) this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    if (withLogo!!) {

        val imageLogo = toolbar.findViewById(logoImage) as ImageView

        if (this.supportActionBar != null) this.supportActionBar!!.setDisplayShowTitleEnabled(false)

        imageLogo.visibility = View.VISIBLE

        icon?.let { imageLogo.setBackgroundResource(it) }
    }
}

/**
 * quick initializer for a recycler view
 * @param linearLayoutManager: the linear layout manager for the recycler view
 * @param context: the context
 */
fun RecyclerView.initializeLinear(linearLayoutManager: LinearLayoutManager, context: Context) {

    this.layoutManager = linearLayoutManager
    this.setHasFixedSize(true)
    this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

/**
 * show the alert dialog
 */
fun AlertDialog.Builder.showQuickAlert(): AlertDialog? {

    val dialog: AlertDialog = this.create()
    dialog.show()
    return dialog
}

/**
 * tool used to hide the keyboard from an activity
 */
fun Activity.hideKeyboard() {

    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

/**
 * lock the activity orientation in landscape or portrait
 * @param currentOrientation: the current orientation mode that will be locked in
 */
fun Activity.lockOrientation(currentOrientation: Int = this.resources.configuration.orientation) {

    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

    else this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
}

/**
 * unlock the activity locked orientation
 */
fun Activity.unLockOrientation() {

    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

/**
 * show the hidden toolbar in the activity
 */
fun Activity.showToolbar() {

    (this as AppCompatActivity).supportActionBar!!.show()
}

/**
 * hiding keyboard anywhere
 * @param view: the view
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
 * @return boolean value representing if the view is visible or not
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
fun View.rzClickListener(quickRapidIdler: QuickRapidIdler, action: () -> Unit) {

    this.setOnClickListener {

        quickRapidIdler.throttle { action() }
    }
}

/**
 * used to guard from multiple rapid clicks on a view
 * @param quickRapidIdler: the class that prevents the rapid clicks
 * @param action: passed function upon successful passed clicked
 */
@Suppress("unused")
fun View.rzClickGuard(quickRapidIdler: QuickRapidIdler, action: () -> Unit) {

    quickRapidIdler.throttle {
        action()
    }
}


