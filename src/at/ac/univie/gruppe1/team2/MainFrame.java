package at.ac.univie.gruppe1.team2;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainFrame extends TabActivity implements View.OnClickListener {

    //private ActionBar.Tab tab1;
    private ActionBar tabs;
    private static TextView userView;
    private static String username;
    private TabHost tabhost;
    private NumberPicker time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_frame);
        userView = (TextView) findViewById(R.id.user_name);
        userView.setText(username);

      /*  TabHost tabHost = getTabHost();

        // setNewTab(context, tabHost, tag, title, icon, contentID);
        this.setNewTab(this, tabHost, "tab1", R.string.textTabTitle1, android.R.drawable.star_on, R.id.tab1);
        this.setNewTab(this, tabHost, "tab2", R.string.textTabTitle2, android.R.drawable.star_on, R.id.tab2);
        this.setNewTab(this, tabHost, "tab3", R.string.textTabTitle3, android.R.drawable.star_on, R.id.tab3);

        //tabHost.setCurrentTabByTag("tab2"); //-- optional to set a tab programmatically.
        //tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tab1");
        tabHost.setCurrentTabByTag("tab2");
        tabHost.setCurrentTabByTag("tab3");*/

        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(MainFrame.this, HomeFrame.class);
        spec = tabHost.newTabSpec("tab1").setIndicator("Home").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(MainFrame.this, TasksFrame.class);
        spec = tabHost.newTabSpec("tab2").setIndicator("Tasks").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(MainFrame.this, ProfilFrame.class);
        spec = tabHost.newTabSpec("tab3").setIndicator("Profil").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(MainFrame.this, LogoutFrame.class);
        spec = tabHost.newTabSpec("tab4").setIndicator("Logout").setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);


    }

    private void setNewTab(Context context, TabHost tabHost, String tag, int title, int icon, int contentID) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        String titleString = getString(title);
        tabSpec.setIndicator(titleString, context.getResources().getDrawable(android.R.drawable.star_on));
        tabSpec.setContent(contentID);

    }

    @Override
    public void onClick(View v) {
        /*if (v == tab1.getCustomView()) {
            // finish() zeigt, wenn eine Activity abgeschlossen ist
            startActivity(new Intent(this, MainActivity.class));
        }*/
    }


    public static void setUsername(String name) {
        username = name;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Logge dich bitte aus, um die App zu verlassen",
                Toast.LENGTH_LONG).show();
    }
}
