package com.ucsd.chenyu.dictionary;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private Map<String, String> dict;
    private List<String> words;
    private List<String> definitions;
    private int totalPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalPoints = 0;
        loadDictionary();
        Log.d("Debug", "End loading");
        pickWord();
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
        Log.d("Debug", Integer.toString(words.size()));

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


    public void pick(View view) {
        pickWord();
    }
}
