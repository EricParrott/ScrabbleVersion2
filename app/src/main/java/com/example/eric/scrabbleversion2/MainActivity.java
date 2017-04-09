package com.example.eric.scrabbleversion2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.example.eric.scrabbleversion2.Permutations.reorderedMatches;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button findResults = (Button) findViewById(R.id.find_results_button);
        Button startTimerButton = (Button) findViewById(R.id.start_timer_button);
        Button stopTimerButton = (Button) findViewById(R.id.stop_timer_button);

        final ListView listView = (ListView) findViewById(R.id.listView);

        final Button p1 = (Button) findViewById(R.id.button1);
        final Button p2 = (Button) findViewById(R.id.button2);
        final Button p3 = (Button) findViewById(R.id.button3);
        final Button p4 = (Button) findViewById(R.id.button4);

        final EditText editPlayerOnePoints = (EditText) findViewById(R.id.add_p1_pts_text_field);
        final EditText editPlayerTwoPoints = (EditText) findViewById(R.id.add_p2_pts_text_field);
        final EditText editPlayerThreePoints = (EditText) findViewById(R.id.add_p3_pts_text_field);
        final EditText editPlayerFourPoints = (EditText) findViewById(R.id.add_p4_pts_text_field);

        final TextView player1Score = (TextView) findViewById(R.id.player_one_score_tally);
        final TextView player2Score = (TextView) findViewById(R.id.player_two_score_tally);
        final TextView player3Score = (TextView) findViewById(R.id.player_three_score_tally);
        final TextView player4Score = (TextView) findViewById(R.id.player_four_score_tally);
        final TextView timerDisplay = (TextView) findViewById(R.id.count_down_timer);

        //read in file chunk and populate hashtable chunk---------------------------------------
        BufferedReader reader;
        ArrayList<String> dictionaryArrayList = new ArrayList<String>();
        try {
            final InputStream file = getAssets().open("dictionary2.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while (line != null) {
                if (line.length() <= 10) {
                    // add word to arrayList
                    dictionaryArrayList.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        for (int i = 0; i < dictionaryArrayList.size(); i++) {
            Permutations.dictionary.put(dictionaryArrayList.get(i).hashCode(), dictionaryArrayList.get(i));
        }
        //end chunk----------------------------------------------------------------------------

        //code to create and format spinner goes here-----------------------------------------
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_dropdown_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinAdapter);
        //-------------------------------------------------------------------------------------

        findResults.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //this first chunk clears the list view each time you generate words-----------
                Permutations.matches.clear();
                Permutations.combinations.clear();
                Permutations.allPermutations.clear();
                Permutations.reorderedMatches.clear();
                spinner.setSelection(0);
                //end chunk-------------------------------------------------------------------
                EditText input_letters = (EditText) findViewById(R.id.input_letters);
                String letterBank = input_letters.getText().toString();

                Permutations.combine(letterBank, new StringBuffer(), 0);
                for (String str : Permutations.combinations) {
                    Permutations.allPermutations.addAll(Permutations.permutation(str));
                }

                for (int i = 0; i < Permutations.allPermutations.size(); i++) {
                    String possibleWord = Permutations.allPermutations.get(i).toUpperCase();
                    int hash = possibleWord.hashCode();
                    if (Permutations.dictionary.get(hash) != null) {
                        Permutations.matches.add(Permutations.dictionary.get(hash));
                    }
                }
                Set<String> s = new HashSet<String>(Permutations.matches);
                ArrayList<String> results = new ArrayList<String>(s);

                Permutations.reorderedMatches = results;

                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).length() > letterBank.length()) {
                        results.remove(i);
                    }
                }
                for (int i = 0; i < results.size(); i++) {
                    if (!Sort.containsAllChars(letterBank, results.get(i).toLowerCase())) {
                        results.remove(i);
                    }
                }
                Sort.sortAlphabetically(results);
                //            String TAG = "Value of results: ";
                //            Log.i(TAG, results.toString());
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, results);
                listView.setAdapter(itemsAdapter);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("alphabetized")) {
                    Sort.sortAlphabetically(reorderedMatches);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, Permutations.reorderedMatches);
                    listView.setAdapter(itemsAdapter);
                }
                if (selectedItem.equals("byLength")) {
                    Sort.sortByWordLength(reorderedMatches);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, Permutations.reorderedMatches);
                    listView.setAdapter(itemsAdapter);
                }
                if (selectedItem.equals("byScore")) {
                    Sort.sortByWordScore(reorderedMatches);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, Permutations.reorderedMatches);
                    listView.setAdapter(itemsAdapter);
                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing here
            }
        });

        //this chunk here disables/enables addPointsButtons when no points are in edittext field-------------
        if (editPlayerOnePoints.getText().toString().isEmpty()) {
            p1.setEnabled(false);
        }
        if (editPlayerTwoPoints.getText().toString().isEmpty()) {
            p2.setEnabled(false);
        }
        if (editPlayerThreePoints.getText().toString().isEmpty()) {
            p3.setEnabled(false);
        }
        if (editPlayerFourPoints.getText().toString().isEmpty()) {
            p4.setEnabled(false);
        }

        editPlayerOnePoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                p1.setEnabled(true);
            }
        });

        editPlayerTwoPoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                p2.setEnabled(true);
            }
        });

        editPlayerThreePoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                p3.setEnabled(true);
            }
        });

        editPlayerFourPoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                p4.setEnabled(true);
            }
        });

        p1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pointsEarnedForTurn = Integer.parseInt(editPlayerOnePoints.getText().toString());
                player1Score.setText("" + (Integer.parseInt(player1Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerOnePoints.setText("");
                p1.setEnabled(false);
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pointsEarnedForTurn = Integer.parseInt(editPlayerTwoPoints.getText().toString());
                player2Score.setText("" + (Integer.parseInt(player2Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerTwoPoints.setText("");
                p2.setEnabled(false);
            }
        });

        p3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pointsEarnedForTurn = Integer.parseInt(editPlayerThreePoints.getText().toString());
                player3Score.setText("" + (Integer.parseInt(player3Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerThreePoints.setText("");
                p3.setEnabled(false);
            }
        });

        p4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pointsEarnedForTurn = Integer.parseInt(editPlayerFourPoints.getText().toString());
                player4Score.setText("" + (Integer.parseInt(player4Score.getText().toString()) + pointsEarnedForTurn));
                editPlayerFourPoints.setText("");
                p4.setEnabled(false);
            }
        });

        //this chunk works the simple timer--------------------------------------------------------------------
        final CountDownTimer countDownTimer;
        countDownTimer = new CountDownTimer(180 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerDisplay.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                timerDisplay.setText("Time's Up");
            }
        };

        startTimerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countDownTimer.start();
            }
        });

        stopTimerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countDownTimer.cancel();
            }
        });
        //end timer chunk--------------------------------------------------------------------------------------
    }
}
