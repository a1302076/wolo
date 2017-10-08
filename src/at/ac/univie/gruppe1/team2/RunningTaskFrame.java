package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by max on 10.05.15.
 */
public class RunningTaskFrame extends Activity implements View.OnClickListener {

    static Task task;

    TextView name_view;
    TextView info_view;
    TextView instruction_view;
    TextView type_view;
    TextView location_view;
    TextView level_view;
    TextView points_view;
    TextView time_view;


    private Button b_button;
    private Button start_button;
    private Button discButton;
    private Button escButton;
    private Button other;

    ColorDrawable start_buttonColor;

    private MyCountDownTimer myCountDownTimer;

    private boolean started;
    private static boolean home;
    int temp_time;

    User user;
    static boolean success;

    //Das Level System
    private Integer[] level_list = {0, 10, 30, 60, 120, 240, 480, 760, 1520};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.running_task_frame);

        other = (Button) findViewById(R.id.other_button);
        other.setOnClickListener(this);

        escButton = (Button) findViewById(R.id.escButton);
        escButton.setOnClickListener(this);

        b_button = (Button) findViewById(R.id.b_button);
        b_button.setOnClickListener(this);
        started = false;

        start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(this);
        start_buttonColor = (ColorDrawable) start_button.getBackground();

        discButton = (Button) findViewById(R.id.discButton);
        discButton.setOnClickListener(this);


        //seekBar = (SeekBar) findViewById(R.id.seekBar);

        boolean success = false;

        try {
            JSONArray jsonArray = null;
            if (home) {
                jsonArray = HomeFrame.getJsonArray();
                setHome(false);
            } else {
                jsonArray = ChosenTasksFrame.getJsonArray();
            }
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("name").equals(task.getName())) {
                        //task.setTask_id(Integer.valueOf(jsonObject.getString("id")));
                        task.setBeschreibung(jsonObject.getString("info"));
                        task.setAnleitung(jsonObject.getString("instruction"));
                        task.setArt(jsonObject.getString("type"));
                        task.setOrt(jsonObject.getString("location"));
                        task.setLevel(Integer.valueOf(jsonObject.getString("level")));
                        task.setPunkte(Integer.valueOf(jsonObject.getString("points")));
                        task.setZeit(Integer.valueOf(jsonObject.getString("time")));
                        task.setLikes(Integer.valueOf(jsonObject.getString("likes")));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", "object nich da");
            e.printStackTrace();
        }

        name_view = (TextView) findViewById(R.id.name_view);
        name_view.setText(task.getName());

        info_view = (TextView) findViewById(R.id.info_view);
        info_view.setText(task.getBeschreibung());

        instruction_view = (TextView) findViewById(R.id.description_view);
        //instruction_view.setText(task.getAnleitung());

        type_view = (TextView) findViewById(R.id.art_view);
        if (task.getArt() != null && task.getArt().equalsIgnoreCase("ernst")) {
            task.setArt("sinnvoll");
        }
        type_view.setText(task.getArt());

        location_view = (TextView) findViewById(R.id.categorie_view);
        location_view.setText(task.getOrt());

        level_view = (TextView) findViewById(R.id.level_view);
        level_view.setText(Integer.toString(task.getLevel()));

        points_view = (TextView) findViewById(R.id.points_view);
        points_view.setText(Integer.toString(task.getPunkte()));

        time_view = (TextView) findViewById(R.id.time_view);
        temp_time = task.getZeit();
        // Minutes to MilliSeconds
        temp_time = temp_time * 60000;
        if (temp_time > 0) {
            //time_view.setGravity();
            int hours = (int) temp_time / 3600000;
            int minutes = (int) (temp_time - hours * 3600000) / 60000;
            int seconds = (int) (temp_time - (hours * 3600000) - minutes * 60000) / 1000;
            time_view.setText(Integer.toString(hours) + ":" + Integer.toString(minutes) + ":" + Integer.toString(seconds));
        }
        //System.out.println("TIME:" + temp_time);

