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

/**
 * Created by max on 28.05.15.
 */
public class DoneTasksFrame extends ListActivity implements View.OnClickListener {


    User user;
    private Button backButton;
    private JSONArray jsonArray;
    private ProgressDialog pDialog;
    private ArrayList<HashMap<String, String>> tasklist;


    private static final String taskname = "taskname";
    private static final String success = "nonsuccesspic";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.done_tasks_list);

        user = MainActivity.getUser();
        tasklist = new ArrayList<HashMap<String, String>>();

        backButton = (Button) findViewById(R.id.zrue_button);
        backButton.setOnClickListener(this);

        new GetTaskList().execute();

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = new Task();
                TextView task_name = (TextView) view.findViewById(R.id.taskn);
                String name = task_name.getText().toString();
                Log.i("TaskName", name);
                task.setName(name);
                RunningTaskFrame.setTask(task);
                RunningTaskFrame.setHome(true);
                Intent myIntent = new Intent(view.getContext(), RunningTaskFrame.class);
                startActivityForResult(myIntent, 0);
                //finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == backButton) {
            finish();
        }
    }


    private class GetTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DoneTasksFrame.this);
            pDialog.setMessage("Liste wird geladen...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {

                // http client..
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;

                HttpGet httpGet = new HttpGet("http://s18354693.onlinehome-server.info/wolo_files/done_tasks.php");
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

                        String u = jsonObject.getString("user");
                        String tn = jsonObject.getString("taskname");
                        String sc = jsonObject.getString("success");

                        if (u.equals(user.getUsername())) {
                            Log.e("User", user.getUsername());
                            HashMap<String, String> a = new HashMap<String, String>();
                            a.put(taskname, tn);
                            //a.put(success, sc);
                            String istring = null;
                            int imgid = 0;
                            Log.i("Boolean", sc);
                            if (sc.equals("1")) {
                                imgid = getResources().getIdentifier("at.ac.univie.gruppe1.team2:drawable/successpic", null, null);
                            } else {
                                imgid = getResources().getIdentifier("at.ac.univie.gruppe1.team2:drawable/nonsuccesspic", null, null);
                            }

                            istring = Integer.toString(imgid);
                            a.put(success, istring);
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


            ListAdapter adapter = new SimpleAdapter(DoneTasksFrame.this, tasklist,
                    R.layout.done_tasks_list_item, new String[]{taskname, success},
                    new int[]{R.id.taskn, R.id.successT});
            setListAdapter(adapter);
        }
    }
}
