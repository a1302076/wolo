package at.ac.univie.gruppe1.team2;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChosenTasksFrame extends ListActivity implements View.OnClickListener {

    private static JSONArray jsonArray = null;
    ArrayList<HashMap<String, String>> tasklist;
    private static boolean indoor = false;
    private static boolean outdoor = false;
    private static boolean fun = false;
    private static boolean seriously = false;
    private static int time;
    private static int level;
    private String art;
    private String ort;

    private ProgressDialog pDialog;

    private static final String name = "Name";
    private static final String lvl = "Level";
    private static final String points = "Punkte";
    private static final String likes = "Likes";

    private Button z_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chosen_tasks);
        z_button = (Button) findViewById(R.id.z_button);
        z_button.setOnClickListener(this);


        tasklist = new ArrayList<HashMap<String, String>>();
        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = new Task();
                TextView task_name = (TextView) view.findViewById(R.id.tasklistname);
                String name = task_name.getText().toString();
                task.setName(name);
                RunningTaskFrame.setTask(task);
                Intent myIntent = new Intent(view.getContext(), RunningTaskFrame.class);
                startActivityForResult(myIntent, 0);
                //finish();
            }
        });

        new GetTaskList().execute();

    }


    @Override
    public void onClick(View v) {
        if (v == z_button) {
            finish();
        }
    }

    public static boolean isIndoor() {
        return indoor;
    }

    public static void setIndoor(boolean i) {
        indoor = i;
    }

    public static boolean isOutdoor() {
        return outdoor;
    }

    public static void setOutdoor(boolean o) {
        outdoor = o;
    }

    public static boolean isFun() {
        return fun;
    }

    public static void setFun(boolean f) {
        fun = f;
    }

    public static boolean isSeriously() {
        return seriously;
    }

    public static void setSeriously(boolean s) {
        seriously = s;
    }

    public static int getTime() {
        return time;
    }

    public static void setTime(int t) {
        time = t;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int l) {
        level = l;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    private class GetTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ChosenTasksFrame.this);
            pDialog.setMessage("Liste wird geladen...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                // Hier die Filter if abfragen!

                String url = null;

                if (isIndoor()) {
                    if (isFun()) {
                        url = "http://s18354693.onlinehome-server.info/wolo_files/indoor_fun.php";
                    } else {
                        url = "http://s18354693.onlinehome-server.info/wolo_files/indoor_serious.php";
                    }
                } else if (isOutdoor()) {
                    if (isFun()) {
                        Log.e("True", "Fun outdoor");
                        url = "http://s18354693.onlinehome-server.info/wolo_files/outdoor_fun.php";
                    } else {
                        url = "http://s18354693.onlinehome-server.info/wolo_files/outdoor_serious.php";
                    }
                } else {
                    // Alle
                    url = "http://s18354693.onlinehome-server.info/wolo_files/alltasks.php";
                }

                indoor = false;
                outdoor = false;
                fun = false;
                seriously = false;


                // http client..
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;

                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);

                httpEntity = httpResponse.getEntity();
                String jsonStr = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(jsonStr);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonArray != null) {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String n = jsonObject.getString("name");
                        String p = jsonObject.getString("points");
                        String l = jsonObject.getString("level");
                        String li = jsonObject.getString("likes");
                        String art_t = jsonObject.getString("type");
                        String location = jsonObject.getString("location");

                        if (Integer.valueOf(l) <= MainActivity.getUser().getLevel()) {
                            HashMap<String, String> a = new HashMap<String, String>();
                            a.put(name, n);
                            a.put(lvl, l);
                            a.put(points, p);
                            a.put(likes, li);
                            tasklist.add(a);
                        }

                    }
                } catch (Exception e) {

                    Log.e("Error", "Listfail");
                    e.printStackTrace();
                }
            } else {
                Log.e("ERROR:", "Liste ist nicht da");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             */


            ListAdapter adapter = new SimpleAdapter(ChosenTasksFrame.this, tasklist,
                    R.layout.list_item, new String[]{name, lvl, points, likes},
                    new int[]{R.id.tasklistname, R.id.tasklistlevel, R.id.tasklistpunkte, R.id.taskListlikes});
            setListAdapter(adapter);
        }
    }

    public static JSONArray getJsonArray() {
        return jsonArray;
    }

    public static void setJsonArray(JSONArray jsonArray) {
        ChosenTasksFrame.jsonArray = jsonArray;
    }
}
