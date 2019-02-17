package com.tamimattafi.asdgroup.Classes

import java.io.Serializable

class NewsObject : Serializable{

    private lateinit var newsTittle : String
    private lateinit var newsDate: String
    private lateinit var newsDescription: String
    private lateinit var newsCategory: String
    private lateinit var newsImage: String
    private lateinit var newsLink: String

    constructor(newsTittle: String,newsDate : String,newsDescription: String,newsCategory: String,newsImage: String,newsLink: String) {
        this.newsTittle = newsTittle
        this.newsDate = newsDate
        this.newsDescription = newsDescription
        this.newsCategory = newsCategory
        this.newsImage = newsImage
        this.newsLink = newsLink
    }
    constructor()

    //Setting Up Setters
    fun setNewsTittle(newsTittle: String){
        this.newsTittle = newsTittle
    }
    fun setNewsDate(newsDate: String){
        this.newsDate = newsDate
    }
    fun setNewsDescription(newsDescription: String){
        this.newsDescription = newsDescription
    }
    fun setNewsCategory(newsCategory: String){
        this.newsCategory = newsCategory
    }
    fun setNewsImage(newsImage: String){
        this.newsImage = newsImage
    }
    fun setNewsLink (newsLink: String){
        this.newsLink = newsLink
    }


    //Setting Up Getters
    fun getNewsTittle() : String{
        return this.newsTittle
    }
    fun getNewsDate() : String{
        return this.newsDate
    }
    fun getNewsDescription():String{
        return this.newsDescription
    }
    fun getNewsCategory():String{
        return this.newsCategory
    }
    fun getNewsImage() : String{
       return this.newsImage
    }
    fun getNewsLink() : String{
       return this.newsLink
    }

    /**
     * NOTE :
     * i could have used the data of this object without setting up get/set methods
     * but the values should be public
     * in this way the code is cleaner and safer
     * **/
}