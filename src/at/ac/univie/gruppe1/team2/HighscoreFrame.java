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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by max on 03.05.15.
 */
public class HighscoreFrame extends ListActivity implements View.OnClickListener {

    private Button back_button;
    private JSONArray jsonArray;
    private ProgressDialog pDialog;
    private ArrayList<HashMap<String, String>> userlist;

    private static final String username = "username";
    private static final String points = "points";

    TextView yourPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore_frame);


        back_button = (Button) findViewById(R.id.back_buttonH);
        back_button.setOnClickListener(this);

        yourPoints = (TextView) findViewById(R.id.yourPoints);
        yourPoints.setText(String.valueOf(MainActivity.getUser().getPunkte()));
        new GetNewList().execute();

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = new User();
                TextView user_name = (TextView) view.findViewById(R.id.userName);
                user.setUsername(user_name.getText().toString());
                AUserFrame.setUser(user);
                Intent myIntent = new Intent(view.getContext(), AUserFrame.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    public ArrayList<User> sortUserlist(ArrayList<User> ulist) {
        Collections.sort(ulist, new CustomComparator());
        return ulist;
    }

    public class CustomComparator implements Comparator<User> {

        @Override
        public int compare(User u1, User u2) {
            if (u1.getPunkte() == u2.getPunkte()) {
                return 0;
            } else if (u1.getPunkte() < u2.getPunkte()) {
                return 1;
            } else if (u1.getPunkte() > u2.getPunkte()) {
                return -1;
            }
            return 0;
        }
    }


    public void onClick(View v) {
        if (v == back_button) {
            //startActivity(new Intent(this, MainFrame.class));
            finish();
        }
    }

    private class GetNewList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HighscoreFrame.this);
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

                HttpGet httpGet = new HttpGet("http://s18354693.onlinehome-server.info/wolo_files/login.php");
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
                    userlist = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String u = jsonObject.getString("username");
                        String p = jsonObject.getString("points");
                        //String sc = jsonObject.getString("success");

                        Log.e("Name", u + " Punkte " + p);
                        HashMap<String, String> a = new HashMap<String, String>();
                        a.put(username, u);
                        a.put(points, p);
                        userlist.add(a);
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

            ArrayList<HashMap<String, String>> sortuserlist = new ArrayList<HashMap<String, String>>();
            ArrayList<User> ulist = new ArrayList<User>();
            for (int i = 0; i < userlist.size(); i++) {
                User u = new User();
                u.setUsername(userlist.get(i).get(username));
                u.setPunkte(Integer.valueOf(userlist.get(i).get(points)));
                ulist.add(u);
            }
            ulist = sortUserlist(ulist);
            for (User u : ulist) {
                // Log.e("User", u.getUsername()+" P: " +u.getPunkte());
                HashMap<String, String> a = new HashMap<String, String>();
                a.put(username, u.getUsername());
                a.put(points, String.valueOf(u.getPunkte()));
                sortuserlist.add(a);

            }

            ListAdapter adapter = new SimpleAdapter(HighscoreFrame.this, sortuserlist,
                    R.layout.highscore_frame_list_item, new String[]{username, points},
                    new int[]{R.id.userName, R.id.userPoints});
            setListAdapter(adapter);
        }
    }
}

