@file:Suppress("unused")

package com.rzahr.quicktools.extensions

import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.rzahr.quicktools.QuickClickGuard

/**
 * @author Rashad Zahr
 */

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
fun View.rzClickListener(clickGuard: QuickClickGuard, action: () -> Unit) {

    this.setOnClickListener {

        clickGuard.guard { action() }
    }
}

/**
 * used to guard from multiple rapid clicks on a view
 * @param clickGuard: the class that prevents the rapid clicks
 * @param action: passed function upon successful passed clicked
 */
@Suppress("unused")
fun View.rzClickGuard(clickGuard: QuickClickGuard, action: () -> Unit) {

    clickGuard.guard {
        action()
    }
}

/**
 * used to show a bubble like view as a hint on a view
 * @param title: the title displayed
 * @param id: the id of this bubble to be displayed only once
 * @param activity: the activity of this view
 * @param description: optional description
 * @param backgroundColorId: the background color of the bubble
 * @return BubbleShowCaseBuilder object
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
