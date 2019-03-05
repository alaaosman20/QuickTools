package com.rzahr.quicktools

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.ref.WeakReference
import javax.inject.Inject

class BaseClass {

    abstract class QuickBaseModel {

        fun createSimpleSelect(columns: String, table: String, whereClause: String = "", groupByClause: String = "", orderByClause: String = ""): String {

            return QuickDBUtils.createSimpleSelect(columns, table, whereClause, groupByClause, orderByClause)
        }

        fun createDistinctSelect(columns: String, table: String, whereClause: String = ""): String {

            return QuickDBUtils.createDistinctSelect(columns, table, whereClause)
        }

        fun delete(table: String, where: String = ""): String {

            return if (where.isNotEmpty()) "DELETE FROM $table WHERE $where"
            else "DELETE FROM $table"
        }
    }

    open class QuickBasePresenter<V : QuickBaseViewInterface, M: QuickBaseModel> @Inject constructor(): QuickBasePresenterInterface<V>, LifecycleObserver {

        @Inject lateinit var model: M
        @Inject lateinit var mContext: Context
        @Inject lateinit var mActivity: Activity

        private var stateBundle: Bundle? = null

        override fun getStateBundle(): Bundle? {
            if (stateBundle == null)
                stateBundle = Bundle()
            return stateBundle
        }

        override fun onPresenterDestroy() {
            if (stateBundle != null && !stateBundle!!.isEmpty) {
                stateBundle?.clear()
            }
        }

        override fun detachLifecycle(lifecycle: Lifecycle) {
            lifecycle.removeObserver(this)
        }

        override fun attachLifecycle(lifecycle: Lifecycle) {
            lifecycle.addObserver(this)
        }

        override fun onPresenterCreated() {
        }

        private var weakReference: WeakReference<V>? = null

        fun getString(id: Int): String {

            return id.get(mContext)
        }

        override fun attachView(view: V) {
            if (!isViewAttached) {
                weakReference = WeakReference(view)
                view.setPresenter(this)
            }
        }

        override fun detachView() {
            weakReference?.clear()

            weakReference = null

            //  disposable.clear()
        }

        val view: V?
            get() = weakReference?.get()

        private val isViewAttached: Boolean
            get() = weakReference != null && weakReference!!.get() != null
    }

    interface QuickBaseViewInterface {

        fun setPresenter(presenter: QuickBasePresenter<*,*>)
    }

    interface QuickBasePresenterInterface<V : QuickBaseViewInterface> {

        fun attachView(view: V)
        fun detachView()

        fun attachLifecycle(lifecycle: Lifecycle)
        fun onPresenterCreated()
        fun detachLifecycle(lifecycle: Lifecycle)
        fun onPresenterDestroy()
        fun getStateBundle(): Bundle?
    }

    abstract class QuickAbstractActivity : AppCompatActivity(), QuickBaseViewInterface {

        @Inject lateinit var mCodeThrottle: CodeThrottle

        override fun attachBaseContext(newBase: Context?) {
            super.attachBaseContext(MyContextWrapper.wrap(newBase,Injectable.shPrefUtils().get("Language")))
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            onActivityInject()
        }

        protected abstract fun onActivityInject()
    }

    abstract class QuickAbstractFragment : Fragment(), QuickBaseViewInterface {

        private var presenter: QuickBasePresenter<*,*>? = null

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            onActivityInject()
        }

        protected abstract fun onActivityInject()

        override fun setPresenter(presenter: QuickBasePresenter<*,*>) {

            this.presenter = presenter
        }

