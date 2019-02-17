package com.tamimattafi.asdgroup.Classes

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class NewsFeedParser(PAGE_SIZE : Int,PREVIOUS_SIZE : Int){

    private val mNameSpaces: String? = null
    private val mPageSize: Int = PAGE_SIZE
    private val mPreviousSize : Int = PREVIOUS_SIZE

    //Setting up the parser
    @Throws(XmlPullParserException::class, IOException::class)
    fun parseNewsRSS(inputStream: InputStream): ArrayList<NewsObject> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readRSS(parser)
        }
    }

    //Get tottal count
    @Throws(XmlPullParserException::class, IOException::class)
    fun getNewsCount(inputStream: InputStream): Long {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            parser.require(XmlPullParser.START_TAG, mNameSpaces, "rss")
            var mTottalNews : Long = 0
            //Checks start tag and pagination size
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                if (parser.name == "channel"){
                    parser.next()
                }
                else {
                    when {
                        parser.name == "item" -> {
                            skipTag(parser)
                            mTottalNews++
                        }
                        else -> skipTag(parser)
                    }
                }

            }
            return mTottalNews
        }
    }

    //Read resources file
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRSS(parser: XmlPullParser): ArrayList<NewsObject> {

        //Unwanted News
        var mOlds = 0
        //Wanted news list
        val mNews = ArrayList<NewsObject>()
        //File start tag
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "rss")
        //Checks start tag and pagination size
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            //Collection start tag
            if (parser.name == "channel"){
                parser.next()
            }
            else {
                //Check unwanted news size
                if (mOlds < mPreviousSize && parser.name == "item") {
                    mOlds++
                    skipTag(parser)
                    continue
                }
                else {
                    //Checks wanted news
                    if (mNews.size < mPageSize - mPreviousSize) {
                        // Starts by looking for the item tags
                        when {
                            parser.name == "item" -> mNews.add(readNewsItem(parser))
                            else -> skipTag(parser)
                        }
                    }
                    else {
                        break
                    }
                }
            }

        }
        return mNews
    }

    //Read news item
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readNewsItem(parser: XmlPullParser): NewsObject {
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "item")
        lateinit var newsTittle : String
        lateinit var newsDate: String
        lateinit var newsDescription: String
        lateinit var newsCategory: String
        lateinit var newsImage: String
        lateinit var newsLink: String
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> newsTittle = readNewsTitle(parser)
                "pubDate" -> newsDate = readNewsDate(parser)
                "description" -> newsDescription = readNewsDescription(parser)
                "category" -> newsCategory = readNewsCategory(parser)
                "enclosure" -> newsImage = readNewsImage(parser)
                "link" -> newsLink = readNewsLink(parser)
                else -> skipTag(parser)
            }
        }
        return NewsObject(
            newsTittle,
            newsDate,
            newsDescription,
            newsCategory,
            newsImage,
            newsLink
        )
    }

    //Read news tittle
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNewsTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "title")
        val mTittle = readTagText(parser)
        parser.require(XmlPullParser.END_TAG, mNameSpaces, "title")
        return mTittle
    }

    //Read news date
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNewsDate(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "pubDate")
        val mDate = readTagText(parser)
        parser.require(XmlPullParser.END_TAG, mNameSpaces, "pubDate")
        return mDate
    }

    //Read news description
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNewsDescription(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "description")
        val mDescription = readTagText(parser)
        parser.require(XmlPullParser.END_TAG, mNameSpaces, "description")
        return mDescription
    }

    //Read news category
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNewsCategory(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "category")
        val mCategory = readTagText(parser)
        parser.require(XmlPullParser.END_TAG, mNameSpaces, "category")
        return mCategory
    }

    //Read news image
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNewsImage(parser: XmlPullParser): String {
        var mImage = ""
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "enclosure")
        val mTag = parser.name
        val mImageType = parser.getAttributeValue(null, "type")
        if (mTag == "enclosure") {
            if (mImageType == "image/jpeg") {
                mImage = parser.getAttributeValue(null, "url")
                parser.nextTag()
            }
        }
        parser.next()
        return mImage
    }

    //Read news link
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNewsLink(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, mNameSpaces, "link")
        val mLink : String = readTagText(parser)
        parser.require(XmlPullParser.END_TAG, mNameSpaces, "link")
        return if (mLink.isEmpty()) {
            "https://lenta.ru/"
        }
        else {
            mLink
        }
    }


    //Read tag text
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTagText(parser: XmlPullParser): String {
        var mText = ""
        if (parser.next() == XmlPullParser.TEXT) {
            mText = parser.text
            parser.nextTag()
        }
        return mText
    }

    //Skip unwanted tags
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skipTag(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}