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
import android.widget.Toast;

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

/**
 * Created by max on 03.05.15.
 */
public class HomeFrame extends ListActivity implements View.OnClickListener {


    private static JSONArray jsonArray = null;
    ArrayList<HashMap<String, String>> liste;

    private static int level;
    View header;

    private ProgressDialog pDialog;

    private static final String name = "Name";
    private static final String lvl = "Level";
    private static final String points = "Punkte";
    private static final String likes = "Likes";

    private Button highscore_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_frame);

        highscore_button = (Button) findViewById(R.id.highscore_button);
        highscore_button.setOnClickListener(this);

        liste = new ArrayList<HashMap<String, String>>();
        header = getLayoutInflater().inflate(R.layout.home_task_header, null);
        header.setOnClickListener(this);
        ListView listView = getListView();
        listView.addHeaderView(header);

        ListView lv = getListView();

        listView.addHeaderView(header);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = new Task();
                TextView task_name = (TextView) view.findViewById(R.id.tasklistname);
                if (task_name.getText().toString().equals("Im Moment keine Tasks vorhanden")) {
                    Toast.makeText(getApplicationContext(), "Netter versuch ;)",
                            Toast.LENGTH_LONG).show();
                } else {
                    String name = task_name.getText().toString();
                    task.setName(name);
                    RunningTaskFrame.setTask(task);
                    RunningTaskFrame.setHome(true);
                    Intent myIntent = new Intent(view.getContext(), RunningTaskFrame.class);
                    startActivityForResult(myIntent, 0);
                    //finish();
                }
            }
        });

        new GetTaskList().execute();
    }


    public void onClick(View v) {
        if (v == highscore_button) {
            startActivity(new Intent(this, HighscoreFrame.class));
        }
        if (v == header) {

        }
    }


    private class GetTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HomeFrame.this);
            pDialog.setMessage("Liste wird geladen...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                String url = "http://s18354693.onlinehome-server.info/wolo_files/alltasks.php";
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
                        //     if ((jsonObject.getString("type").equalsIgnoreCase(art)) && (jsonObject.getString("location").equalsIgnoreCase(ort))){
                        String n = jsonObject.getString("name");
                        String p = jsonObject.getString("points");
                        String l = jsonObject.getString("level");
                        String li = jsonObject.getString("likes");

                        if (Integer.valueOf(l) == MainActivity.getUser().getLevel()) {
                            HashMap<String, String> a = new HashMap<String, String>();
                            a.put(name, n);
                            a.put(lvl, l);
                            a.put(points, p);
                            a.put(likes, li);

                            liste.add(a);
                        }
                    }
                    if (liste.size() < 1) {
                        HashMap<String, String> a = new HashMap<String, String>();
                        a.put(name, "Im Moment keine Tasks vorhanden");
                        a.put(lvl, "-");
                        a.put(points, "-");
                        a.put(likes, "-");
                        //   a.put(beschreibung, b);
                        //   a.put(info, j);
                        liste.add(a);
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


            ListAdapter adapter = new SimpleAdapter(HomeFrame.this, liste,
                    R.layout.list_item, new String[]{name, lvl, points, likes},
                    new int[]{R.id.tasklistname, R.id.tasklistlevel, R.id.tasklistpunkte, R.id.taskListlikes});
            setListAdapter(adapter);
        }
    }

    public static JSONArray getJsonArray() {
        return jsonArray;
    }

    public static void setJsonArray(JSONArray jsonArray) {
        HomeFrame.jsonArray = jsonArray;
    }

    @Override
    public void onBackPressed() {
    }
}
