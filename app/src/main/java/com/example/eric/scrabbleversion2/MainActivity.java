package com.example.eric.scrabbleversion2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import static com.example.eric.scrabbleversion2.Permutations.reorderedMatches;

public class MainActivity extends AppCompatActivity {

    private String onClickItem;

    private String dictionaryEntries() {
        final String language = "en";
        final String word = this.onClickItem;
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id + "/definitions";
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     *
    private GoogleApiClient client;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //the below chunk is used for my custom toasts.------------------------------
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        final TextView text = (TextView) layout.findViewById(R.id.definitionText);
        //end chunk------------------------------------------------------------------

        Button findResults = (Button) findViewById(R.id.find_results_button);
        final Button startTimerButton = (Button) findViewById(R.id.start_timer_button);
        //stop timer button needs to default to disabled unless timer is running.
        final Button stopTimerButton = (Button) findViewById(R.id.stop_timer_button);
        stopTimerButton.setEnabled(false);
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
        ArrayList<String> dictionaryArrayList = new ArrayList<>();
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

            //this chunk handles the input of illegal special characters and eliminates case-sensitivity
            // within the user inputted letter bank, as well as checking to make sure the field is not empty.
            EditText input_letters = (EditText) findViewById(R.id.input_letters);
            String letterBank = input_letters.getText().toString().toLowerCase();
            if (!letterBank.matches("[a-zA-Z]*")) {
                text.setText(R.string.charWarning);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }

            else if (letterBank.isEmpty()) {
                text.setText(R.string.emptyStringWarning);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
            //end of input handling.  If input passes handling requisites, the below code will execute.----

            else {
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
                Set<String> s = new HashSet<>(Permutations.matches);
                ArrayList<String> results = new ArrayList<>(s);

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

                //display no results found in listview if no results found
                if (results.isEmpty()) {
                    results.add("no available options");
                    ArrayAdapter<String> emptyItemsAdapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_list_item_1, results);
                    listView.setAdapter(emptyItemsAdapter);
                }

                else {
                    Sort.sortAlphabetically(results);
                    //String TAG = "Value of results: ";
                    //Log.i(TAG, results.toString());
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, results);
                    listView.setAdapter(itemsAdapter);

                    //the code for retrieving the dictionary definition of word is below-----------------
                    listView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            onClickItem = (listView.getItemAtPosition(position).toString().toLowerCase());
                            //the API callbacktask occurs here.  Works with lines 47-52 and 427-454.
                            new CallbackTask().execute(dictionaryEntries());
                        }
                    });
                    //end API code section----------------------------------------------------------------
                }
            }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("alphabetized")) {
                    Sort.sortAlphabetically(reorderedMatches);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, Permutations.reorderedMatches);
                    listView.setAdapter(itemsAdapter);
                }
                if (selectedItem.equals("byLength")) {
                    Sort.sortByWordLength(reorderedMatches);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, Permutations.reorderedMatches);
                    listView.setAdapter(itemsAdapter);
                }
                if (selectedItem.equals("byScore")) {
                    Sort.sortByWordScore(reorderedMatches);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, Permutations.reorderedMatches);
                    listView.setAdapter(itemsAdapter);
                    Log.i("Sorted By Score", reorderedMatches.toString());
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

        //this chunk works the simple timer---------------------------------------------------------
        final CountDownTimer countDownTimer;
        countDownTimer = new CountDownTimer(180 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerDisplay.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                timerDisplay.setText(R.string.endTimer);
            }
        };

        startTimerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //the below starts the timer and resets the start/reset button enabled/disabled state
                countDownTimer.start();
                stopTimerButton.setEnabled(true);
                startTimerButton.setEnabled(false);
            }
        });

        stopTimerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //the below cancels the timer and resets the start/reset button enabled/disabled state
                countDownTimer.cancel();
                startTimerButton.setEnabled(true);
                stopTimerButton.setEnabled(false);
                timerDisplay.setText(R.string.defaultTimeRemaining);
            }
        });
        //end timer chunk---------------------------------------------------------------------------
    }

    //this chunk here is for the API call
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            final String app_id = "99ec26a8";
            final String app_key = "2704b58d2e5a86093e6e76611550868d";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.i("JSON String", result);

            //parse json here to retrieve and show definition to user
            try {
                JSONObject first = new JSONObject(result);

                JSONArray resultsArray = first.getJSONArray("results");
                JSONObject resultsObj = resultsArray.getJSONObject(0);

                JSONArray lexEntriesArray = resultsObj.getJSONArray("lexicalEntries");
                JSONObject lexEntriesObj = lexEntriesArray.getJSONObject(0);

                JSONArray entriesArray = lexEntriesObj.getJSONArray("entries");
                JSONObject entriesObj = entriesArray.getJSONObject(0);

                JSONArray sensesArray = entriesObj.getJSONArray("senses");
                JSONObject sensesObj = sensesArray.getJSONObject(0);

                JSONArray definitionsArray = sensesObj.getJSONArray("definitions");
                String definition = definitionsArray.get(0).toString();

                //custom toast to show desired font and background color, also declared
                //at the beginning of the file because i could not access in this inner class.
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));
                TextView text = (TextView) layout.findViewById(R.id.definitionText);
                text.setText(definition);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                //definition="";
                //Log.i("word definition", definition);

            } catch (JSONException e) {
                Log.e("Scrabble Companion", "unexpected JSON exception", e);
                // Do something to recover ... or kill the app.
            }
        }
    }
}
