package com.rzahr.quicktools

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject

class QuickBottomSheet {

    class DialogViewer @Inject constructor(val context: Context, val activity: Activity) {

        private var mBottomSheetDialog: BottomSheetDialog? = null
        private lateinit var mLocationChangerReasonBottomSheetView: View

        fun getView(layoutId: Int): View {

            create(layoutId)
            return mLocationChangerReasonBottomSheetView
        }

        fun dismiss() {

            mBottomSheetDialog?.dismiss()
        }

        private fun create(layoutId: Int) {

            if (mBottomSheetDialog == null) {

                mBottomSheetDialog = BottomSheetDialog(context)
                mLocationChangerReasonBottomSheetView = activity.layoutInflater.inflate(layoutId, null)
                mBottomSheetDialog?.setContentView(mLocationChangerReasonBottomSheetView)
            }
        }

        fun show(layoutId: Int) {

            create(layoutId)
            mBottomSheetDialog?.show()
        }
    }

    class Viewer @Inject constructor(@Suppress("MemberVisibilityCanBePrivate") val codeThrottle: CodeThrottle) {

        //todo needs revision
        private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

        fun create(linearLayout: LinearLayout) {

            sheetBehavior = BottomSheetBehavior.from<LinearLayout>(linearLayout)
            linearLayout.rzClickListener(codeThrottle) { expandCloseSheet() }
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