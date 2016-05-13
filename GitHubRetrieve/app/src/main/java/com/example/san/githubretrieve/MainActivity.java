package com.example.san.githubretrieve;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.net.URL;
import java.util.Map;


public class MainActivity extends ListActivity {
    private ProgressDialog dialog;
    private static final String API_URL = "https://api.github.com/repos/rails/rails/issues";

    // Define name tag
    private static final String TAG_NUMBER = "number";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_UPDATED_AT = "updated_at";
    private static final String TAG_BODY = "body";

    //User Object
    private static final String TAG_USER = "user";
    private static final String TAG_USER_LOGIN = "login";

    ArrayList<HashMap<String, String>> issueList = new ArrayList<HashMap<String, String>>();

    //To sort ArrayList of HashMap with Updated Date
    class MapComparator implements Comparator<Map<String,String>>{
        private final String keyToCompare;

        public MapComparator(String keyToCompare){
            this.keyToCompare = keyToCompare;
        }

        public int compare(Map<String, String> first, Map<String,String> second){
            String firstValue = first.get(keyToCompare);
            System.out.println("FirstValue: " + firstValue );
            String secondValue = second.get(keyToCompare);
            System.out.println("SecondValue: " + secondValue );
            return secondValue.compareTo(firstValue);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //issueList = new ArrayList<HashMap<String, String>>();
        ListView lv = getListView();

        // Listview onItemClickListener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String title = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String body = ((TextView) view.findViewById(R.id.full_body))
                        .getText().toString();
                String updatedDate = ((TextView) view.findViewById(R.id.updatedDate))
                        .getText().toString();
                String author = ((TextView) view.findViewById(R.id.author))
                        .getText().toString();

                // Starting single issue activity
                Intent intend = new Intent(getApplicationContext(),
                        SingleIssueActivity.class);
                intend.putExtra(TAG_TITLE, title);
                intend.putExtra(TAG_BODY, body);
                intend.putExtra(TAG_UPDATED_AT , updatedDate);
                intend.putExtra(TAG_USER_LOGIN,author);
                startActivity(intend);

            }
        });


        // Calling async task to get json
        new RetrieveTask().execute();
    } // End of onCreate


    private class RetrieveTask extends AsyncTask<Void, Void, Void>{


        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Progress start");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if (dialog.isShowing())
                dialog.dismiss();


             // Updating parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, issueList,
                    R.layout.list_item, new String[] { TAG_TITLE, TAG_BODY,
                    TAG_UPDATED_AT,"BODY_FULL",TAG_USER_LOGIN}, new int[] { R.id.title,
                    R.id.body, R.id.updatedDate, R.id.full_body,R.id.author});

            setListAdapter(adapter);
        } // End of onPostExecute

        protected Void doInBackground(Void... arg0){

            String jsonString = null;

            try{
                URL url = new URL(API_URL);
                //URL url = new URL(TESTING_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    //Getting inputstream from url and convert it to result string
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = br.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    br.close();
                    jsonString = sb.toString();

                    //Do the parsing here
                    if(jsonString != null){
                        try{
                            JSONArray jsonArray = new JSONArray(jsonString);
                            int len = jsonArray.length();

                            for (int i=0; i<len; i++){
                                JSONObject o = jsonArray.getJSONObject(i);
                                int Number = o.getInt(TAG_NUMBER);
                                String issues_Number = Integer.toString(Number);
                                String issues_Title = o.getString(TAG_TITLE);
                                String createdDate = o.getString(TAG_CREATED_AT);
                                String updatedDate = o.getString(TAG_UPDATED_AT);
                                String issues_Body_full = o.getString(TAG_BODY);
                                String issues_Body_resize = issues_Body_full.substring(0, Math.min(issues_Body_full.length(), 141));

                                // USER node is JSON Object
                                JSONObject user = o.getJSONObject(TAG_USER);
                                String user_login = user.getString(TAG_USER_LOGIN);

                                //Hashmap for single issue
                                HashMap<String, String> singleIssue = new HashMap<String, String>();
                                singleIssue.put(TAG_NUMBER,issues_Number);
                                singleIssue.put(TAG_TITLE,issues_Title);
                                singleIssue.put(TAG_CREATED_AT,createdDate);
                                singleIssue.put(TAG_UPDATED_AT,updatedDate);
                                singleIssue.put(TAG_BODY,issues_Body_resize);
                                singleIssue.put("BODY_FULL",issues_Body_full);
                                singleIssue.put(TAG_USER_LOGIN,user_login);

                                issueList.add(singleIssue);

                            }
                            //Sort Updated Date
                            Collections.sort(issueList, new MapComparator(TAG_UPDATED_AT));


                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.e("HttpUrlController", "Couldn't get any data from the url");
                    }

                }
                finally {
                    urlConnection.disconnect();

                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }

            return null;
        }//End of doInBackground

    }
}
