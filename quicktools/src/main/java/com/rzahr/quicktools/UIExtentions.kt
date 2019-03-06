@file:Suppress("unused")

package com.rzahr.quicktools

import android.animation.ValueAnimator
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*


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
fun View.rzClickListener(mCodeThrottle: CodeThrottle, action: () -> Unit) {

    this.setOnClickListener {

        // used to eliminate fast taps on the same row
        mCodeThrottle.throttle {

            action()
        }
    }
}

fun showBubbles(showCases: Array<BubbleShowCaseBuilder>) {

    val bubbleSequence =  BubbleShowCaseSequence()

    for (showCase in showCases) bubbleSequence.addShowCase(showCase)

    bubbleSequence.show()
}

fun animateBackgroundWithColors(colorAnimation: ValueAnimator, vararg views: View): ValueAnimator {

    colorAnimation.duration = 10850 // milliseconds
    colorAnimation.addUpdateListener { animator -> for (view in views) view.setBackgroundColor(animator.animatedValue as Int) }
    colorAnimation.start()
    colorAnimation.repeatCount = ValueAnimator.INFINITE

    return colorAnimation
}

fun addShowCase(title: String, id: String, activity: Activity, target: View, description: String = "", backgroundColorId: Int): BubbleShowCaseBuilder {

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
        .targetView(target) //View to point out
        .backgroundColor(ContextCompat.getColor(activity, backgroundColorId))
}

fun sendNotification(title: String, message: String, mNotificationUtils: NotificationUtils, context: Context, key: String = title + message, smallIcon: Int, id: String, logo: Int, defaultActivity: Class<Any>) {

    val mNotificationCompatBuilder = mNotificationUtils.getNotificationBuilder(
        title,
        message,
        false,
        smallIcon,
        id,
        logo
    )
    mNotificationCompatBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))

    mNotificationUtils.openTopActivityOnClick(mNotificationCompatBuilder, context, Injectable.currentActivity(), defaultActivity)
    mNotificationUtils.setSoundAndVibrate(mNotificationCompatBuilder)

    val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    mNotificationManager.notify(key.hashCode(), mNotificationCompatBuilder.build())
}

private fun createCustomAlert(title: String, message: String, cancelable: Boolean, context: Context): Array<Any> {

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)
    builder.setView(dialogView)
    builder.setCancelable(false)

    dialogView.alert_title_tv.text = title
    dialogView.alert_description_tv.text = message

    val alert = builder.show()
    alert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    if (cancelable) {

        builder.setOnKeyListener { dialog, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
            }
            true
        }
    }

    else {

        builder.setCancelable(false)

        builder.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
            }
            true
        }
    }

    return arrayOf(builder, dialogView, alert)
}

private fun createCustomAlert(title: String, message: Spanned, cancelable: Boolean, context: Context): Array<Any> {

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)
    builder.setView(dialogView)
    builder.setCancelable(false)

    dialogView.alert_title_tv.text = title
    dialogView.alert_description_tv.text = message

    val alert = builder.show()
    alert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    if (cancelable) {

        builder.setOnKeyListener { dialog, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
            }
            true
        }
    }

    else {

        builder.setCancelable(false)

        builder.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
            }
            true
        }
    }

    return arrayOf(builder, dialogView, alert)
}

fun quickCreateAlert(title: String, message: String, negativeButtonText: String, positiveButtonText: String, context: Context, positiveAction: () -> Unit, negativeAction: () -> Unit, hasNegativeButton: Boolean = true, cancelable: Boolean = true, logo: Drawable? = null) {

    val a = createCustomAlert(title, message, cancelable, context)
    // create the alert dialog and set it to cancellable or not depending on what was supplied
    val dialogView = a[1] as View
    val alert = a[2] as AlertDialog

    // if the alert has a negative button then set it
    if (hasNegativeButton) {

        var negativeButtonTextTemp = negativeButtonText

        // if no negative text was provided and the alert has a negative button, then set it to the default close text
        if (negativeButtonTextTemp.isEmpty()) negativeButtonTextTemp = context.getString(R.string.close)

        if (positiveButtonText.isEmpty())  {

            dialogView.admin_cancel_mb.rzSetVisibilityInvisible()
            dialogView.admin_ok_mb.rzSetVisible()

            dialogView.admin_ok_mb.text = negativeButtonTextTemp

            // set the negative button action
            dialogView.admin_ok_mb.setOnClickListener {

                //  run {
                alert.cancel()
                negativeAction()
                //   }
            }
        }

        else {

            dialogView.admin_cancel_mb.rzSetVisible()

            dialogView.admin_cancel_mb.text = negativeButtonTextTemp

            // set the negative button action
            dialogView.admin_cancel_mb.setOnClickListener {

                //  run {
                alert.cancel()
                negativeAction()
                //   }
            }
        }
    }


    if (logo != null) {

        dialogView.alert_iv.rzSetVisible()
        dialogView.alert_iv.setImageDrawable(logo)
    }

    // if the alert has a positive button (the positive button text is not empty) then set the positive button action to the action supplied
    if (positiveButtonText.isNotEmpty()) {

        dialogView.admin_ok_mb.rzSetVisible()
        dialogView.admin_ok_mb.text = positiveButtonText

        // set the negative button action
        dialogView.admin_ok_mb.setOnClickListener {

            positiveAction()

            alert.cancel()
        }
    }

    // finally, show the alert button
    //showAlert(builder)
}


