package com.example.android.newsapp4udacity;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


//THIS WILL MAKE THE REQUEST AND PARSE THE JSON
public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getName();

    //PRIVATE CONSTRUCTOR
    private QueryUtils() {
    }

    //THIS METHOD WILL FETCH THE DATA FROM THE API
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        // Extract relevant fields from the JSON response and create a list of news
        List<News> news = extractNewsData(jsonResponse);
        return news;
    }

    //THIS CREATES THE URL FROM A STRING
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return url;
    }

    //THIS METHOD WILL MAKE THE HTTP REQUEST AND GET THE DATA
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }
     //THIS METHOD CONVERTS THE INPUTSTREAM (THE WHOLE JSON RESPONSE) INTO A STRING
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //PARSING THE JSON
    public static List<News> extractNewsData(String jsonResponse) {
        //NEW ARRAYLIST TO STORE THE INFORMATION ABOUT EACH NEW
        ArrayList<News> newsList = new ArrayList<>();
        try {
            //THIS IS THE ROOT
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject response = baseJsonResponse.getJSONObject("response");

            if (response.has("results")) {
                JSONArray newsArray = response.getJSONArray("results");
                //THIS WILL GO THROUGH THE WHOLE JSON RESPONSE TO GET THE DATA FROM EACH NEW
                for (int i = 0; i < newsArray.length(); i++) {

                    JSONObject singleNews = newsArray.getJSONObject(i);

                    //GETTING THE TITLE OF THE ARTICLE
                    String webTitle = "";
                    if (singleNews.has("webTitle")) {
                        webTitle = singleNews.getString("webTitle");
                    }
                    //GETTING THE SECTION OF THE ARTICLE
                    String sectionName = "";
                    if (singleNews.has("sectionName")) {
                        sectionName = singleNews.getString("sectionName");
                    }
                    //GETTING THE PUBLISH DATE OF THE ARTICLE
                    String webPublicationDate = "";
                    if (singleNews.has("webPublicationDate")) {
                        webPublicationDate = singleNews.getString("webPublicationDate");
                    }
                    //GETTING THE URL TO THE ARTICLE
                    String webUrl = "";
                    if (singleNews.has("webUrl")) {
                        webUrl = singleNews.getString("webUrl");
                    }
                    //GETTING THE AUTHOR(S) OF THE ARTICLE
                    JSONArray tagsArray;
                    String authorName = "";
                    if (singleNews.has("tags")) {
                        tagsArray = singleNews.getJSONArray("tags");
                        if (tagsArray.length() > 0) {
                            for (int author = 0; author < 1; author++) {
                                JSONObject tags = tagsArray.getJSONObject(author);
                                if (tags.has("webTitle")) {
                                    authorName = tags.getString("webTitle");
                                }
                            }
                        }
                    }
                    //GETTING THE SHORT DESCRIPTION AND THE WORDCOUNT
                    String trailText = "";
                    String wordCount = "";
                    if (singleNews.has("fields")) {
                        JSONObject fields = singleNews.getJSONObject("fields");
                        if (fields.has("trailText")) {
                            trailText = fields.getString("trailText");
                        }
                        if (fields.has("wordcount")){
                            wordCount = fields.getString("wordcount");
                        }
                    }


                    //CREATING A NEW LIST WITH ALL THE DATA
                    News news = new News(sectionName, webTitle, authorName, webPublicationDate, webUrl, trailText, wordCount);
                    //ADDING THE NEW NEWS TO THE LIST
                    newsList.add(news);
                }
            } else {
                Log.v(LOG_TAG, "No results found");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return newsList;
    }
}
