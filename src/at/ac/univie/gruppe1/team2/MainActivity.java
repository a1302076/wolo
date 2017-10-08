package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements View.OnClickListener {

    private static ImageButton loginButton;
    private Button regButton;
    private EditText username_input;
    private EditText password_input;
    private String username;
    private String password;
    private static User user;
    private NumberPicker time;

    private static JSONArray jsonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (ImageButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        regButton = (Button) findViewById(R.id.reg_button);
        regButton.setOnClickListener(this);


        username_input = (EditText) findViewById(R.id.username);
        password_input = (EditText) findViewById(R.id.password);
        new GetList().execute();

        user = new User();

    }


    public void onClick(View v) {

        if (v == regButton) {
            startActivity(new Intent(this, RegisterFrame.class));
        }
        if (v == loginButton) {
            if (username_input.length() < 1 || password_input.length() < 1) {

                Toast.makeText(getApplicationContext(), "Username und Password eingeben",
                        Toast.LENGTH_LONG).show();

            } else {
                username = username_input.getText().toString();
                password = password_input.getText().toString();
                /**
                 * Loginprozess kommt hier her
                 */
                new GetList().execute();

                try {
                    JSONArray Jarray = jsonArray;

                    boolean found = false;
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject jsonObject = null;
                        jsonObject = Jarray.getJSONObject(i);

                        String name = jsonObject.getString("username");

                        if (username.equals(name)) {
                            String pw = jsonObject.getString("password");
                            found = true;
                            if (password.equals(pw)) {
                                String mail = jsonObject.getString("email");
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

                                user.setUsername(name);
                                user.setPassword(pw);
                                user.setEimaladresse(mail);
                                user.setLevel(level);
                                user.setPunkte(punkte);
                                user.setProfilPic(bitmap);
                                //startActivity(new Intent(this, MainFrame.class));
                                Intent intent = new Intent(this, MainFrame.class);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                break;
                            } else {
                                Toast.makeText(getApplicationContext(), "Passwort falsch",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    if (!found) {
                        Toast.makeText(getApplicationContext(), "Username nicht vorhanden",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Login Fail!" + e);
                }
            }
        }
    }

    public static void setJsonArray(JSONArray jarray) {
        jsonArray = jarray;
    }


    private class GetList extends AsyncTask<Void, Void, Void> {

        private JSONArray jsonArray = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            InternetAccess access = new InternetAccess();

            // Making a request to url and getting response
            String jsonStr = access.getList();

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    jsonArray = new JSONArray(jsonStr);

                    //Log.d("Das Object:", jsonObj.toString());
                    MainActivity.setJsonArray(jsonArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("Liste noch nicht da", "fehler beim holen der Liste");
            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static JSONArray getJsonArray() {
        return jsonArray;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User u) {
        user = u;
    }
}
