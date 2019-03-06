@file:Suppress("unused")

package com.rzahr.quicktools.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickNotificationUtils
import com.rzahr.quicktools.R
import com.rzahr.quicktools.extensions.rzSetVisibilityInvisible
import com.rzahr.quicktools.extensions.rzSetVisible
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*

/**
 * @author Rashad Zahr
 *
 * object used as a helper to build common UI views
 */
object QuickUIUtils {

    /**
     * shows a bubble on view as a hint to what it does with the help of a third party library: com.elconfidencial.bubbleshowcase:bubbleshowcase
     * @param showCases: an array of @BubbleShowCaseBuilder which is created using addShowCase
     */
    fun showBubbles(showCases: Array<BubbleShowCaseBuilder>) {

        val bubbleSequence =  BubbleShowCaseSequence()

        for (showCase in showCases) bubbleSequence.addShowCase(showCase)

        bubbleSequence.show()
    }

    /**
     * used to animate a background view with colors
     * use example: QuickUIUtils.animateBackgroundWithColors(ValueAnimator.ofObject(ArgbEvaluator(), color1, color3, color4, color1), this)
     */
    fun animateBackgroundWithColors(colorAnimation: ValueAnimator, vararg views: View): ValueAnimator {

        colorAnimation.duration = 10850 // milliseconds
        colorAnimation.addUpdateListener { animator -> for (view in views) view.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()
        colorAnimation.repeatCount = ValueAnimator.INFINITE

        return colorAnimation
    }

    fun sendNotification(title: String, message: String, mQuickNotificationUtils: QuickNotificationUtils, context: Context, key: String = title + message, smallIcon: Int, id: String, logo: Int, defaultActivity: Class<Any>) {

        val mNotificationCompatBuilder = mQuickNotificationUtils.getNotificationBuilder(
            title,
            message,
            false,
            smallIcon,
            id,
            logo
        )
        mNotificationCompatBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))

        mQuickNotificationUtils.openTopActivityOnClick(mNotificationCompatBuilder, context,
            QuickInjectable.currentActivity(), defaultActivity)
        mQuickNotificationUtils.setSoundAndVibrate(mNotificationCompatBuilder)

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(key.hashCode(), mNotificationCompatBuilder.build())
    }

    @SuppressLint("InflateParams")
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

    @SuppressLint("InflateParams")
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

    fun createQuickAlert(title: String, message: String, negativeButtonText: String, positiveButtonText: String, context: Context, positiveAction: () -> Unit, negativeAction: () -> Unit, hasNegativeButton: Boolean = true, cancelable: Boolean = true, logo: Drawable? = null) {

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

    fun createQuickAlert(title: String, message: Spanned, negativeButtonText: String, positiveButtonText: String, context: Context, positiveAction: () -> Unit, negativeAction: () -> Unit, hasNegativeButton: Boolean = true, cancelable: Boolean = true, logo: Drawable? = null) {

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
}