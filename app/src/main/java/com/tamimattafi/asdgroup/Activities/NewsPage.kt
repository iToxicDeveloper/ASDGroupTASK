package com.tamimattafi.asdgroup.Activities

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.tamimattafi.asdgroup.Classes.InternalStorage
import com.tamimattafi.asdgroup.Classes.NewsFeedParser
import com.tamimattafi.asdgroup.Classes.NewsObject
import com.tamimattafi.asdgroup.Classes.NewsRecyclerAdapter
import kotlinx.android.synthetic.main.activity_news.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.tamimattafi.asdgroup.R




class NewsPage : AppCompatActivity() {

    private  var adapter: NewsRecyclerAdapter? = null

    //Pagination size
    private var mPageSize = 5
    //Last page size
    private var mPreviousSize = 0
    //News cashing cashing key
    private var mNewsKey = "$mPreviousSize-$mPageSize"
    private var mTotalCount : Long = 0

    //RSS file mFeedUrl
    private val mFeedUrl = "https://lenta.ru/rss/news"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setSupportActionBar(news_toolbar)
        supportActionBar!!.title = ""

        //Load news
        DownloadXmlTask().execute(mFeedUrl)

        //Get news count
        DownloadXmlTaskForCount().execute(mFeedUrl)

        //Refresh Listener
        news_refresh.setOnRefreshListener {
            mPageSize = 5
            mPreviousSize = 0
            mNewsKey = "$mPreviousSize-$mPageSize"
            DownloadXmlTask().execute(mFeedUrl)
        }

    }

    //AsyncTask XML downloader implementation for news list
    private inner class DownloadXmlTask : AsyncTask<String, Void, ArrayList<NewsObject>>() {
        override fun doInBackground(vararg urls: String): ArrayList<NewsObject>?  {
            return try {
                   cashNews(loadNewsList(urls[0]))
                    readNews()
            } catch (e: IOException) {
                readNews()
            } catch (e: XmlPullParserException) {
                readNews()
            }
        }

        override fun onPostExecute(mNewsArray: ArrayList<NewsObject>) {
            news_refresh.isRefreshing = false
            if (!mNewsArray.isEmpty()) {
                updateUi(mNewsArray)
            }
            else {
                if (mPreviousSize != 0) {
                    adapter!!.getFooter()!!.bind()
                }
            }
        }
    }

    //AsyncTask XML downloader implementation for news count
    private inner class DownloadXmlTaskForCount : AsyncTask<String, Void, Long>() {
        override fun doInBackground(vararg urls: String): Long  {
            return try {
               getNewsCount(urls[0])
            } catch (e: IOException) {
               0
            } catch (e: XmlPullParserException) { 0
            }
        }

        override fun onPostExecute(mCount: Long) {
            if (mCount > 0) {
                mTotalCount = mCount
            }
            else {
                if (adapter == null) {
                    news_error.visibility = View.VISIBLE
                    news_progressBar.visibility = View.GONE
                    Toast.makeText(this@NewsPage,"Please check your internet connection",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //Load news ArrayList
    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadNewsList(urlString: String): ArrayList<NewsObject> {

        val mNews = downloadUrl(urlString)?.use { stream ->
            // Instantiate the parser
            NewsFeedParser(mPageSize,mPreviousSize).parseNewsRSS(stream)
        } ?: ArrayList()
        return if (mNews.isEmpty()){
            readNews()
        } else {
            mNews
        }
    }

    //Get news total item count
    @Throws(XmlPullParserException::class, IOException::class)
    private fun getNewsCount(urlString: String): Long {
        return downloadUrl(urlString)?.use { stream ->
            // Instantiate the parser
            NewsFeedParser(mPageSize,mPreviousSize).getNewsCount(stream)
        } ?: 0
    }

    //Download feed file
    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        val mFeedURL = URL(urlString)
        return (mFeedURL.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            connect()
            inputStream
        }
    }

    //Adds 5 more items to the page
    fun addMoreItems(){
        mPreviousSize = mPageSize
        mPageSize  += 5
        mNewsKey = "$mPreviousSize-$mPageSize"
        DownloadXmlTask().execute(mFeedUrl)
    }

    //Update news recycler
    fun updateUi(mNewsArray:ArrayList<NewsObject>){
        if (mPreviousSize == 0) {
            news_error.visibility = View.GONE
            news_progressBar.visibility = View.GONE
            news_recycler.layoutManager = LinearLayoutManager(this)
            news_recycler.itemAnimator = DefaultItemAnimator()
            news_recycler.setHasFixedSize(true)
            adapter = NewsRecyclerAdapter(mNewsArray)
            news_recycler.adapter = adapter
            val mRecyclerListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItem = (recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition().toLong()
                        if (lastVisibleItem >= mPageSize -1)
                        {
                            if (lastVisibleItem == mTotalCount-1) {
                                adapter!!.getFooter()!!.bind()
                            }
                            else {
                                addMoreItems()
                            }
                        }
                }

            }
            news_recycler.addOnScrollListener(mRecyclerListener)

        }
        else {
            adapter!!.addNewItems(mNewsArray)
        }
        adapter!!.notifyDataSetChanged()
    }

    //Save news list to internal storage
    fun cashNews(mNewsList: ArrayList<NewsObject>) {
        try {
            if (!mNewsList.isEmpty()){
                InternalStorage.writeObject(this@NewsPage, mNewsKey, mNewsList )
            }
        } catch (e: IOException) {
            Log.i("Cashing News: ",e.message)
        } catch (e: ClassNotFoundException) {
            Log.i("Cashing News: ",e.exception.message)
        }
    }

    //Read news list from internal storage
    fun readNews() : ArrayList<NewsObject> {
        return try {
            InternalStorage.readObject(this@NewsPage, mNewsKey) as ArrayList<NewsObject>
        } catch (e: IOException) {
            Log.i("Reading News: ",e.message)
            ArrayList()
        } catch (e: ClassNotFoundException) {
            Log.i("Reading News: ",e.message)
            ArrayList()
        }
    }
}
