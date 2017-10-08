package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by max on 03.05.15.
 */
public class ProfilFrame extends Activity implements View.OnClickListener {


    private TextView name;
    private TextView mail;
    private TextView level;
    private TextView punkte;
    private static User user;

    private static ImageView profilPic;
    private static Bitmap pic;


    // private Button back;
    private Button pwandern;
    private Button taskButton;

    // PW ändern popup
    TableRow popRow;
    private PopupWindow popupMessage;
    private boolean pop;

    private EditText eingabepw;
    private EditText neueingabepw;
    private Button changeB;

    private View popUpView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_frame);
        user = MainActivity.getUser();


        profilPic = (ImageView) findViewById(R.id.profilImg);

        if (user.getProfilPic() == null) {
            profilPic.setImageResource(R.drawable.ic_launchersmall);
            pic = null;
        } else {
            pic = user.getProfilPic();
            profilPic.setImageBitmap(user.getProfilPic());
        }
        name = (TextView) findViewById(R.id.nameView);
        name.setText(user.getUsername());


        mail = (TextView) findViewById(R.id.mailView);
        mail.setText(user.getEimaladresse());

        String lvl = Integer.toString(user.getLevel());
        level = (TextView) findViewById(R.id.levelView);
        level.setText(lvl);

        String p = Integer.toString(user.getPunkte());
        punkte = (TextView) findViewById(R.id.punkteView);
        punkte.setText(p);

        //back = (Button) findViewById(R.id.back_button);
        //back.setOnClickListener(this);
        pwandern = (Button) findViewById(R.id.change_pwd_button);
        pwandern.setOnClickListener(this);

        taskButton = (Button) findViewById(R.id.task_button);
        taskButton.setOnClickListener(this);

        //eingabepw = (EditText) findViewById(R.id.textViewA);
        //neueingabepw = (EditText) findViewById(R.id.textViewNP);


        popRow = (TableRow) findViewById(R.id.pop);

        pop = false;


        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popUpView = inflater.inflate(R.layout.profil_popup, null, false);
        //View popupView = inflater.inflate(R.layout.profil_popup,(ViewGroup)findViewById(R.id.profil_frame2));

        popupMessage = new PopupWindow(
                inflater.inflate(R.layout.profil_popup, null, false),
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                true);
        popupMessage.setBackgroundDrawable(new BitmapDrawable());

        popupMessage.setContentView(popUpView);

        changeB = (Button) popUpView.findViewById(R.id.pop_button);
        changeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == changeB) {
                    String aktuellespw = MainActivity.getUser().getPassword();
                    User user = MainActivity.getUser();

                    eingabepw = (EditText) popUpView.findViewById(R.id.pw);
                    neueingabepw = (EditText) popUpView.findViewById(R.id.neupw);
                    String eingegebenespw = eingabepw.getText().toString();
                    String neuespw = neueingabepw.getText().toString();

                    if (eingegebenespw.length() >= 1 && neuespw.length() >= 1) {

                        if ((aktuellespw.equals(eingegebenespw))) {
                            if(user.getPassword().equals(eingegebenespw)){
                                Toast.makeText(getApplicationContext(), "Definition Ändern: wechseln, durch etwas anderes ersetzen, umformen, wandeln",
                                        Toast.LENGTH_LONG).show();
                            }else {
                                user.setPassword(neuespw);
                                Toast.makeText(getApplicationContext(), "Passwort erfolgreich geändert",
                                        Toast.LENGTH_LONG).show();
                                new UpdateUserlist().execute();
                                //startActivity(new Intent(this, MainFrame.class));
                                popupMessage.dismiss();
                                pop = false;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Passwort falsch",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Passwort eingeben",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }


    public static void updatePic() {
        user = MainActivity.getUser();
        if (user.getProfilPic() == null) {
            profilPic.setImageResource(R.drawable.ic_launchersmall);
        } else {
            Bitmap pic = user.getProfilPic();
            if (pic.getHeight() > 100 || pic.getWidth() > 100) {
                Log.i("Picture", "Ist noch zu groß!");
            }
            profilPic.setImageBitmap(user.getProfilPic());
        }
    }


    public void onClick(View v) {
        if (v == taskButton) {
            //startActivity(new Intent(this, DoneTasksFrame.class));
            startActivity(new Intent(this, UploadPicFrame.class));
        }
        if (v == pwandern) {
            if (pop) {
                popupMessage.dismiss();
                pop = false;
            } else {
                pop = true;
                //popupMessage.showAsDropDown(popRow, 0, 100);
                popupMessage.showAtLocation(popRow, Gravity.CENTER, 0, 0);


            }

        }
    }

    @Override
    public void onBackPressed() {
    }

    private class UpdateUserlist extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("username", user.getUsername().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", user.getPassword().toString()));
       /*     nameValuePairs.add(new BasicNameValuePair("email", user.getEimaladresse().toString()));
            nameValuePairs.add(new BasicNameValuePair("level", Integer.toString(user.getLevel()).toString()));
            nameValuePairs.add(new BasicNameValuePair("points", Integer.toString(user.getPunkte()).toString()));*/

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/updateUser.php");
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

    public static Bitmap getPic() {
        return pic;
    }

    public static void setPic(Bitmap pic) {
        ProfilFrame.pic = pic;
    }
}
