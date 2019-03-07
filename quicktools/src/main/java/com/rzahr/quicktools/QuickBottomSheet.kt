package com.rzahr.quicktools

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rzahr.quicktools.extensions.rzClickListener
import javax.inject.Inject

@Suppress("unused")
class QuickBottomSheet {

    /**
     * @author Rashad Zahr
     */
    class DialogViewer @Inject constructor(val context: Context, val activity: Activity) {

        private var mBottomSheetDialog: BottomSheetDialog? = null
        private lateinit var mBottomSheetView: View

        fun getView(layoutId: Int): View {

            create(layoutId)
            return mBottomSheetView
        }

        fun dismiss() {

            mBottomSheetDialog?.dismiss()
        }

        private fun create(layoutId: Int) {

            if (mBottomSheetDialog == null) {

                mBottomSheetDialog = BottomSheetDialog(context)
                mBottomSheetView = activity.layoutInflater.inflate(layoutId, null)
                mBottomSheetDialog?.setContentView(mBottomSheetView)
            }
        }

        fun show(layoutId: Int) {

            create(layoutId)
            mBottomSheetDialog?.show()
        }
    }

    /**
     * @author Rashad Zahr
     */
    class Viewer @Inject constructor(@Suppress("MemberVisibilityCanBePrivate") val quickRapidIdler: QuickRapidIdler) {

        //todo needs revision
        private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

        fun create(linearLayout: LinearLayout) {

            sheetBehavior = BottomSheetBehavior.from<LinearLayout>(linearLayout)
            linearLayout.rzClickListener(quickRapidIdler) { expandCloseSheet() }
        }

        fun dismiss() {

            BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.STATE_HIDDEN
        }

        private fun expandCloseSheet() {

            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED else sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}