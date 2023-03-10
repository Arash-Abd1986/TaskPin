package com.jobtick.android.adapers

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.network.model.response.LevelsItem
import com.jobtick.android.utils.setBackgroundShape
import com.jobtick.android.widget.CircularProgressView
import kotlin.math.roundToInt

class LevelsAdapter(var lastMonthIncome: Float) : RecyclerView.Adapter<BaseViewHolder>() {
    private var items: ArrayList<LevelsItem> = ArrayList()
    lateinit var dismissListener: DismissListener


    interface DismissListener {
        fun dismiss()
    }

    fun getItems(): List<LevelsItem> {
        return items
    }

    fun setItems(items: ArrayList<LevelsItem>) {
        this.items = items
    }

    inner class OriginalViewHolder(v: View) : BaseViewHolder(v) {
        var txtName: TextView
        var description: TextView
        var info: TextView
        var progressLevel1: CircularProgressView
        var progressLevel2: CircularProgressView
        var progressLevel4: CircularProgressView
        var progressLevel3: CircularProgressView
        var rlLevel1: RelativeLayout
        var rlLevel2: RelativeLayout
        var rlLevel3: RelativeLayout
        var rlLevel4: RelativeLayout
        var ln_lin_main: LinearLayout
        var ivLevel1: ImageView
        var ivLevel2: ImageView
        var ivLevel3: ImageView
        var ivLevel4: ImageView
        var ic_arrow: ImageView
        val context = v.context
        override fun clear() {}

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            val item = items[position]
            txtName.text = "Level " + (position + 1)
            info.text = """${item.service_fee.toFloat().roundToInt()}% service fee"""
            if (position == 0)
                description.text =
                    """Less than ${"$"}${
                        item.max_amount.toFloat().roundToInt()
                    } in the last 30 days"""
            else
                description.text =
                    """${"$"}${item.min_amount.toFloat().roundToInt()}+ in the last 30 days"""

            if ((((lastMonthIncome - item.min_amount.toFloat()) / (item.max_amount.toFloat() - item.min_amount.toFloat())) * 100).toInt() >= 100) {
                ln_lin_main.setBackgroundShape(
                    ContextCompat.getColor(context, R.color.N020),
                    ContextCompat.getColor(context, R.color.N020),
                    8,
                    0,
                    GradientDrawable.RECTANGLE
                )
            }
            if (position == 3)
                ic_arrow.visibility = View.INVISIBLE
            else
                ic_arrow.visibility = View.VISIBLE


            when (position) {
                0 -> {
                    progressLevel1.progress =
                        (((lastMonthIncome - item.min_amount.toFloat()) / (item.max_amount.toFloat() - item.min_amount.toFloat())) * 100).toInt()
                    rlLevel1.visibility = View.VISIBLE
                    rlLevel4.visibility = View.GONE
                    rlLevel3.visibility = View.GONE
                    rlLevel2.visibility = View.GONE
                    ivLevel1.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_level1_active
                        )
                    )
                    if (progressLevel1.progress < 4) {
                        progressLevel1.progress = 3
                    }

                }
                1 -> {
                    if (lastMonthIncome > item.min_amount.toFloat())
                        progressLevel2.progress =
                            (((lastMonthIncome - item.min_amount.toFloat()) / (item.max_amount.toFloat() - item.min_amount.toFloat())) * 100).toInt()
                    rlLevel4.visibility = View.GONE
                    rlLevel3.visibility = View.GONE
                    rlLevel2.visibility = View.VISIBLE
                    rlLevel1.visibility = View.GONE
                    if ((((lastMonthIncome - items[0].min_amount.toFloat()) / (items[0].max_amount.toFloat() - items[0].min_amount.toFloat())) * 100).toInt() >= 100) {
                        ivLevel2.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level2_active
                            )
                        )
                        ivLevel1.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level1_active
                            )
                        )
                        if (progressLevel2.progress < 4) {
                            progressLevel2.progress = 3
                        }
                    }
                }
                2 -> {
                    if (lastMonthIncome > item.min_amount.toFloat())
                        progressLevel3.progress =
                            (((lastMonthIncome - item.min_amount.toFloat()) / (item.max_amount.toFloat() - item.min_amount.toFloat())) * 100).toInt()
                    rlLevel4.visibility = View.GONE
                    rlLevel3.visibility = View.VISIBLE
                    rlLevel2.visibility = View.GONE
                    rlLevel1.visibility = View.GONE
                    if ((((lastMonthIncome - items[1].min_amount.toFloat()) / (items[1].max_amount.toFloat() - items[1].min_amount.toFloat())) * 100).toInt() >= 100) {
                        ivLevel3.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level3_active
                            )
                        )
                        ivLevel2.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level2_active
                            )
                        )
                        ivLevel1.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level1_active
                            )
                        )
                        if (progressLevel3.progress < 4) {
                            progressLevel3.progress = 3
                        }
                    }

                }
                3 -> {
                    if (lastMonthIncome > item.min_amount.toFloat())
                        progressLevel4.progress =
                            (((lastMonthIncome - item.min_amount.toFloat()) / (item.max_amount.toFloat() - item.min_amount.toFloat())) * 100).toInt()
                    rlLevel4.visibility = View.VISIBLE
                    rlLevel3.visibility = View.GONE
                    rlLevel2.visibility = View.GONE
                    rlLevel1.visibility = View.GONE
                    if ((((lastMonthIncome - items[2].min_amount.toFloat()) / (items[2].max_amount.toFloat() - items[2].min_amount.toFloat())) * 100).toInt() >= 100) {
                        ivLevel4.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level4_active
                            )
                        )
                        ivLevel3.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level3_active
                            )
                        )
                        ivLevel2.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level2_active
                            )
                        )
                        ivLevel1.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_level1_active
                            )
                        )
                        if (progressLevel4.progress < 4) {
                            progressLevel4.progress = 3
                        }
                    }
                }

            }

        }


        init {
            ln_lin_main = v.findViewById(R.id.ln_lin_main)
            txtName = v.findViewById(R.id.txt_level_title)
            description = v.findViewById(R.id.txt_description)
            info = v.findViewById(R.id.txt_info)
            progressLevel1 = v.findViewById(R.id.progress_level1)
            progressLevel2 = v.findViewById(R.id.progress_level2)
            progressLevel4 = v.findViewById(R.id.progress_level4)
            progressLevel3 = v.findViewById(R.id.progress_level3)
            rlLevel1 = v.findViewById(R.id.rl_p1)
            rlLevel2 = v.findViewById(R.id.rl_p2)
            rlLevel3 = v.findViewById(R.id.rl_p3)
            rlLevel4 = v.findViewById(R.id.rl_p4)
            ivLevel1 = v.findViewById(R.id.iv_level1)
            ivLevel2 = v.findViewById(R.id.iv_level2)
            ivLevel3 = v.findViewById(R.id.iv_level3)
            ivLevel4 = v.findViewById(R.id.iv_level4)
            ic_arrow = v.findViewById(R.id.ic_arrow)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OriginalViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(mItems: MutableList<LevelsItem>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items = ArrayList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): LevelsItem {
        return items[position]
    }
}