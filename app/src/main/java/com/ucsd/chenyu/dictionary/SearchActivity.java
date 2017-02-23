package com.ucsd.chenyu.dictionary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
        EditText edit = (EditText)findViewById(R.id.word);
        if(dict.containsKey(edit.getText().toString()))
            def.setText(dict.get(edit.getText().toString()));
        else
            def.setText("Word not in current dictionary");
        hideKeyBoard(view);
    }

    private void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
