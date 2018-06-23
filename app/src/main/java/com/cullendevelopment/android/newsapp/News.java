package com.cullendevelopment.android.newsapp;



public class News {

    /** Private string for Section Name */
    private String mSectionName;

    /**
     * private string value
     * Of item Title
     */
    private String mItemTitle;

    /**
     * private string value
     * Of Date (and time)
     */
    private String mDate;

    /**
     * private string value
     * Of Author/contributor
     */
    private String mAuthor;

    /**
     *
     *private string value of URL
     *
     */
    private String mUrl;


    //Constructor with four inputs
    public News(String sectionName, String itemTitle, String date, String author, String url) {
        mSectionName = sectionName;
        mItemTitle = itemTitle;
        mDate = date;
        mAuthor = author;
        mUrl = url;
    }

    /**
     * Get the Section Name
     */
    public String getSectionName() {

        return mSectionName;
    }

    /**
     * Get the Item Title
     */
    public String getItemTitle() {

        return mItemTitle;
    }

    /**
     * Get the Date and time
     */
    public String getDate() {

        return mDate;
    }

    /**
     * Get the name of the author/contributor.
     */
    public String getAuthor() {

        return mAuthor;
    }

    /**
     *
     * Get the URL
     */
    public String getUrl() {
        return mUrl;
    }

}