fun quickCreateAlert(title: String, message: Spanned, negativeButtonText: String, positiveButtonText: String, context: Context, positiveAction: () -> Unit,
                negativeAction: () -> Unit, hasNegativeButton: Boolean = true, cancelable: Boolean = true, logo: Drawable? = null) {

    val a = createCustomAlert(title, message, cancelable, context)
    val dialogView = a[1] as View
    val alert = a[2] as AlertDialog

    // if the alert has a negative button then set it
    if (hasNegativeButton) {

        var negativeButtonTextTemp = negativeButtonText

        // if no negative text was provided and the alert has a negative button, then set it to the default close text
        if (negativeButtonTextTemp.isEmpty()) negativeButtonTextTemp = context.getString(R.string.close)

        if (positiveButtonText.isEmpty())  {

            dialogView.admin_cancel_mb.rzSetVisibilityInvisible()
            dialogView.admin_ok_mb.rzSetVisible()

            dialogView.admin_ok_mb.text = negativeButtonTextTemp

            // set the negative button action
            dialogView.admin_ok_mb.setOnClickListener {

                //  run {
                alert.cancel()
                negativeAction()
                //   }
            }
        }

        else {

            dialogView.admin_cancel_mb.rzSetVisible()

            dialogView.admin_cancel_mb.text = negativeButtonTextTemp

            // set the negative button action
            dialogView.admin_cancel_mb.setOnClickListener {

                //  run {
                alert.cancel()
                negativeAction()
                //   }
            }
        }
    }


    if (logo != null) {

        dialogView.alert_iv.rzSetVisible()
        dialogView.alert_iv.setImageDrawable(logo)
    }

    // if the alert has a positive button (the positive button text is not empty) then set the positive button action to the action supplied
    if (positiveButtonText.isNotEmpty()) {

        dialogView.admin_ok_mb.rzSetVisible()
        dialogView.admin_ok_mb.text = positiveButtonText

        // set the negative button action
        dialogView.admin_ok_mb.setOnClickListener {

            positiveAction()

            alert.cancel()
        }
    }
}

fun quickShowAlert(builder: AlertDialog.Builder): AlertDialog? {

        val dialog: AlertDialog = builder.create()
        dialog.show()
       return dialog
}

/**
 * creates the activity toolbar
 *
 * @param appCompatActivity the activity
 * @param title the toolbar title
 * @param subTitle the toolbar subtitle
 * @param withNavigation boolean value specifying if the activity supports back
 * @param withLogo boolean value specifying if the activity has a logo
 */
fun createToolbar(appCompatActivity: AppCompatActivity, title: String, subTitle: String, withNavigation: Boolean?, withLogo: Boolean?, backgroundColorId: Int? = null, toolbarId: Int,titleId: Int, subtitleId: Int, logoImage: Int, icon: Int? =null) {

    val toolbar = appCompatActivity.findViewById(toolbarId) as Toolbar
    appCompatActivity.setSupportActionBar(toolbar)

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

    if (withNavigation!! && appCompatActivity.supportActionBar != null) appCompatActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    if (withLogo!!) {

        val imageLogo = toolbar.findViewById(logoImage) as ImageView

        if (appCompatActivity.supportActionBar != null) appCompatActivity.supportActionBar!!.setDisplayShowTitleEnabled(false)

        imageLogo.visibility = View.VISIBLE

        icon?.let { imageLogo.setBackgroundResource(it) }
    }
}


