package com.example.san.githubretrieve;


 import android.app.Activity;
 import android.content.Intent;
 import android.os.Bundle;
 import android.widget.TextView;


public class SingleIssueActivity  extends Activity {

    // JSON node keys
    private static final String TAG_TITLE = "title";
    private static final String TAG_UPDATED_AT = "updated_at";
    private static final String TAG_BODY = "body";
    private static final String TAG_FULL_BODY = "BODY_FULL";
    private static final String TAG_USER_LOGIN = "login";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_issue);

        // getting intent data
        Intent intend = getIntent();

        // Get JSON values from previous intent
        String title = intend.getStringExtra(TAG_TITLE);
        String updatedDate = intend.getStringExtra(TAG_UPDATED_AT);
        String body = intend.getStringExtra(TAG_BODY);
        String full_body = intend.getStringExtra(TAG_FULL_BODY);
        String author = intend.getStringExtra(TAG_USER_LOGIN);

        // Displaying all values on the screen
        TextView lblTitle = (TextView) findViewById(R.id.title_label);
        TextView lblUpdatedDate = (TextView) findViewById(R.id.updatedDate_label);
        TextView lblBody = (TextView) findViewById(R.id.body_label);
        TextView lblFullBody = (TextView) findViewById(R.id.full_body_label);
        TextView lblAuthor = (TextView) findViewById(R.id.author_label);


        lblTitle.setText(title);
        lblUpdatedDate.setText(updatedDate);
        lblBody.setText(body);
        lblFullBody.setText(full_body);
        lblAuthor.setText(author);
    }
}
