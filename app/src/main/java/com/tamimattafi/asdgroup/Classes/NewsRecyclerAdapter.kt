package com.tamimattafi.asdgroup.Classes

import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.tamimattafi.asdgroup.Activities.WebPage
import com.tamimattafi.asdgroup.R
import kotlinx.android.synthetic.main.news_recycler_item_layout.view.*
import kotlinx.android.synthetic.main.news_recycler_loading_footer.view.*
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



 class NewsRecyclerAdapter(mNews: ArrayList<NewsObject>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

     private val mNewsArray = mNews
     private val FOOTER_VIEW = 1
     private val ITEM_VIEW = 2
     private var mFooterView : FooterViewHolder? = null

     //Progressbar footer
     fun getFooter(): FooterViewHolder? {
         return mFooterView
     }

     //Add new items on scroll
     fun addNewItems(mNewItems : ArrayList<NewsObject>){
        val mOldNewsArraySize = mNewsArray.size
        mNewsArray.addAll(mNewItems)
        notifyItemRangeInserted(mOldNewsArraySize,mNewItems.size)
    }

    //Creates view holders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return if(viewType == ITEM_VIEW){
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_recycler_item_layout, parent, false))
        } else {
            FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_recycler_loading_footer, parent, false))
        }
    }

    //Get recycler item count
     override fun getItemCount(): Int {
        return if (mNewsArray.isEmpty()) {
            0
        }
        else mNewsArray.size +1
    }

    //Binds view holders
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        try {
            if (holder is ItemViewHolder) {
                holder.bind(mNewsArray[position])
            } else if (holder is FooterViewHolder) {
                 mFooterView = holder
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

     //Get recycler item type
     override fun getItemViewType(position: Int): Int {
         return if (position<mNewsArray.size)  {
             ITEM_VIEW
         } else{
             FOOTER_VIEW
         }

     }

    //Initializes news items
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(mNewsItem: NewsObject) {
            if (mNewsItem.getNewsTittle()== ""){
                itemView.rootView.visibility = View.GONE
                itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
            else {
                itemView.rootView.visibility = View.VISIBLE
            }
            //Parsing date
            val pubDateString = try {
                val sourceDateString = mNewsItem.getNewsDate()
                val sourceSdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzzz", Locale.US)
                val date = sourceSdf.parse(sourceDateString)
                val mTodaysCal = Calendar.getInstance()
                val mNewsCal = Calendar.getInstance()
                mNewsCal.time = date

                //Setting date
                when {
                    //Setting date for today
                    mTodaysCal.get(Calendar.DAY_OF_YEAR) == mNewsCal.get(Calendar.DAY_OF_YEAR) -> {
                        itemView.news_item_date.setTextColor(
                            ResourcesCompat.getColor(
                                itemView.context.resources,
                                com.tamimattafi.asdgroup.R.color.error_color_darker, null
                            )
                        )
                        "Сегодня - ${
                        if ((mTodaysCal.get(Calendar.HOUR_OF_DAY) - mNewsCal.get(Calendar.HOUR_OF_DAY) != 0)) {
                            "${(mTodaysCal.get(Calendar.HOUR_OF_DAY) - mNewsCal.get(Calendar.HOUR_OF_DAY))}ч назад"
                        } else {
                            if ((mTodaysCal.get(Calendar.MINUTE) - mNewsCal.get(Calendar.MINUTE) != 0)) {
                                "${(mTodaysCal.get(Calendar.MINUTE) - mNewsCal.get(Calendar.MINUTE))}м назад"
                            } else {
                                "${(mTodaysCal.get(Calendar.SECOND) - mNewsCal.get(Calendar.SECOND))}с назад"

                            }
                        }
                        }"
                    }

                    //Setting date for yesterday
                    mTodaysCal.get(Calendar.DAY_OF_YEAR) - 1 == mNewsCal.get(Calendar.DAY_OF_YEAR) -> {
                        itemView.news_item_date.setTextColor(
                            ResourcesCompat.getColor(
                                itemView.context.resources,
                                R.color.colorPrimary, null
                            )
                        )
                        "Вчера - ${
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                        }"
                    }

                    //Setting date for other days
                    else -> {
                        itemView.news_item_date.setTextColor(
                            ResourcesCompat.getColor(
                                itemView.context.resources,
                                R.color.gray_light, null
                            )
                        )
                        val mRussian = Locale("ru", "RU")
                        val sdf = SimpleDateFormat("dd MMMM yyyy - HH:mm", mRussian)
                        sdf.format(date)
                    }
                }


            }

            //On error return the full date
            catch (e: ParseException) {
                e.printStackTrace()
                mNewsItem.getNewsDate()
            }

            //Setting news image from cash then from internet
            Picasso.get()
                .load(mNewsItem.getNewsImage())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.place_holder)
                .into(itemView.news_item_ImageView, object : Callback{
                    override fun onSuccess() {
                    }
                    override fun onError(e: Exception?) {
                        Picasso.get()
                            .load(mNewsItem.getNewsImage())
                            .placeholder(R.drawable.place_holder)
                            .error(R.drawable.error_image)
                            .into(itemView.news_item_ImageView,object :Callback{
                                override fun onSuccess() {
                                }

                                override fun onError(e: Exception?) {
                                    Log.i("Picasso:",e!!.message)
                                }

                            })
                    }

                })

            //Date
            itemView.news_item_date.text = pubDateString
            //Tittle
            itemView.news_item_tittle.text = mNewsItem.getNewsTittle()
            //Category
            itemView.news_item_category.text = mNewsItem.getNewsCategory()
            //Description
            itemView.news_item_description.text = mNewsItem.getNewsDescription()
            //Setting click item listener
            itemView.setOnClickListener {
                val mIntent = Intent(itemView.context,WebPage::class.java)
                mIntent.putExtra("category",mNewsItem.getNewsCategory())
                mIntent.putExtra("link",mNewsItem.getNewsLink())
                itemView.context.startActivity(mIntent)
            }

        }
    }

     //Setting footer view
     inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(){
                itemView.news_recycler_progressBar.visibility = View.GONE
                itemView.news_recycler_footer.visibility = View.VISIBLE }
     }



}