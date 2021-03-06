package com.cullendevelopment.android.newsapp;

import android.text.TextUtils;
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

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Returns new URL object from the given string URL or gives error if not possible.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            //log message to help with error detection
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Query the Guardian Api dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        try {
            //creating a slight delay to get Url
            Thread.sleep(2000);
        } //but creating an error log message if creating Url interrupted
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            //Error log if Http request not fulfilled
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link newsItems}s
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link newsItems}s
        return news;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
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
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                //error log returns response code if not 200(success)
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
            //error log informs us if there is a problem getting JSON results
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() throws JSONException {

    }


    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();

        /*
        Try to parse the JSON response string. If there's a problem with the way the JSON
        is formatted, a JSONException exception object will be thrown.
        Catch the exception so the app doesn't crash, and print the error message to the logs.
        */
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject responseObject = baseJsonResponse.getJSONObject("response");

            /*
            Extract the JSONArray associated with the key called "results",
            which represents a list of results (or news items).
            */
            JSONArray newsArray = responseObject.getJSONArray("results");

            // For each item of news in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single item of news at position i within the list of news items
                JSONObject currentNews = newsArray.getJSONObject(i);

                /*
                For a given news items, extract the JSONObject associated with the
                key called "properties", which represents a list of all properties
                for that news item.
                Extract the value for the key called "section name"
                */

                String sectionName = currentNews.getString("sectionName");

                // Extract the value for the key called "web title"
                String itemTitle = currentNews.getString("webTitle");

                // Extract the value for the key called "webPublicationDate"
                String date = currentNews.getString("webPublicationDate");

                // Extract the value for the key called "url"
                String url = currentNews.getString("webUrl");
                //access the tags array within the results
                JSONArray tagArray = currentNews.getJSONArray("tags");

                //creates an if statement that an author is shown when the tagarray is >=1
                if (tagArray.length() >= 1) {
                    // this creates a for loop that goes through the arraylist of tags no matter how long it is
                    for (int j = 0; j < 1; j++) {

                        // Get a single tagsobject in the  tagarray
                        JSONObject tagsObject = tagArray.getJSONObject(j);
                        // Extract the value for the key called "webTitle"
                        String author = tagsObject.getString("webTitle");


                        /*
                        Create a new {@link news} object with the section Name, title, date and time, author ,
                        and url from the JSON response.
                        */
                        News newsItem = new News(sectionName, itemTitle, date, author, url);

                        // Add the new {@link News} to the list of news items.
                        news.add(newsItem);
                    }

                }
            }
        } catch (JSONException e) {
            /*
            If an error is thrown when executing any of the above statements in the "try" block,
            catch the exception here, so the app doesn't crash. Print a log message
            with the message from the exception.
            */
            Log.e("QueryUtils", "Problem parsing the news item JSON results", e);
        }
        // Return the list of News Items
        return news;
    }
}

