package org.turkiye.offlinechat.ui.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.turkiye.offlinechat.R
import org.turkiye.offlinechat.interfaces.ItemClickListener

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

class ClientDialog(var context: Context, var activity: Activity) {

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun createGoalsDialog(userName: String?, clickListiner: ItemClickListener) {
        val builder = AlertDialog.Builder(context)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.dialog_client,
            activity.findViewById<View>(R.id.layoutDialogContainer) as ConstraintLayout?
        )
        builder.setView(view)

        val alertDialog = builder.create()

        val dialogClientAdd: Button = (view.findViewById(R.id.dialog_client_add)) as Button

        val dialogClientClose: ImageButton =
            (view.findViewById(R.id.dialog_client_close)) as ImageButton

        val dialogClientEdittext: EditText =
            (view.findViewById(R.id.dialog_client_title_edittext)) as EditText


        val dialogClientTextView: TextView =
            (view.findViewById(R.id.dialog_client_title_textview)) as TextView

        dialogClientTextView.text = userName

        dialogClientAdd.setOnClickListener {
            if (dialogClientEdittext.text.isNotEmpty()) {
                clickListiner.customItemListener(dialogClientEdittext.text.toString())
            }
            alertDialog.dismiss()
        }

        dialogClientClose.setOnClickListener {
            alertDialog.dismiss()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}