package com.ucsd.chenyu.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    @BindView(R.id.word_field) TextView wordField;
    @BindView(R.id.choices_field) ListView choicesField;
    @BindView(R.id.pronounce) TextView pronounce;
    @BindView(R.id.points_field) TextView pointsField;
    @BindView(R.id.next_word) Button nextWord;
    @BindView(R.id.search_field) Button searchField;

//    @BindView(R.id.search_field) FloatingActionButton searchField;



    private Database dict;
    private TextToSpeech tts;
    private int totalPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        dict = new Database();
        dict.openDB(this);
        totalPoints = 0;
        setUpPrononciationListeners();
        setUpNextWordListener();
        setUpSearchButtonListener();
        pickAndPaintWords();
    }

    private void setUpSearchButtonListener() {
        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpNextWordListener() {
        nextWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAndPaintWords();
            }
        });
    }

    private void setUpPrononciationListeners() {
        pronounce.setText("\uD83D\uDD0A");
        pronounce.setTextSize(20);
        pronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
            private void speak() {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i != TextToSpeech.ERROR) {
                            tts.speak(wordField.getText(), TextToSpeech.QUEUE_FLUSH, null, "pronounce");
                        }
                    }
                });
            }
        });
    }


    private void pickAndPaintWords() {
        final Map<String, String> map = dict.pickWords();
        List<String> keys = new ArrayList<>(map.keySet());
        List<String> choices = new ArrayList<>(map.values());
        Collections.shuffle(keys);
        Collections.shuffle(choices);

        final String word = keys.get(0);
        wordField.setText(word);

        ArrayAdapter<String> choiceAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, choices);
        choicesField.setAdapter(choiceAdapter);
        choicesField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String defPicked = parent.getItemAtPosition(position).toString();
                if (map.get(word).equals(defPicked)){
                    totalPoints++;
                    view.setBackgroundColor(Color.GREEN);
                    pointsField.setText("AWESOME! POINTS + 1: " + totalPoints);
                    dict.updateAttempts(word, true);
                }else{
                    totalPoints--;
                    view.setBackgroundColor(Color.RED);
                    pointsField.setText("Uh... POINTS - 1: " + totalPoints);
                    dict.updateAttempts(word, true);
                }
                for (int i=0; i<5; i++){
                    if (map.get(word).equals(parent.getItemAtPosition(i).toString())) {
                        parent.getChildAt(i).setBackgroundColor(Color.GREEN);
                        break;
                    }
                }
                //disable second choice
                parent.setOnItemClickListener(null);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dict.openDB(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dict.closeDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dict.closeDB();
    }
}


//code for initialize database
//db = openOrCreateDatabase("Dictionary", MODE_PRIVATE, null);
//
//        db.execSQL("DROP TABLE IF EXISTS wordlist;");
//        db.execSQL(DATABASE_CREATE);
//        Scanner scan = new Scanner(getResources().openRawResource(R.raw.wordlist));
//        while (scan.hasNextLine()){
//            String[] wordDef = scan.nextLine().split(" - ");
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_WORD, wordDef[0]);
//            values.put(COLUMN_DEFINITION, wordDef[1]);
//            values.put(COLUMN_ATTEMPT, 0);
//            values.put(COLUMN_WRONG_ATTMPT, 0);
//            db.insertWithOnConflict(TABLE_COMMENT, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//        }
//        scan.close();
//        Cursor cursor = db.rawQuery("SELECT * FROM wordlist;", null);
//        Log.d("database", cursor.getCount() + "");
