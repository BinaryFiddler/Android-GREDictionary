package com.ucsd.chenyu.dictionary;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    Map<String, String> dict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initialization();
    }

    private void initialization() {
        Intent intent = getIntent();
        dict = (Map<String, String>) intent.getSerializableExtra("dictionary");
    }

    public void searchAndDisplay(View view) {
        TextView def = (TextView)findViewById(R.id.definition);
        WebView webview = (WebView)findViewById(R.id.web_search);
        EditText edit = (EditText)findViewById(R.id.word);
        if(dict.containsKey(edit.getText().toString())){
            def.setText(dict.get(edit.getText().toString()));
            def.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);
        }
        else{
            String url = new String("http://www.dictionary.com/browse/" + edit.getText().toString());
            webview.loadUrl(url);
            def.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
        }
        hideKeyBoard(view);
    }

    private void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
