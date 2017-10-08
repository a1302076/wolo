package at.ac.univie.gruppe1.team2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.util.ArrayList;

/**
 * Created by max on 27.05.15.
 */
public class FinishTaskFrame extends Activity implements View.OnClickListener {


    static Task task;
    Button ready;
    RadioButton like;
    RadioButton dislike;
    boolean complete;

    // Upload
    private static final int PICK_IMAGE = 1;
    private ImageView imgView;

    private Button esc;

    private Bitmap bitmap;
    private ProgressDialog dialog;
    private String picString;

    private ProgressDialog pDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_task_frame);

        complete = false;
        ready = (Button) findViewById(R.id.please_button);
        ready.setOnClickListener(this);

        esc = (Button) findViewById(R.id.escape_button);
        esc.setOnClickListener(this);

        like = (RadioButton) findViewById(R.id.radioButtonLike);
        dislike = (RadioButton) findViewById(R.id.radioButtonDislike);

        // Upload
        imgView = (ImageView) findViewById(R.id.ImageView);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.imageuploadmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ic_menu_gallery:
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            PICK_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "ERROR 42",
                            Toast.LENGTH_LONG).show();
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        if(v == esc){
            Toast.makeText(getApplicationContext(), "Task abgebrochen!",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainFrame.class);
            // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else
        if (v == ready) {
            if (bitmap == null) {
                complete = false;
            } else {

                //new UploadPic().execute();
                complete = true;

            }

            if (complete) {
                if (like.isChecked()) {
                    Log.i("Like", "jawollo");
                    task.setLikes(task.getLikes() + 1);
                    new LikeTheTask().execute();

                    Toast.makeText(getApplicationContext(), "Glückwunsch!",
                            Toast.LENGTH_LONG).show();


                    Intent i = new Intent(this, MainFrame.class);
                    // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                } else if (dislike.isChecked()) {
                    if (task.getLikes() > 0) {
                        task.setLikes(task.getLikes() - 1);
                        new LikeTheTask().execute();


                        Toast.makeText(getApplicationContext(), "Glückwunsch!",
                                Toast.LENGTH_LONG).show();
                        Intent i = new Intent(this, MainFrame.class);
                        // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Gib bitte eine Bewertung ab ;)",
                            Toast.LENGTH_LONG).show();
                }


            }else {

                Toast.makeText(getApplicationContext(), "Lade bitte einen Beweis hoch, um Punkte zu bekommen)",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
    }

    private class LikeTheTask extends AsyncTask<Void, Void, Void> {

       /* @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(FinishTaskFrame.this);
            pDialog.setMessage("Hochladen...");
            pDialog.setCancelable(false);
            pDialog.show();

        }*/

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                //[{"ID":"1","creator":"testuser","name":"testtask","info":"test","instruction":"t","time":"90","level":"1","points":"100","likes":null,"type":"Ernst"}]
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("id", username));
                nameValuePairs.add(new BasicNameValuePair("taskname", task.getName()));
                nameValuePairs.add(new BasicNameValuePair("likes", Integer.toString(task.getLikes())));


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/like_task.php");
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

            //----Pic upload
            try {
                User user = MainActivity.getUser();
                //[{"ID":"1","creator":"testuser","name":"testtask","info":"test","instruction":"t","time":"90","level":"1","points":"100","likes":null,"type":"Ernst"}]
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("id", username));
                nameValuePairs.add(new BasicNameValuePair("user", user.getUsername()));
                nameValuePairs.add(new BasicNameValuePair("taskname", task.getName()));
                nameValuePairs.add(new BasicNameValuePair("picture", picString));


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/updateTaskPicture.php");
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

        /*@Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Glückwunsch",
                        Toast.LENGTH_LONG).show();

            }
        }*/


    }

    public Task getTask() {
        return task;
    }

    public static void setTask(Task t) {
        task = t;
    }


    // Upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    String filePath = null;
                    ParcelFileDescriptor parcelFD = null;
                    try {
                        parcelFD = getContentResolver().openFileDescriptor(imageUri, "r");
                        FileDescriptor imageSource = parcelFD.getFileDescriptor();

                      /*  // OI FILE Manager
                        String filemanagerstring = selectedImageUri.getPath();

                        // MEDIA GALLERY
                        String selectedImagePath = getPath(selectedImageUri);

                        if (selectedImagePath != null) {
                            filePath = selectedImagePath;
                        } else if (filemanagerstring != null) {
                            filePath = filemanagerstring;
                        } else {
                            Toast.makeText(getApplicationContext(), "Pfad nicht gefunden",
                                    Toast.LENGTH_LONG).show();
                        }*/

                        if (imageSource != null) {
                            decodeFile(imageSource);
                        } else {
                            bitmap = null;
                            Toast.makeText(getApplicationContext(), "Pfad nicht gefunden",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error mit Pfad",
                                Toast.LENGTH_LONG).show();
                        Log.e(e.getClass().getName(), e.getMessage(), e);
                        finish();
                    }
                }
                break;
            default:
        }
    }

   /* private class UploadPic extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(FinishTaskFrame.this);
            pDialog.setMessage("Hochladen...");
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                User user = MainActivity.getUser();
                //[{"ID":"1","creator":"testuser","name":"testtask","info":"test","instruction":"t","time":"90","level":"1","points":"100","likes":null,"type":"Ernst"}]
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("id", username));
                nameValuePairs.add(new BasicNameValuePair("user", user.getUsername()));
                nameValuePairs.add(new BasicNameValuePair("taskname", task.getName()));
                nameValuePairs.add(new BasicNameValuePair("picture", picString));


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://s18354693.onlinehome-server.info/wolo_files/updateTaskPicture.php");
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Bild erfolgreich hochgeladen",
                        Toast.LENGTH_LONG).show();

            }
        }

    }*/


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void decodeFile(FileDescriptor filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(filePath, null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFileDescriptor(filePath, null, o2);

        //Log.e("DAS Bild:" , bitmap.toString());

        imgView.setImageBitmap(bitmap);
        //Log.e("DAS Bild-Matrix:", imgView.getImageMatrix().toString());


        //----------------------------
        Bitmap immagex = bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        picString = imageEncoded;


        // Log.i("DER String", imageEncoded);
        /*
        byte[] decodedByte = Base64.decode(imageEncoded, 0);
        bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        */
        //---------------------------

    }
}
