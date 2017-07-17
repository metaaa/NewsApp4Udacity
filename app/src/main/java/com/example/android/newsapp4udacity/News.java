package com.example.android.newsapp4udacity;

public class News {

    //CREATING THE VARIABLES WHICH WILL CONTAIN THE NEEDED INFORMATION
    private String mSection;        //SECTION OF THE ARTICLE
    private String mTitle;          //TITLE OF THE ARTICLE
    private String mAuthor;         //AUTHOR(S) OF THE ARTICLE
    private String mPubDate;        //PUBLISH DATE OF THE ARTICLE
    private String mUrl;            //LINK TO THE ARTICLE
    private String mTrailText;      //SHORT DESCRIPTION OF THE ARTICLE
    private String mWordCount;         //LENGTH OF THE ARTICLE IN WORDS

    //PUBLIC CONSTRUCTOR
    public News(String section, String title, String author, String pubDate, String url, String trailText, String wordCount){
        mSection = section;
        mTitle = title;
        mAuthor = author;
        mPubDate = pubDate;
        mUrl = url;
        mTrailText = trailText;
        mWordCount = wordCount;
    }
    //GETTER METHODS
    public String getmSection() {
        return mSection;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmPubDate() {
        return mPubDate;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmTrailText() {
        return mTrailText;
    }

    public String getmWordCount() {
        return mWordCount;
    }


}
