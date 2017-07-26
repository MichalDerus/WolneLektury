package com.derus.wolnelektury;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.derus.wolnelektury.fragment.BookDetailFragment;

public class ReaderActivity extends AppCompatActivity {

    public static final String SAVED_POSITION = "position";
    private Toolbar mToolbar;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        webView = (WebView) findViewById(R.id.web_view_reader);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_reader);
        setSupportActionBar(mToolbar);
        showBackButton();

        Intent intent = getIntent();
        String url = intent.getStringExtra(BookDetailFragment.EXTRA_URL);
        String title = intent.getStringExtra(BookDetailFragment.EXTRA_TITLE);
        setTitle(title);

        WebSettings setting = webView.getSettings();
        setting.setDefaultFontSize(12);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
            webView.scrollTo(0, savedInstanceState.getInt(SAVED_POSITION));
        }
        else
            webView.loadUrl(url);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        outState.putInt(SAVED_POSITION, webView.getScrollY());
    }

    private void showBackButton() {

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// show back button

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
