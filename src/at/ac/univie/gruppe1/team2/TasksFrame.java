package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by max on 03.05.15.
 */
public class TasksFrame extends Activity implements View.OnClickListener {

    Button search_button;
    Button create_button;
    Button mytasks_button;
    static JSONArray jsonArray;
    private Spinner spinner;
    private RadioButton indoor;
    private RadioButton outdoor;
    private RadioButton fun;
    private RadioButton seriously;
    private NumberPicker time;
    private NumberPicker min;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_frame);

        time = (NumberPicker) findViewById(R.id.stdPick);
        time.setMinValue(0);
        time.setMaxValue(8);
        time.setValue(3);
        time.setWrapSelectorWheel(false);

        min = (NumberPicker) findViewById(R.id.minPick);
        min.setMinValue(1);
        min.setMaxValue(59);
        min.setValue(30);
        min.setWrapSelectorWheel(false);

        ArrayList<String> spinnerArray = new ArrayList<String>();
        for (int i = 0; i <= MainActivity.getUser().getLevel(); i++) {
            spinnerArray.add(Integer.toString(i));
        }
       /* ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,spinnerArray);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);*/


        search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(this);

        create_button = (Button) findViewById(R.id.create_button);
        create_button.setOnClickListener(this);

        mytasks_button = (Button) findViewById(R.id.mt_button);
        mytasks_button.setOnClickListener(this);

        seriously = (RadioButton) findViewById(R.id.suchErnst);
        fun = (RadioButton) findViewById(R.id.suchFun);
        indoor = (RadioButton) findViewById(R.id.suchIndoorButton);
        outdoor = (RadioButton) findViewById(R.id.suchOutdoorButton);


    }

    public void onClick(View v) {

        if (v == search_button) {
            //    final Bundle extras = getIntent().getExtras();

            String time_string = String.valueOf(time.getValue());
            ChosenTasksFrame.setTime(Integer.valueOf(time_string));
            //  time = (NumberPicker) findViewById(R.id.timePicker2);
            //String time_string = String.valueOf(time.getValue());
            //ChosenTasksFrame.setTime(Integer.valueOf(time_string));
            if (indoor.isChecked()) {
                ChosenTasksFrame.setIndoor(true);
                //indoor.setChecked(false);
            }
            if (outdoor.isChecked()) {
                ChosenTasksFrame.setOutdoor(true);
                //outdoor.setChecked(false);
            }
            if (fun.isChecked()) {
                ChosenTasksFrame.setFun(true);
                //fun.setChecked(false);
            }
            if (seriously.isChecked()) {
                ChosenTasksFrame.setSeriously(true);
                //seriously.setChecked(false);
            }
 /*           level = extras.getInt("spinner");
            ChosenTasksFrame.setLevel(level);

*/
            startActivity(new Intent(this, ChosenTasksFrame.class));

        } else if (v == create_button) {
            startActivity(new Intent(this, CreateTaskFrame.class));
        } else if (v == mytasks_button) {
            startActivity(new Intent(this, DoneTasksFrame.class));
        }

    }


    public static JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray j) {
        jsonArray = j;
    }

    @Override
    public void onBackPressed() {
    }
}
