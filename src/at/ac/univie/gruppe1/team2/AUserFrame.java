package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class AUserFrame extends Activity implements View.OnClickListener {


    private TextView name;
    private TextView mail;
    private TextView level;
    private TextView punkte;
    private static User user;
    private ProgressDialog pDialog;

    private static ImageView profilPic;
    private static Bitmap pic;

    private Button usertaskButton;
    private Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_user_frame);
        //user = MainActivity.getUser();

        new GetUserInformation().execute();

        usertaskButton = (Button) findViewById(R.id.usertask_button);
        usertaskButton.setOnClickListener(this);

        back = (Button) findViewById(R.id.backbuttonA);
        back.setOnClickListener(this);

    }


    public void onClick(View v) {
        if (v == usertaskButton) {
            AUserTaskList.setUser(user);
            startActivity(new Intent(this, AUserTaskList.class));
        } else if (v == back) {
            finish();
        }
    }


    private class GetUserInformation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AUserFrame.this);
            pDialog.setMessage("Userdaten werden geladen...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            JSONArray jsonArray = null;
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

            try {
                boolean found = false;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = null;
                    jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.getString("username");

                    Log.e("Vor Gefunden!", name);
                    if (user.getUsername().equalsIgnoreCase(name)) {

                        Log.e("Gefunden!", "YEP");
                        found = true;

                        //String mail = jsonObject.getString("email");
                        String lvl = jsonObject.getString("level");
                        if (lvl.equals("null")) {
                            lvl = "1";
                        }
                        int level = Integer.parseInt(lvl);
                        String p = jsonObject.getString("points");
                        if (p.equals("null")) {
                            p = "0";
                        }
                        int punkte = Integer.parseInt(p);

                        String bmp = jsonObject.getString("picture");
                        Bitmap bitmap = null;
                        if (bmp.equalsIgnoreCase("null")) {
                            //default pic
                            bitmap = null;
                        } else {
                            byte[] decodedByte = Base64.decode(bmp, 0);
                            bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                        }
                        user.setProfilPic(bitmap);
                        user.setUsername(name);
                        // user.setPassword(pw);
                        // user.setEimaladresse(mail);
                        user.setLevel(level);
                        user.setPunkte(punkte);

                        //startActivity(new Intent(this, MainFrame.class));

                        break;
                    } else {
                    }
                }
            } catch (Exception e) {
                Log.e("ERROR", e.toString());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            setData();
        }
    }

    public static Bitmap getPic() {
        return pic;
    }

    public static void setPic(Bitmap pic) {
        AUserFrame.pic = pic;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        AUserFrame.user = user;
    }

    public void setData() {
        profilPic = (ImageView) findViewById(R.id.aprofilImg);

        if (user.getProfilPic() == null) {
            profilPic.setImageResource(R.drawable.ic_launchersmall);
        } else {
            pic = user.getProfilPic();
            profilPic.setImageBitmap(user.getProfilPic());
        }
        name = (TextView) findViewById(R.id.anameView);
        name.setText(user.getUsername());

        Log.e("Name", user.getUsername());

        String lvl = Integer.toString(user.getLevel());
        level = (TextView) findViewById(R.id.alevelView);
        level.setText(lvl);
        Log.e("Level", lvl);
        String p = Integer.toString(user.getPunkte());
        punkte = (TextView) findViewById(R.id.apunkteView);
        punkte.setText(p);
        Log.e("Punkte", p);

    }
}
