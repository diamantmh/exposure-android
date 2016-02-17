package io.github.getExposure;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationView extends AppCompatActivity {
    LinearLayout commentArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);
        commentArea = (LinearLayout) findViewById(R.id.comments);
        String[] comments = {"dopest photo ever!", "I love this pic", "show me your ways!"};
        fillComments(comments);
    }

    private void fillComments(String[] comments) {
        View comment;
        TextView commentator;
        TextView commentDate;
        TextView commentText;
        LayoutInflater inflater = getLayoutInflater();

        for (String s : comments) {
            comment = inflater.inflate(R.layout.comment_layout, null);
            commentator = (TextView) comment.findViewById(R.id.author);
            commentDate = (TextView) comment.findViewById(R.id.date);
            commentText = (TextView) comment.findViewById(R.id.content);
            commentator.setText("Test commentator");
            commentDate.setText("12-12-2012");
            commentText.setText(s);
            commentArea.addView(comment);
        }
    }

}
