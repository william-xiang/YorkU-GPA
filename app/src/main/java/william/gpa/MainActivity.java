package william.gpa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    TextView text;
    Button logoutButton;
    Button exitButton;
    static String realUrl;
    String cookie;
    Converter converter;
    CookieManager cookieManager;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = new TextView(this);
        logoutButton = new Button(this);
        exitButton = new Button(this);

        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        //cookieManager.removeAllCookies(null);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                realUrl = url;
                cookie = cookieManager.getCookie(realUrl);
                super.onPageFinished(view, url);
                if (url.equals("https://wrem.sis.yorku.ca/Apps/WebObjects/ydml.woa/wa/DirectAction/document?name=CourseListv1")) {
                    ((ConstraintLayout)findViewById(R.id.mainLayout)).removeView(webView);
                    new doit().execute();
                }
            }
        });

        ConnectivityManager manager =(ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            webView.loadUrl("https://wrem.sis.yorku.ca/Apps/WebObjects/ydml.woa/wa/DirectAction/document?name=CourseListv1");
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Bad internet connection!\nDo you want to refresh the app?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //finish();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
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

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                cookieManager.removeAllCookies(null);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
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
    }

    public class doit extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(realUrl).header("Cookie", cookie).get();
                Elements elements = doc.select(".bodytext td");
                converter = new Converter(elements);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int viewHeight = findViewById(R.id.mainLayout).getHeight();
            int buttonHeight = 130;
            int height = viewHeight - buttonHeight * 2 - 10;

            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    height);
            textViewParams.setMargins(15, 15, 15, 15);
            text.setMovementMethod(new ScrollingMovementMethod());
            text.setText(converter.getResult());

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
            //logoutButtonParams.addRule(RelativeLayout.BELOW, text.getId());
            logoutButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            logoutButton.setGravity(Gravity.CENTER);
            logoutButton.setLayoutParams(logoutButtonParams);
            logoutButton.setText("Logout YorkU Passport");

            RelativeLayout resultLayout = new RelativeLayout(MainActivity.this);
            resultLayout.addView(text, textViewParams);
            resultLayout.addView(logoutButton);
            resultLayout.addView(exitButton);
            setContentView(resultLayout);
        }
    }
}


