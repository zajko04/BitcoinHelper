package com.example.bitcoinhelper.mainscreen

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.database.CashEntity
import java.lang.IllegalStateException

class MainRecyclerViewAdapter(
    private val mCashEntityViewModel: CashEntityViewModel,
    private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "MainRecyclerViewAdapter"
    private var itemsList = ArrayList<Item>()
    private var currentLongPressedPosition = -1

    override fun getItemViewType(position: Int): Int =
        if(position == currentLongPressedPosition) ViewType.LONG_PRESS.value
        else ViewType.MAIN.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater? = LayoutInflater.from(parent.context)
        val view: View = inflater!!.inflate(R.layout.main_item, parent, false)
        val longPressView: View =
            inflater.inflate(R.layout.main_item_after_long_press, parent, false)

        return when (viewType) {
            ViewType.LONG_PRESS.value -> LongPressViewHolder(longPressView, view)
            else -> MainViewHolder(view, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnLongClickListener{
            (holder as MainViewHolder).stopTimer()
            currentLongPressedPosition = position
            notifyDataSetChanged()
            true
        }

        if (holder is LongPressViewHolder) {
            val cancelButton = holder.itemView.findViewById<RelativeLayout>(R.id.cancel)
            cancelButton.setOnClickListener {
                currentLongPressedPosition = -1
                notifyDataSetChanged()
            }

            val deleteButton = holder.itemView.findViewById<RelativeLayout>(R.id.delete)
            deleteButton.setOnClickListener {
                val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                    run {
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE ->
                                mCashEntityViewModel.delete(itemsList[position].cashEntity)
                            DialogInterface.BUTTON_NEGATIVE ->
                                dialog.dismiss()
                            else ->
                                throw IllegalStateException()
                        }
                    }
                }

                val builder = AlertDialog.Builder(mContext, R.style.alertDialog)
                val dialog : AlertDialog = builder.setMessage(mContext.getString(R.string.areYouSure))
                    .setPositiveButton(mContext.getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(mContext.getString(R.string.no), dialogClickListener)
                    .show()

                val title = dialog.findViewById<TextView>(android.R.id.message)
                if(title == null){
                    Log.e(TAG, "onBindViewHolder - title is null")
                } else {
                    title.textSize = 25F
                }
            }
        }

        if (holder is MainViewHolder) {
            holder.setUp(itemsList[position].cashEntity)
        }
    }

    override fun getItemCount() = itemsList.size

    internal fun setItems(items: List<CashEntity>) {
        itemsList.clear()
        for(item in items) {
            itemsList.add(Item(item, ViewType.MAIN))
        }
        notifyDataSetChanged()
    }

    enum class ViewType(val value: Int) {
        MAIN(0),
        LONG_PRESS(1)
    }

    data class Item(val cashEntity: CashEntity, var viewType: ViewType)
}