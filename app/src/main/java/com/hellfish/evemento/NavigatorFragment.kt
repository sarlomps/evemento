package com.hellfish.evemento

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.hellfish.evemento.R.string.app_name

open class NavigatorFragment : Fragment() {

    protected open val titleId: Int = app_name
    lateinit var navigatorListener: Navigator

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Navigator) navigatorListener = context else throw ClassCastException(context.toString() + " must implement Navigator.")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    protected open fun setupToolbar() {
        navigatorListener.setCustomToolbar(title = resources.getString(titleId))
    }

    protected fun showToast(stringId: Int) = Toast.makeText(activity, getString(stringId), Toast.LENGTH_LONG).show()

    protected fun showToast(string: String) = Toast.makeText(activity, string, Toast.LENGTH_LONG).show()


    protected fun createAlertDialog(title: Int,
                                    input: View,
                                    positiveButtonDefinition: Pair<Int, ((DialogInterface, Int) -> Unit)>? = null,
                                    negativeButtonDefinition: Pair<Int, ((DialogInterface, Int) -> Unit)>? = null,
                                    neutralButtonDefinition: Pair<Int, ((DialogInterface, Int) -> Unit)>? = null): AlertDialog {
        val inputContainer = FrameLayout(activity!!)
        input.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            marginStart = resources.getDimension(R.dimen.alertDialogPadding).toInt()
            marginEnd = resources.getDimension(R.dimen.alertDialogPadding).toInt()
        }
        inputContainer.addView(input)

        return AlertDialog.Builder(activity!!)
                .setTitle(getString(title))
                .setView(inputContainer).apply{
                    positiveButtonDefinition?.let { setPositiveButton(getString(it.first), it.second) }
                    negativeButtonDefinition?.let { setNegativeButton(getString(it.first), it.second) }
                    neutralButtonDefinition?.let { setNeutralButton(getString(it.first), it.second) }
                }.create()

    }

    protected fun withConfirmationDialog(title: Int, action: () -> Unit) {
        AlertDialog.Builder(activity!!)
                .setTitle(getString(title))
                .setPositiveButton(getString(R.string.yes)) { _, _ -> action() }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> Unit }
                .create()
                .show()
    }

}