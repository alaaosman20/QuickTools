package com.rzahr.quicktools

import android.animation.ValueAnimator
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
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*

object QuickUIUtils {

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

        mQuickNotificationUtils.openTopActivityOnClick(mNotificationCompatBuilder, context, QuickInjectable.currentActivity(), defaultActivity)
        mQuickNotificationUtils.setSoundAndVibrate(mNotificationCompatBuilder)

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

    fun showQuickAlert(builder: AlertDialog.Builder): AlertDialog? {

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
    fun createToolbar(appCompatActivity: AppCompatActivity, title: String, subTitle: String, withNavigation: Boolean?, withLogo: Boolean?, backgroundColorId: Int? = null, toolbarId: Int, titleId: Int, subtitleId: Int, logoImage: Int, icon: Int? =null) {

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
}