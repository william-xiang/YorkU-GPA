package william.gpa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    TextView text;
    Button calButton;
    static String realUrl;
    String cookie;
    Collector collector;
    CookieManager cookieManager;
    List<List<View>> views;
    List<List<String>> selected;
    String code;
    Spinner subjectSpinner;
    static int viewHeight;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = new TextView(this);
        calButton = new Button(this);
        subjectSpinner = new Spinner(this);
        myPreferences
                = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        myEditor = myPreferences.edit();

        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript

        webView.setWebViewClient(new WebViewClient() {
            int i = 0;

            @Override
            public void onPageFinished(WebView view, String url) {
                realUrl = url;
                cookie = cookieManager.getCookie(realUrl);

                if (i == 2) {
                    myEditor.putBoolean("hasCookie", true);
                    myEditor.commit();
                }

                super.onPageFinished(view, url);

                if (url.equals("https://wrem.sis.yorku.ca/Apps/WebObjects/ydml.woa/wa/DirectAction/document?name=CourseListv1")) {
                    new doit().execute();
                }

                i++;
            }
        });

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null != activeNetwork) {
            boolean hasCookie = myPreferences.getBoolean("hasCookie", false);

            if (hasCookie) {
                webView.setVisibility(View.INVISIBLE);
            }
            webView.loadUrl("https://wrem.sis.yorku.ca/Apps/WebObjects/ydml.woa/wa/DirectAction/document?name=CourseListv1");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Bad internet connection!\nDo you want to refresh the app?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = new ArrayList<>();

                for (List<View> view : views) {
                    CheckBox checkBox = (CheckBox)view.get(4);
                    if (checkBox.isChecked()) {
                        List<String> selectedElement = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            selectedElement.add(((TextView)view.get(i)).getText().toString());
                        }
                        selected.add(selectedElement);
                    }
                }

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                int i = 0;
                for (; i < selected.size(); i++) {
                    String[] element = new String[selected.get(i).size()];

                    for (int j = 0; j < selected.get(i).size(); j++) {
                        element[j] = selected.get(i).get(j);
                    }
                    intent.putExtra("value" + i, element);
                }

                intent.putExtra("size", i);
                startActivityForResult(intent, 1);
            }
        });

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Object item = parent.getItemAtPosition(position);
                    String selectedItem = item.toString();
                    for (int i = 0; i < views.size(); i++) {
                        String subjectName = ((TextView)views.get(i).get(1)).getText().toString().split(" ")[0];
                        if (selectedItem.equals(subjectName)) {
                            ((CheckBox)views.get(i).get(4)).setChecked(true);
                        } else {
                            ((CheckBox)views.get(i).get(4)).setChecked(false);
                        }
                    }
                } else {
                    for (int i = 0; i < views.size(); i++) {
                        ((CheckBox) views.get(i).get(4)).setChecked(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            code = data.getStringExtra("exit");
            if (code.equals("exit")) {
                finish();
            }
            else if (code.equals("refresh")) {
                cookieManager.removeAllCookies(null);
                myEditor.putBoolean("hasCookie", false);
                myEditor.commit();

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

    public class doit extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(realUrl).header("Cookie", cookie).get();
                Elements elements = doc.select(".bodytext td");
                collector = new Collector(elements);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SpannableString title = new SpannableString("Course Detail:");
            title.setSpan(new RelativeSizeSpan(2f), 0, title.length(), 0); // set size
            Context context = MainActivity.this;
            TextView titleTextView = new TextView(context);
            titleTextView.setText(title);

            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(35, 20, 15, 20);

            container.addView(titleTextView, layoutParams);

            List<List<String>> detail = collector.getDetail();
            views = new ArrayList<>();

            LinearLayout subjectContainer = new LinearLayout(context);
            subjectContainer.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams subjectLayoutParams;

            SpannableString subject = new SpannableString("Select subject(Select all by default): ");
            subject.setSpan(new RelativeSizeSpan(1f), 0, title.length(), 0); // set size
            TextView subjectTextView = new TextView(context);
            subjectTextView.setText(subject);
            subjectLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            subjectLayoutParams.setMargins(45, 0, 20, 0);
            subjectContainer.addView(subjectTextView, subjectLayoutParams);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, collector.getSubjects()); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(spinnerArrayAdapter);
            subjectContainer.addView(subjectSpinner);

            container.addView(subjectContainer);

            for (int i = 0; i < detail.size(); i++) {
                List<String> detailElement = detail.get(i);
                List<View> view = new ArrayList<>();

                LinearLayout elementContainer = new LinearLayout(context);
                elementContainer.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams elementLayoutParams;

                for (int j = 0; j < detailElement.size(); j++) {
                    String element = detailElement.get(j);

                    if (j == 0) {
                        elementLayoutParams = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT);
                        elementLayoutParams.setMargins(45, 0, 0, 0);
                    } else if (j == 1) {
                        elementLayoutParams = new LinearLayout.LayoutParams(280, LinearLayout.LayoutParams.WRAP_CONTENT);
                    } else if (j == 2) {
                        elementLayoutParams = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT);
                    } else {
                        elementLayoutParams = new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }

                    TextView elementTextView = new TextView(context);
                    elementTextView.setText(element);
                    elementContainer.addView(elementTextView, elementLayoutParams);
                    view.add(elementTextView);
                }

                elementLayoutParams = new LinearLayout.LayoutParams(140, LinearLayout.LayoutParams.WRAP_CONTENT);
                CheckBox checkBox = new CheckBox(context);
                checkBox.setChecked(true);
                elementContainer.addView(checkBox, elementLayoutParams);
                container.addView(elementContainer);
                view.add(checkBox);
                views.add(view);
            }

            viewHeight = findViewById(R.id.mainLayout).getHeight();
            int buttonHeight = 130;
            int height = viewHeight - buttonHeight;

            RelativeLayout.LayoutParams calButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    buttonHeight);
            calButtonParams.setMargins(5, 0, 5, 0);
            calButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            calButton.setGravity(Gravity.CENTER);
            calButton.setLayoutParams(calButtonParams);
            calButton.setText("Calculate GPA");

            RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    height);

            RelativeLayout external = new RelativeLayout(context);
            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(container, containerParams);
            external.addView(scrollView, containerParams);
            external.addView(calButton);
            external.setId(R.id.mainLayout);
            setContentView(external);
        }
    }
}