//        seekBar.setProgress(100);


    }

    @Override
    public void onClick(View v) {

        if(v == other){
            EvidenceFrame.setTaskname(task.getName());
            Intent intent = new Intent(this, EvidenceFrame.class);
            startActivity(intent);
        }else
        if (v == escButton) {
            if (started) {
                try {
                    user = MainActivity.getUser();
                    started = false;
                    escButton.setVisibility(View.INVISIBLE);
                    success = false;
                    new UpdateUserPoints().execute();
                    //finish();
                    Toast.makeText(getApplicationContext(), "Task abgebrochen",
                            Toast.LENGTH_LONG).show();
                    b_button.setVisibility(View.VISIBLE);
                    start_button.setText("Nochmal");
                    start_button.setBackgroundColor(Color.parseColor("#0099CC"));

                    temp_time = task.getZeit();
                    // Minutes to MilliSeconds
                    temp_time = temp_time * 60000;
                    if (temp_time > 0) {
                        //time_view.setGravity();
                        int hours = (int) temp_time / 3600000;
                        int minutes = (int) (temp_time - hours * 3600000) / 60000;
                        int seconds = (int) (temp_time - (hours * 3600000) - minutes * 60000) / 1000;
                        time_view.setText(Integer.toString(hours) + ":" + Integer.toString(minutes) + ":" + Integer.toString(seconds));
                    }

                    myCountDownTimer.cancel();

                } catch (Exception e) {
                    //Log.e("FAIL: ", e.toString());
                }
            }
        } else if (v == b_button) {
            started = false;

            //Abbruch schreiben

            finish();
        } else if (v == discButton) {
            if (instruction_view.getVisibility() == View.INVISIBLE) {
                instruction_view.setText(task.getAnleitung());
                instruction_view.setVisibility(View.VISIBLE);
            } else {
                instruction_view.setText(null);
                instruction_view.setVisibility(View.INVISIBLE);
            }
        } else if (v == start_button) {

            if (!started) {
                // Start ma!
                started = true;

                escButton.setVisibility(View.VISIBLE);
                start_button.setBackgroundColor(Color.GREEN);
                start_button.setText("Fertig");
                b_button.setVisibility(View.INVISIBLE);
                //b_button.setText("Abbrechen");


                myCountDownTimer = new MyCountDownTimer(temp_time, 1000);
                myCountDownTimer.start();


            } else {
                user = MainActivity.getUser();
                user.setPunkte(user.getPunkte() + task.getPunkte());
                success = true;
                //User Punkte in der DB aktualisieren
                new UpdateUserPoints().execute();

                Toast.makeText(getApplicationContext(), "Task erfolgreich abgeschlossen, gz ;)",
                        Toast.LENGTH_LONG).show();
                FinishTaskFrame.setTask(task);
                started = false;


                //Bewertung

                Intent i = new Intent(this, FinishTaskFrame.class);
                // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                success = false;
                finish();
            }
        }


    }

    @Override
    public void onBackPressed() {
    }


    public Task getTask() {
        return task;
    }

    public static void setTask(Task t) {
        task = t;
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (started) {

                int hours = (int) millisUntilFinished / 3600000;
                int minutes = (int) (millisUntilFinished - hours * 3600000) / 60000;
                int seconds = (int) (millisUntilFinished - (hours * 3600000) - minutes * 60000) / 1000;
                Log.e("!", "H:" + hours + " M:" + minutes + " S" + seconds);
                time_view.setText(String.valueOf(hours) + ":" + String.valueOf((minutes) + ":" + String.valueOf(seconds)));

            } else {
                return;
            }

        }

        @Override
        public void onFinish() {
            time_view.setText("Ende");
            Toast.makeText(getApplicationContext(), "Die Zeit ist leider vorbei...",
                    Toast.LENGTH_LONG).show();
            //seekBar.setProgress(0);
            start_button.setText("Nochmal");
            started = false;
        }
    }


    private class UpdateUserPoints extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!success) {
                try {
                    ArrayList<NameValuePair> nameValuePairs0 = new ArrayList<NameValuePair>(3);
                    nameValuePairs0.add(new BasicNameValuePair("username", user.getUsername()));
                    nameValuePairs0.add(new BasicNameValuePair("success", "0"));
                    nameValuePairs0.add(new BasicNameValuePair("taskname", task.getName()));
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/user_task.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs0));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d("Http Post Response:", response.toString());
                    Log.e("pass 1", "connection success, with " + nameValuePairs0.toString());

                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }

            } else {


                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", user.getUsername().toString()));
                nameValuePairs.add(new BasicNameValuePair("points", Integer.toString(user.getPunkte())));


                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/updatePoints.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d("Http Post Response:", response.toString());
                    Log.e("pass 1", "connection success, with " + nameValuePairs.toString());

                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }

                if (user.getLevel() < level_list.length - 1) {
                    if (level_list[user.getLevel() + 1] <= user.getPunkte()) {
                        //Level aufstieg!
                        user.setLevel((user.getLevel() + 1));
                        ArrayList<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
                        nameValuePairs2.add(new BasicNameValuePair("username", user.getUsername().toString()));
                        nameValuePairs2.add(new BasicNameValuePair("level", Integer.toString(user.getLevel())));


                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/updateLevel.php");
                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs2));
                            HttpResponse response = httpclient.execute(httppost);
                            Log.d("Http Post Response:", response.toString());
                            Log.e("pass 1", "connection success, with " + nameValuePairs2.toString());

                        } catch (Exception e) {
                            Log.e("Fail 1", e.toString());
                        }
                    }
                }

                ArrayList<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>(3);
                nameValuePairs3.add(new BasicNameValuePair("username", user.getUsername()));
                nameValuePairs3.add(new BasicNameValuePair("success", "1"));
                nameValuePairs3.add(new BasicNameValuePair("taskname", task.getName()));


                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/user_task.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs3));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d("Http Post Response:", response.toString());
                    Log.e("pass 1", "connection success, with " + nameValuePairs3.toString());

                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }
            }

            return null;
        }
    }


/*        private class WriteUserTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... arg0) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("username", user.getUsername()));
                nameValuePairs.add(new BasicNameValuePair("success", Boolean.toString(success)));
                nameValuePairs.add(new BasicNameValuePair("taskname", task.getName()));


                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/user_task.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d("Http Post Response:", response.toString());
                    Log.e("pass 1", "connection success, with " + nameValuePairs.toString());

                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }

                return null;
            }
        }
*/

    public boolean isHome() {
        return home;
    }

    public static void setHome(boolean h) {
        home = h;
    }

    public static boolean isSuccess() {
        return success;
    }

    public static void setSuccess(boolean success) {
        RunningTaskFrame.success = success;
    }
}
