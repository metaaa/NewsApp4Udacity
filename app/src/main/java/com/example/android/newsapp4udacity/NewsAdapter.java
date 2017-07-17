package com.example.android.newsapp4udacity;


import android.app.Activity;
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
import java.util.Locale;

import static com.example.android.newsapp4udacity.QueryUtils.LOG_TAG;


public class NewsAdapter extends ArrayAdapter<News> {
    //PUBLIC CONSTRUCTOR
    public NewsAdapter(Activity context, ArrayList<News> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (listView == null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list, parent, false);
        }


        News currentItem = getItem(position);
        //SETTING THE TITLE OF THE ARTICLE
        TextView titleText = (TextView) listView.findViewById(R.id.title_text);
        titleText.setText(currentItem.getmTitle());
        //SETTING THE AUTHOR(S) OF THE ARTICLE
        TextView authorText = (TextView) listView.findViewById(R.id.author_text);
        authorText.setText(currentItem.getmAuthor());
        //SETTING THE PUBLISH DATE OF THE ARTICLE
        TextView publishDateText = (TextView) listView.findViewById(R.id.publish_date_text);
        publishDateText.setText(formatDate(currentItem.getmPubDate()));
        //SETTING THE SECTION OF THE ARTICLE
        TextView sectionText = (TextView) listView.findViewById(R.id.section_text);
        sectionText.setText(currentItem.getmSection());
        //SETTING THE SHORT DESCRIPTION OF THE ARTICLE
        TextView trailTextText = (TextView) listView.findViewById(R.id.trailText_text);
        trailTextText.setText(currentItem.getmTrailText());
        //SETTING THE WORDCOUNT OF THE ARTICLE
        TextView wordCountText = (TextView) listView.findViewById(R.id.word_count_text);
        String temp = currentItem.getmWordCount();
        String wordLabel = "Words: " + temp;
        wordCountText.setText(wordLabel);

        return listView;
    }

    public String formatDate(String date){
        String newFormatData = "";
        if (date.length() >= 10) {
            // SPLITTING THE OBTAINED DATA AFTER 10 CHARS "2017-07-15T21:30:35Z" --> 2017-07-15
            CharSequence splitDate = date.subSequence(0, 10);
            try{
                Date formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(splitDate.toString());
                newFormatData = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(formatDate);
            }catch (ParseException e){
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            newFormatData = date;
        }
        return newFormatData;
    }
}