        override fun onDestroy() {
            super.onDestroy()

            presenter?.detachView()
            presenter = null
        }
    }

    abstract class QuickAbstractDialogFragment : DialogFragment(), QuickBaseViewInterface {

        private var presenter: QuickBasePresenter<*,*>? = null

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            onActivityInject()
        }

        protected abstract fun onActivityInject()

        override fun setPresenter(presenter: QuickBasePresenter<*,*>) {

            this.presenter = presenter
        }

        override fun onDestroy() {
            super.onDestroy()

            presenter?.detachView()
            presenter = null
        }
    }

    abstract class QuickBaseActivity<P : QuickBasePresenterInterface<*>>: QuickAbstractActivity() {

        @Inject lateinit var mPresenter: P

        private var presenter: QuickBasePresenter<*,*>? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            onActivityInject()
        }

        override fun setPresenter(presenter: QuickBasePresenter<*,*>) {

            this.presenter = presenter
        }

        override fun onDestroy() {
            super.onDestroy()

            presenter?.detachView()
            presenter = null
        }
    }

    abstract class QuickBaseViewModel constructor(val context: Context) : ViewModel(), LifecycleObserver {

        // used to show or hide the progress bar
        val mutableProgressBarViewState = MutableLiveData<Int>()

        open fun updateProgressBarViewState(state: Int) {
            mutableProgressBarViewState.value = state
        }

        open fun update() { /*filled in the view model*/
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        open fun onResume() { /*filled in the view model*/
        }

        open fun saveState(outState: Bundle) { /*filled in the view model*/
        }

        open fun restoreState(inState: Bundle?) { /*filled in the view model*/
        }

        fun getString(id: Int): String {

            return id.get(context)
        }
    }

    abstract class QuickMVVMFragment<VM : QuickBaseViewModel> constructor(private val layoutId: Int, private val lockOrientation: Boolean = false, private val hideToolbar: Boolean = true) : BottomSheetDialogFragment(), QuickBaseViewInterface { //dialogfragment

        @Inject
        lateinit var viewModelFactory: ViewModelProvider.Factory
        var mViewModel: VM? = null

        @Inject lateinit var mCodeThrottle: CodeThrottle

        override fun setPresenter(presenter: QuickBasePresenter<*,*>) {
            //
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(layoutId, container, false)
        }

        fun <T : ViewModel> setGVViewModel(savedInstanceState: Bundle?, modelClass: Class<T>, triggerRestoreState: Boolean = true) {

            @Suppress("UNCHECKED_CAST")
            mViewModel = ViewModelProviders.of(this, viewModelFactory).get(modelClass) as VM

            lifecycle.addObserver(mViewModel!!)

            if (triggerRestoreState) (mViewModel)?.restoreState(savedInstanceState)
        }

        fun getViewModel(): QuickBaseViewModel? {

            return mViewModel
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)

            mViewModel?.saveState(outState)
        }

        override fun onResume() {
            super.onResume()

            if (lockOrientation) {
                // set orientation strictly to portrait and hide the toolbar
                activity?.lockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                if (hideToolbar) (activity as AppCompatActivity).supportActionBar!!.hide()
            }
        }

        override fun onStop() {
            super.onStop()

            if (lockOrientation) {
                if (hideToolbar) activity?.showToolbar()
                activity?.unLockOrientation()
            }
        }
    }

    abstract class QuickMVPFragment<P : QuickBasePresenterInterface<*>>(private val layoutId: Int, private val lockOrientation: Boolean = false) : QuickAbstractFragment() {

        @Inject lateinit var mCodeThrottle: CodeThrottle

        @Inject lateinit var mPresenter: P
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(layoutId, container, false)
        }

        override fun onResume() {
            super.onResume()

            if (lockOrientation) {
                // set orientation strictly to portrait and hide the toolbar
                activity?.lockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                (activity as AppCompatActivity).supportActionBar!!.hide()
            }
        }

        override fun onStop() {
            super.onStop()

            if (lockOrientation) {
                activity?.showToolbar()
                activity?.unLockOrientation()
            }
        }
    }

    abstract class QuickMVPFragmentDialog<P : QuickBasePresenterInterface<*>>(private val layoutId: Int, private val lockOrientation: Boolean = false) : QuickAbstractDialogFragment() {

        @Inject lateinit var mCodeThrottle: CodeThrottle
        @Inject lateinit var mPresenter: P

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(layoutId, container, false)
        }

        override fun onResume() {
            super.onResume()

            if (lockOrientation) {
                // set orientation strictly to portrait and hide the toolbar
                activity?.lockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                (activity as AppCompatActivity).supportActionBar!!.hide()
            }
        }

        override fun onStop() {
            super.onStop()

            if (lockOrientation) {
                activity?.showToolbar()
                activity?.unLockOrientation()
            }
        }
    }

}