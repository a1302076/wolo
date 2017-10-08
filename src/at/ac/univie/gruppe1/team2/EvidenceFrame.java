package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by max on 01.06.15.
 */
public class EvidenceFrame extends Activity implements View.OnClickListener {

    private ViewFlipper viewFlipper;
    private float lastX;

    private ProgressDialog pDialog;
    private ArrayList<Bitmap> bitmapList;
    private ArrayList<String> nameList;

    private static String taskname;

    private Button back;

    private TextView tName;
    private TextView user;
    private ImageView iv1;
    private LinearLayout lin1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evidence_switch_frame);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        // Add pics to list
        back = (Button) findViewById(R.id.back_buttonE);
        back.setOnClickListener(this);

        tName = (TextView) findViewById(R.id.task_name);
        tName.setText(taskname);
        user = (TextView) findViewById(R.id.user);
        iv1 = (ImageView) findViewById(R.id.imageView1);
        lin1 = (LinearLayout) findViewById(R.id.linid);
        new GetPicsList().execute();

    }



    // Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent)
    {
        switch (touchevent.getAction())
        {
            // when user first touches the screen to swap
            case MotionEvent.ACTION_DOWN:
            {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                float currentX = touchevent.getX();

                // if left to right swipe on screen
                if (lastX < currentX)
                {
                    // If no more View/Child to flip
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Left and current Screen will go OUT from Right
                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
                    // Show the next Screen
                    viewFlipper.showNext();
                }

                // if right to left swipe on screen
                if (lastX > currentX)
                {
                    if (viewFlipper.getDisplayedChild() == 1)
                        break;
                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Right and current Screen will go OUT from Left
                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
                    // Show The Previous Screen
                    viewFlipper.showPrevious();
                }
                break;
            }
        }
        return false;
    }



    private class GetPicsList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(EvidenceFrame.this);
            pDialog.setMessage("Liste wird geladen...");
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

                HttpGet httpGet = new HttpGet("http://s18354693.onlinehome-server.info/wolo_files/user_task_pics.php");
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

            bitmapList = new ArrayList<Bitmap>();
            nameList = new ArrayList<String>();
            Log.e("drin", "hier");
            if (jsonArray != null) {
                try {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String n = jsonObject.getString("user");
                        String t = jsonObject.getString("taskname");
                        String picString = jsonObject.getString("picture");

                        // Decode an add to list;
                        if(taskname.equals(t) && picString.length()>4) {
                            byte[] decodedByte = Base64.decode(picString, 0);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                            bitmapList.add(bitmap);
                            nameList.add(n);
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

            for(int i = 0; i < bitmapList.size(); i++) {
                addPicToView(bitmapList.get(i), nameList.get(i));

            }
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    public void addPicToView(Bitmap bitmap, String name){
        ImageView imageView = new ImageView(this);
        //setting image resource

        LinearLayout lin = new LinearLayout(this);
        lin.setLayoutParams(lin1.getLayoutParams());
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView tv = new TextView(this);

        tv.setText(name);
        tv.setLayoutParams(user.getLayoutParams());
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);


        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(iv1.getLayoutParams());
        lin.addView(tv);
        lin.addView(imageView);
        viewFlipper.addView(lin);
    }

    public static String getTaskname() {
        return taskname;
    }

    public static void setTaskname(String taskname) {
        EvidenceFrame.taskname = taskname;
    }


    // Manueller Switch


    @Override
    public void onClick(View v) {

        if(v == back){
            finish();
        }
    }




   /* private Button back;

    private Button left;
    private Button right;

    private ImageSwitcher switcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evidence_frame);

        back = (Button) findViewById(R.id.b_buttonO);
        back.setOnClickListener(this);

        left = (Button) findViewById(R.id.buttonLeft);
        left.setOnClickListener(this);
        right = (Button) findViewById(R.id.buttonRight);
        right.setOnClickListener(this);

        switcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == back){
            finish();
        }else
        if(v == left){
            Toast.makeText(getApplicationContext(), "previous Image", Toast.LENGTH_LONG).show();
            switcher.setImageResource(R.drawable.aud);
        }else
        if(v == right){
            Toast.makeText(getApplicationContext(), "Next Image",Toast.LENGTH_LONG).show();
            switcher.setImageResource(R.drawable.eur);
        }
    }*/
}
