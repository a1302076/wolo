package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by max on 03.05.15.
 */
public class SettingsFrame extends Activity implements View.OnClickListener {


    private TextView name;
    private TextView mail;
    private TextView level;
    private TextView punkte;
    private User user;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_frame);
        user = MainActivity.getUser();
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

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClick(View v) {
        if (v == back) {
            startActivity(new Intent(this, MainFrame.class));
        }
    }
}
