package com.example.bitcoinhelper.mainscreen

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.database.CashEntity

class AddCashDialog(cashEntityViewModel: CashEntityViewModel) : AppCompatDialogFragment() {

    private var mCashEntityViewModel: CashEntityViewModel = cashEntityViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.alertDialog)
        val layoutInflater = activity?.layoutInflater
        val view = layoutInflater?.inflate(R.layout.dialog_add, null)

        val who = view?.findViewById<EditText>(R.id.who)!!
        val cashIn = view.findViewById<EditText>(R.id.cashIn)!!
        val btcQuantity = view.findViewById<EditText>(R.id.btcQuantity)!!

        return builder.setView(view)
            .setTitle(R.string.dialogTitle)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.add) { _, _ ->
                val priceInPLN =
                    (1/btcQuantity.text.toString().toDouble()) * cashIn.text.toString().toFloat()

                mCashEntityViewModel.insert(
                    CashEntity(
                        who.text.toString(),
                        cashIn.text.toString().toFloat(),
                        priceInPLN.toFloat(),
                        btcQuantity.text.toString().toDouble()
                    )
                )
            }.create()
    }
}