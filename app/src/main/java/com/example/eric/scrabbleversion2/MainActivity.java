package com.example.eric.scrabbleversion2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button findResults = (Button) findViewById(R.id.find_results_button);
        Button p1 = (Button) findViewById(R.id.button1);
        Button p2 = (Button) findViewById(R.id.button2);
        Button p3 = (Button) findViewById(R.id.button3);
        Button p4 = (Button) findViewById(R.id.button4);
        Button timer = (Button) findViewById(R.id.timer_button);
        final ListView listView = (ListView) findViewById(R.id.listView);


        //read in file chunk and populate hashtable chunk
        BufferedReader reader;
        ArrayList<String> dictionaryArrayList = new ArrayList<String>();
        try{
            final InputStream file = getAssets().open("C:\\Users\\Eric\\Documents\\ScrabbleVersion2\\app\\src\\main\\assets\\dictionary2.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while(line != null){
                if (line.length() <= 10) {
                    // add word to arrayList
                    dictionaryArrayList.add(line);
                }
                line = reader.readLine();
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        for (int i=0; i<dictionaryArrayList.size(); i++) {
           Permutations.dictionary.put(dictionaryArrayList.get(i).hashCode(), dictionaryArrayList.get(i));
        }
        //end chunk



        findResults.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText input_letters = (EditText) findViewById(R.id.input_letters);
                String letterBank = input_letters.getText().toString();
                Permutations.combine(letterBank, new StringBuffer(), 0);
                for (String str: Permutations.combinations) {
                    Permutations.allPermutations.addAll(Permutations.permutation(str));
                }
                for(int i = 0; i< Permutations.allPermutations.size(); i++) {
                    String possibleWord = Permutations.allPermutations.get(i).toUpperCase();
                    int hash = possibleWord.hashCode();
                    if (Permutations.dictionary.get(hash) != null) {
                        Permutations.matches.add(Permutations.dictionary.get(hash));
                    }
                }
                Set<String> s = new HashSet<String>(Permutations.matches);
                ArrayList<String> results = new ArrayList<String>(s);
                for (int i=0; i<results.size(); i++) {
                    if (results.get(i).length() > letterBank.length()) {
                        results.remove(i);
                    }
                }
                for (int i=0; i<results.size(); i++) {
                    if (Sort.containsAllChars(letterBank, results.get(i).toLowerCase()) == false) {
                        results.remove(i);
                    }
                }
                Sort.sortAlphabetically(results);
//                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results);
//                listView.setAdapter(itemsAdapter);
            }
        });


        p1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editPlayerOnePoints = (EditText) findViewById(R.id.add_p1_pts_text_field);
                TextView player1Score = (TextView) findViewById(R.id.player_one_score_tally);
                int pointsEarnedForTurn = Integer.parseInt(editPlayerOnePoints.getText().toString());
                player1Score.setText("" + (Integer.parseInt(player1Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerOnePoints.setText("");
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editPlayerTwoPoints = (EditText) findViewById(R.id.add_p2_pts_text_field);
                TextView player2Score = (TextView) findViewById(R.id.player_two_score_tally);
                int pointsEarnedForTurn = Integer.parseInt(editPlayerTwoPoints.getText().toString());
                player2Score.setText("" + (Integer.parseInt(player2Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerTwoPoints.setText("");
            }
        });

        p3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editPlayerThreePoints = (EditText) findViewById(R.id.add_p3_pts_text_field);
                TextView player3Score = (TextView) findViewById(R.id.player_three_score_tally);
                int pointsEarnedForTurn = Integer.parseInt(editPlayerThreePoints.getText().toString());
                player3Score.setText("" + (Integer.parseInt(player3Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerThreePoints.setText("");
            }
        });

        p4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editPlayerFourPoints = (EditText) findViewById(R.id.add_p1_pts_text_field);
                TextView player4Score = (TextView) findViewById(R.id.player_four_score_tally);
                int pointsEarnedForTurn = Integer.parseInt(editPlayerFourPoints.getText().toString());
                player4Score.setText("" + (Integer.parseInt(player4Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerFourPoints.setText("");
            }
        });
    }
}