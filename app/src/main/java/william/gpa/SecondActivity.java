package william.gpa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    Button logoutButton;
    Button exitButton;
    static List<List<String>> selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        logoutButton = new Button(this);
        exitButton = new Button(this);
        selected = new ArrayList<>();

        execute();

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Do you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                                intent.putExtra("exit", "exit");
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                intent.putExtra("exit", "refresh");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    public void execute() {
        Intent intent = getIntent();
        for (int i = 0; i < intent.getIntExtra("size", 1); i++) {
            String[] element = intent.getStringArrayExtra("value" + i);
            List<String> elementList = Arrays.asList(element);
            selected.add(elementList);
        }

        Converter converter = new Converter();

        SpannableString title1 = new SpannableString("Your GPA is:");
        SpannableString clarify = new SpannableString("-- Conversion based on OLSAS Conversion Table\n");
        SpannableString title2 = new SpannableString("Course Detail:");

        title1.setSpan(new RelativeSizeSpan(2f), 0, title1.length(), 0); // set size
        title2.setSpan(new RelativeSizeSpan(2f), 0, title2.length(), 0); // set size
        clarify.setSpan(new RelativeSizeSpan(0.8f), 0, clarify.length(), 0); // set size

        TextView title1TextView = new TextView(this);
        title1TextView.setText(title1);

        TextView resultTextView = new TextView(this);
        resultTextView.setText(converter.getResult());

        TextView clarifyTextView = new TextView(this);
        clarifyTextView.setText(clarify);
        TextView title2TextView = new TextView(this);
        title2TextView.setText(title2);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(45, 0, 15, 0);

        container.addView(title1TextView, layoutParams);
        container.addView(resultTextView, layoutParams);
        container.addView(clarifyTextView, layoutParams);
        container.addView(title2TextView, layoutParams);

        List<List<String>> detail = Converter.selected;

        for (List<String> detailElement : detail) {
            LinearLayout elementContainer = new LinearLayout(this);
            elementContainer.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams elementLayoutParams;

            for (int i = 0; i < detailElement.size(); i++) {
                String element = detailElement.get(i);

                if (i == 0) {
                    elementLayoutParams = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                    elementLayoutParams.setMargins(45, 0, 0, 0);
                } else if (i == 1) {
                    elementLayoutParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                } else {
                    elementLayoutParams = new LinearLayout.LayoutParams(190, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                TextView elementTextView = new TextView(this);
                elementTextView.setText(element);
                elementContainer.addView(elementTextView, elementLayoutParams);
            }

            container.addView(elementContainer);
        }

        int viewHeight = MainActivity.viewHeight;
        int buttonHeight = 130;
        int height = viewHeight - buttonHeight * 2 + 10;

        RelativeLayout.LayoutParams exitButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                buttonHeight);
        exitButtonParams.setMargins(5, 0, 5, 0);
        exitButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        exitButton.setGravity(Gravity.CENTER);
        exitButton.setLayoutParams(exitButtonParams);
        exitButton.setText("Exit without logout YorkU Passport");

        RelativeLayout.LayoutParams logoutButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                buttonHeight);
        logoutButtonParams.setMargins(5, 0, 5, 120);
        logoutButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        logoutButton.setGravity(Gravity.CENTER);
        logoutButton.setLayoutParams(logoutButtonParams);
        logoutButton.setText("Logout YorkU Passport");

        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                height);

        RelativeLayout external = new RelativeLayout(this);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(container, containerParams);
        external.addView(scrollView, containerParams);
        external.addView(logoutButton);
        external.addView(exitButton);
        setContentView(external);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        intent.putExtra("exit", "back");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
