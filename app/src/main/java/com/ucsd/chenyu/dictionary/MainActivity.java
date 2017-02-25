package com.ucsd.chenyu.dictionary;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private final static int REQ_CODE_ADD_WORDS = 1;
    private Map<String, String> dict;
    private List<String> words;
    private List<String> definitions;
    private int totalPoints;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateActivity();
    }

    public void pick(View view) {
        pickWord();
    }

    //initialization
    private void onCreateActivity() {
        totalPoints = 0;
        loadDictionary();
        pickWord();
        initializePronunciationButton();
    }

    private void initializePronunciationButton() {
        Button pronounce = (Button)findViewById(R.id.pronunciation);
        pronounce.setText("\uD83D\uDD0A");
        pronounce.setTextSize(20);
        pronounce.setBackgroundColor(Color.TRANSPARENT);
        pronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView wordField = (TextView) findViewById(R.id.word_field);
                speak(wordField);
            }
        });
    }

    void loadDictionary(){
        dict = new HashMap<>();
        words = new ArrayList<>();
        definitions = new ArrayList<>();
        Scanner scan = new Scanner(getResources().openRawResource(R.raw.wordlist));
        while (scan.hasNext()){
            String[] line = scan.nextLine().split(" - ");
            if (line.length < 2) continue;
            dict.put(line[0], line[1]);
            words.add(line[0]);
            definitions.add(line[1]);
        }
        scan.close();
    }

    private void pickWord(){

        // select relavant UI elements
        final TextView wordField = (TextView) findViewById(R.id.word_field);
        final TextView pointsField = (TextView) findViewById(R.id.points_field);
        ListView choiceField = (ListView) findViewById(R.id.choices);

        Random rand = new Random();
        int dictSize = words.size();

        // setting the new word on screen
        String newWord = words.get(rand.nextInt(dictSize));
        wordField.setText(newWord);
        speak(wordField);

        //get 5 random definitions
        List<String> choices = new ArrayList<>();
        choices.add(dict.get(newWord));
        while(choices.size() < 5){
            String nextDef = definitions.get(rand.nextInt(dictSize));

            if (choices.contains(nextDef))
                continue;
            else
                choices.add(nextDef);
        }
        Collections.shuffle(choices);

        //set choices on screen, set event listener
        ArrayAdapter<String> choiceAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, choices);
        choiceField.setAdapter(choiceAdapter);
        choiceField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String defPicked = parent.getItemAtPosition(position).toString();
                String newWord = wordField.getText().toString();
                if (dict.get(newWord).equals(defPicked)){
                    totalPoints++;
                    view.setBackgroundColor(Color.GREEN);
                    pointsField.setText("AWESOME! POINTS + 1: " + totalPoints);
                }else{
                    totalPoints--;
                    view.setBackgroundColor(Color.RED);
                    pointsField.setText("Uh... POINTS - 1: " + totalPoints);
                }
                for (int i=0; i<5; i++){
                    if (dict.get(newWord).equals(parent.getItemAtPosition(i).toString())) {
                        parent.getChildAt(i).setBackgroundColor(Color.GREEN);
                        break;
                    }
                }
                //disable second choice
                parent.setOnItemClickListener(null);
            }
        });

    }

    private void speak(final TextView wordField) {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.speak(wordField.getText(), TextToSpeech.QUEUE_FLUSH, null, "pronounce");
                }
            }
        });
    }

    public void searchWord(View view) {
        //.class syntax is a class object representing that class
        //Intent(current activity, target activity)
//        Intent intent = new Intent(this, AddNewWords.class);
//        startActivityForResult(intent, REQ_CODE_ADD_WORDS);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("dictionary", (Serializable) dict);
        startActivity(intent);
    }

    //preserve points through phone rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("points", totalPoints);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        totalPoints = savedInstanceState.getInt("points", 0);
        TextView pointsField = (TextView)findViewById(R.id.points_field);
        pointsField.setText("Points: " + totalPoints);
    }
}
