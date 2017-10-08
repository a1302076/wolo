package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 09.05.15.
 */
public class CreateTaskFrame extends Activity implements View.OnClickListener {

    private EditText task_name;
    private EditText task_info;
    private EditText task_discription;


    private RadioButton ernst;
    private RadioButton fun;
    private RadioButton indoor;
    private RadioButton outdoor;

    private Spinner level_spinner;
    private Spinner punkte_spinner;
    private NumberPicker hours;
    private NumberPicker mins;
    private Button create;
    private Button back;

    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_frame);

        task_name = (EditText) findViewById(R.id.task_name);
        task_info = (EditText) findViewById(R.id.task_info);
        task_discription = (EditText) findViewById(R.id.task_description);
        level_spinner = (Spinner) findViewById(R.id.level_spinner);
        punkte_spinner = (Spinner) findViewById(R.id.points_spinner);

        List<String> level_list = new ArrayList<String>();
        for (int i = 1; i <= MainActivity.getUser().getLevel(); i++) {
            level_list.add(Integer.toString(i));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, level_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level_spinner.setAdapter(dataAdapter);

        List<String> punkte_list = new ArrayList<String>();
        for (int i = 1; i < 20; i++) {
            punkte_list.add(Integer.toString(i));
        }

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, punkte_list);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        punkte_spinner.setAdapter(dataAdapter2);


        ernst = (RadioButton) findViewById(R.id.ernst);
        fun = (RadioButton) findViewById(R.id.fun);
        indoor = (RadioButton) findViewById(R.id.indoorButton);
        outdoor = (RadioButton) findViewById(R.id.outdoorButton);

        hours = (NumberPicker) findViewById(R.id.timePicker);
        hours.setMinValue(0);
        hours.setMaxValue(8);
        hours.setValue(3);
        hours.setWrapSelectorWheel(false);


        mins = (NumberPicker) findViewById(R.id.timePicker2);
        mins.setMinValue(1);
        mins.setMaxValue(59);
        mins.setValue(30);
        mins.setWrapSelectorWheel(false);


        create = (Button) findViewById(R.id.tasks_button);
        create.setOnClickListener(this);

        back = (Button) findViewById(R.id.backbuttonC);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == back) {
            finish();
        } else if (v == create) {
            task = new Task();
            task.setErsteller(MainActivity.getUser().getUsername());


            // null Abfragen
            if (task_name.getText().toString().length() < 1 || task_info.getText().toString().length() < 1 || task_discription.getText().toString().length() < 1) {
                Toast.makeText(getApplicationContext(), "Name, Info und Beschreibung nötig",
                        Toast.LENGTH_LONG).show();
            } else {
                if (!fun.isChecked() && !ernst.isChecked() && !indoor.isChecked() && !outdoor.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Wähle bitte Art und Kategorie",
                            Toast.LENGTH_LONG).show();
                } else {
                    task.setName(task_name.getText().toString());
                    task.setBeschreibung(task_info.getText().toString());
                    task.setAnleitung(task_discription.getText().toString());

                    String lvl = level_spinner.getSelectedItem().toString();
                    String p = punkte_spinner.getSelectedItem().toString();
                    Log.e("Level", lvl);
                    Log.e("Punkte", p);
                    task.setLevel(Integer.valueOf(lvl));
                    task.setPunkte(Integer.valueOf(p));

                    String hour_string = String.valueOf(hours.getValue());
                    String mins_string = String.valueOf(mins.getValue());
                    task.setZeit(Integer.valueOf(hour_string) * 60 + Integer.valueOf(mins_string));

                    if (fun.isChecked()) {
                        task.setArt("fun");
                    } else {
                        task.setArt("ernst");
                    }

                    if (indoor.isChecked()) {
                        task.setOrt("drinnen");
                    } else {
                        task.setOrt("draussen");
                    }

                    // if(task.taskCheck()) {
                    Toast.makeText(getApplicationContext(), "Dein Task wurde erstellt!",
                            Toast.LENGTH_LONG).show();
                    new AddToTaskList().execute();

                    finish();
                }
            }
        }
    }


    private class AddToTaskList extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                //[{"ID":"1","creator":"testuser","name":"testtask","info":"test","instruction":"t","time":"90","level":"1","points":"100","likes":null,"type":"Ernst"}]
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("id", username));
                nameValuePairs.add(new BasicNameValuePair("creator", task.getErsteller()));
                nameValuePairs.add(new BasicNameValuePair("name", task.getName()));
                nameValuePairs.add(new BasicNameValuePair("info", task.getBeschreibung()));
                nameValuePairs.add(new BasicNameValuePair("instruction", task.getAnleitung()));
                nameValuePairs.add(new BasicNameValuePair("time", Integer.toString(task.getZeit())));
                nameValuePairs.add(new BasicNameValuePair("level", Integer.toString(task.getLevel())));
                nameValuePairs.add(new BasicNameValuePair("points", Integer.toString(task.getPunkte())));
                nameValuePairs.add(new BasicNameValuePair("likes", "0"));
                nameValuePairs.add(new BasicNameValuePair("type", task.getArt()));
                nameValuePairs.add(new BasicNameValuePair("location", task.getOrt()));


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/create_task.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Http Post Response:", response.toString());
                //HttpEntity entity = response.getEntity();
                //is = entity.getContent();
                Log.e("pass 1", "connection success, with " + nameValuePairs.toString());
                //is.close();
            } catch (Exception e) {
                Log.e("Fail 1", e.toString());
            }

            return null;
        }
    }
}
