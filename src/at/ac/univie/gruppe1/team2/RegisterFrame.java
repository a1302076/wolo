package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.InputStream;
import java.util.ArrayList;

public class RegisterFrame extends Activity implements View.OnClickListener {
    /**
     * Der Registrieren Button
     */
    private Button reg_button;
    /**
     * Eingegebener Username
     */
    private EditText username_input;
    /**
     * Eingegebene EmailAdresse
     */
    private EditText mail_input;
    /**
     * Eingegebenes Passwort
     */
    private EditText password_input;

    private String username;
    private String mail;
    private String password;

    private InputStream is = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_frame);


        reg_button = (Button) findViewById(R.id.reg_button);
        reg_button.setOnClickListener(this);

        username_input = (EditText) findViewById(R.id.username_neu);
        mail_input = (EditText) findViewById(R.id.mail_neu);
        password_input = (EditText) findViewById(R.id.password_neu);


    }

    @Override
    public void onClick(View v) {
        if (v == reg_button) {
            /**
             * Hier kommt dann der Registrierprozess rein
             */
            username = username_input.getText().toString();
            password = password_input.getText().toString();
            mail = mail_input.getText().toString();
            //MainFrame.setUsername(username);
            if ((username.length() < 1 || password.length() < 1 || mail.length() < 1)) {
                // Hier Warnmeldung?!
                Toast.makeText(getApplicationContext(), "Username und Passwort angeben",
                        Toast.LENGTH_LONG).show();
                Log.e("Error:", "Username, Mail, or Password is null");
            } else {
                JSONArray jsonArray = MainActivity.getJsonArray();
                boolean found = false;
                try {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = null;
                        jsonObject = jsonArray.getJSONObject(i);

                        String name = jsonObject.getString("username");
                        String email = jsonObject.getString("email");

                        if (username.equals(name) || mail.equalsIgnoreCase(email)) {
                            found = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error:", "Error in finding username");
                }
                //createUser(username, password);
                if (!found) {
                    new AddToList().execute();
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEimaladresse(mail);
                    user.setProfilPic(null);
                    user.setLevel(1);
                    user.setPunkte(0);
                    MainActivity.setUser(user);
                    Toast.makeText(getApplicationContext(), "Glückwusch! Deine Registrierung war erfolgreich! Viel Spaß!",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainFrame.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Username oder Email schon vergeben",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }


    private class AddToList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("email", mail));
            nameValuePairs.add(new BasicNameValuePair("level", "1"));
            nameValuePairs.add(new BasicNameValuePair("points", "0"));

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/registration.php");
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
