@file:Suppress("unused")

package com.rzahr.quicktools.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton

/**
 * @author Rashad Zahr
 */

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
 * @return an alert dialog
 */
fun AlertDialog.Builder.showQuickAlert(): AlertDialog? {

    val dialog: AlertDialog = this.create()
    dialog.show()
    return dialog
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
