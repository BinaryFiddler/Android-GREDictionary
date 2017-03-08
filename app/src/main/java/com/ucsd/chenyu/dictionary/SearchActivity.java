package com.ucsd.chenyu.dictionary;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.search_bar) EditText searchBar;
    @BindView(R.id.definition_field) ListView defnitionField;
    @BindView(R.id.web_search_button) Button webSearchButton;
    Database dict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        dict = new Database();
        dict.openDB(this);
        setSearchBarListener();
        setWebSearchButton();
    }

    private void setWebSearchButton() {
        webSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnWeb();
            }
        });
    }

    private void setSearchBarListener() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                List<String> potentialDefs = dict.searchForPotentialMatch(searchBar.getText().toString());
                updateDefnitionField(potentialDefs);
            }
        });
    }



    private void updateDefnitionField(List<String> potentialDefs) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, potentialDefs);
        if (adapter.isEmpty()){
            searchOnWeb();
        }
        defnitionField.setAdapter(adapter);
    }

    private void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dict.closeDB();
    }

    private void searchOnWeb() {
        final String word = searchBar.getText().toString();
        Ion.with(getApplicationContext())
                .load("http://api.datamuse.com/words?sp=" + word + "*&md=d")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONArray json = new JSONArray(result);
                            JSONObject jobject = json.getJSONObject(0);
                            JSONArray defs = jobject.getJSONArray("defs");
                            List<String> res = new ArrayList<>();
                            res.add(jobject.getString("word") + ": " + defs.getString(0));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, res);
                            defnitionField.setAdapter(adapter);
                            //get all definitions
//                            for(int i=0; i<defs.length(); i++){
//                                res.add(defs.getString(i));
//                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
}
