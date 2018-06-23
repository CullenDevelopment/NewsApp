package com.cullendevelopment.android.newsapp;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class NewsAdapter extends ArrayAdapter {

    public NewsAdapter(Context context, ArrayList<News> news) {
        /*
        Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        the second argument is used when the ArrayAdapter is populating a single TextView.
        Because this is a custom adapter for three TextViews the adapter is not
        going to use this second argument, so it can be any value. Here, we used 0.
        */
        super(context, 0, news);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

        }


        // Get the {@link News} object located at this position in the list
        News currentNews = (News) getItem(position);

        //getting the String to fill "Section Name" TextView in UI
        String sectionName = currentNews.getSectionName();

        //getting the String to fill "Item Title" TextView in UI
        String itemTitle = currentNews.getItemTitle();

        //getting the String to fill "Author" TextView in UI - if there is one
        String author = currentNews.getAuthor();

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView itemTitleView = (TextView) convertView.findViewById(R.id.item_title);
        /*
        Get the version number from the current location object and
        set this text on the news item TextView
        */
        itemTitleView.setText(itemTitle);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView sectionNameView = (TextView) convertView.findViewById(R.id.section_name);
        /*
        Get the version number from the current location object and
        set this text on the section name TextView
        */
        sectionNameView.setText(sectionName);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView authorView = (TextView) convertView.findViewById(R.id.author);
        /*
        Get the version number from the current news item object and
        set this text on the author TextView
        */
        authorView.setText(author);

        // Find the TextView with view ID date
        TextView dateView = (TextView) convertView.findViewById(R.id.date);

        // Find the TextView with view ID time
        TextView timeView = (TextView) convertView.findViewById(R.id.time);


        // Create a new Date object from the time and date String from Guardian Api
        String dateObject = (currentNews.getDate());

        /*
        Creates a date and time object date as presented on the Guardian Api (iso DateFormat) and
        stores it in the variable dateTimeFormat
        */
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        /*
        Creates a date object as I would like it to appear in the date TextView in the UI
        and stores it in the Variable "dateFormat"
        */
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");

        /*
        Creates a Time object as I would like it to appear in the time TextView in the UI
        and stores it in the Variable "timeFormat"
        */
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


        try {
            //Parsing the dateTimeFormat object and storing as a new object dateTimeParsed
            Date dateTimeParsed = dateTimeFormat.parse(dateObject);
            //Formatting the dateTimeParsed object in the order created in the dateFormat object and
            //storing it as a String which will fill the date TextView of the UI for the currentNews item
            String dateString = dateFormat.format(dateTimeParsed);
            //Formatting the dateTimeParsed object in the order created in the timeFormat object and
            //storing it as a String which will fill the time TextView of the UI for the currentNews item
            String timeString = timeFormat.format(dateTimeParsed);
            //setting the dateString in the UI in the dateView
            dateView.setText(dateString);
            //setting the timeString in the UI in the timeView
            timeView.setText(timeString);


        } catch (ParseException e) {
            e.printStackTrace();
            //if the time and date does not parse we need to know so we set a parse exception  log message
            Log.e("News Adapter", "Parse Exception");
        }

        // Return the whole list item layout (containing 3 TextViews)
        return convertView;
    }

}
