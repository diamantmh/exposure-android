package io.github.getExposure.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import io.github.getExposure.R;

/**
 * @author Michael Diamant
 * @version  1.0
 * @since 3-8-16
 *
 * PopUpCategoriesView class represents controller for categories layout.
 * handles the selecting of different categories for a new ExposureLocation object
 * and passes this data back to the PostView controller.
 */
public class PopUpCategoriesView extends AppCompatActivity {
    private ViewGroup rel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_layout);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;
        // set up the categories view
        getWindow().setLayout((int) (w * .8), (int) (h * .6));
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        rel = (ViewGroup) root.getChildAt(0);

        // put the selected categories into the return intent
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                for(int i = 0; i < rel.getChildCount(); i++) {
                    if (rel.getChildAt(i) instanceof CheckBox) {
                        CheckBox child = (CheckBox) rel.getChildAt(i);
                        if(child.isChecked()) {
                            returnIntent.putExtra((String) child.getText(), child.isChecked());
                        }
                    }
                }
                setResult(AppCompatActivity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
