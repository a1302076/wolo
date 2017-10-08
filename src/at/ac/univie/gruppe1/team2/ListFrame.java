package at.ac.univie.gruppe1.team2;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListFrame extends ListActivity {

    private ProgressDialog pDialog;

    // JSON Elemente
    private static final String base = "base";
    private static final String date = "date";
    private static final String rates = "rates";
    private static final String land = "land";
    private static final String kurs = "kurs";
    private static final String landname = "landname";
    private static final String bildString = "ic_launcher";

    // JSONArray  1
    JSONArray list = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> onlineList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_frame);

		/*onlineList = new ArrayList<HashMap<String, String>>();

		ListView lv = getListView();

		// wenn ein Land ausgwewählt wird
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView tvland = (TextView) view.findViewById(R.id.land);
				String land = tvland.getText().toString();
				TextView tvkurs = (TextView) view.findViewById(R.id.kurs);
				String kurs = tvkurs.getText().toString();
					  
				//SecondFrame.setUserAuswahl(land, kurs);
				MainActivity.setWahlButton(land, view, kurs);
				finish();
			}
		});

		new GetList().execute();*/
    }

    private class GetList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ListFrame.this);
            pDialog.setMessage("Kurse werden aktualisiert...");
            pDialog.setCancelable(false);
            pDialog.show();

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
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String b = jsonObj.getString(base);
                    String d = jsonObj.getString(date);

                    System.out
                            .println("Die Base: " + b + " und das Date: " + d);

                    JSONObject rate = jsonObj.getJSONObject(rates);
                    JSONArray jarrayrate = rate.names();
                    for (int i = 0; i < jarrayrate.length(); i++) {

                        String landtag = jarrayrate.getString(i);
                        String landrate = rate.getString(jarrayrate
                                .getString(i));
                        String landganz = findelandname(landtag);
                        HashMap<String, String> a = new HashMap<String, String>();
                        a.put(land, landtag);
                        a.put(landname, landganz);
                        a.put(kurs, landrate);

                        if (landtag.equals("TRY")) {
                            landtag = "TRYA";
                        }
                        int imgid = getResources().getIdentifier("at.ac.univie.stolze.maximilian:drawable/" + landtag.toLowerCase(), null, null);

                        String istring = Integer.toString(imgid);
                        a.put(bildString, istring);
                        //System.out.println(getResources().getIdentifier("at.ac.univie.stolze.maximilian:drawable/"+landname.toLowerCase(),null,null));
                        onlineList.add(a);
                    }
                    // Collections.sort(onlineList.get(0));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out
                        .println("fehler beim holen der Liste, hier muss noch ein Log rein");
            }

            return null;
        }

        public String findelandname(String landtag) {

            if (landtag.equals("AUD")) {
                return "Austrialien";
            }
            if (landtag.equals("JPY")) {
                return "Japan";
            }
            if (landtag.equals("CAD")) {
                return "Kanada";
            }
            if (landtag.equals("CNY")) {
                return "China";
            }
            if (landtag.equals("RON")) {
                return "Rumänien";
            }
            if (landtag.equals("CZK")) {
                return "Tschechen";
            }
            if (landtag.equals("MXN")) {
                return "Mexico";
            }
            if (landtag.equals("ZAR")) {
                return "Südafrika";
            }
            if (landtag.equals("NZD")) {
                return "Neuseeland";
            }
            if (landtag.equals("GBP")) {
                return "Großbritanien";
            }
            if (landtag.equals("NOK")) {
                return "Norwegen";
            }
            if (landtag.equals("ILS")) {
                return "Israel";
            }
            if (landtag.equals("CHF")) {
                return "Schweiz";
            }
            if (landtag.equals("RUB")) {
                return "Russland";
            }
            if (landtag.equals("INR")) {
                return "Indien";
            }
            if (landtag.equals("THB")) {
                return "Thailand";
            }
            if (landtag.equals("IDR")) {
                return "Indonesien";
            }
            if (landtag.equals("TRY")) {
                return "Türkei";
            }
            if (landtag.equals("SGD")) {
                return "Singapur";
            }
            if (landtag.equals("HKD")) {
                return "Hongkong";
            }
            if (landtag.equals("HRK")) {
                return "Kroatien";
            }
            if (landtag.equals("DKK")) {
                return "Dänemark";
            }
            if (landtag.equals("SEK")) {
                return "Schweden";
            }
            if (landtag.equals("MYR")) {
                return "Malaysia";
            }
            if (landtag.equals("BRL")) {
                return "Brasilien";
            }
            if (landtag.equals("BGN")) {
                return "Bulgarien";
            }
            if (landtag.equals("PHP")) {
                return "Philippinen";
            }
            if (landtag.equals("HUF")) {
                return "Ungarn";
            }
            if (landtag.equals("PLN")) {
                return "Polen";
            }
            if (landtag.equals("USD")) {
                return "USA";
            }
            if (landtag.equals("KRW")) {
                return "Südkorea";
            }

            return "Land nicht bekannt";
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             *


             ListAdapter adapter = new SimpleAdapter(ListFrame.this, onlineList,
             R.layout.list_item, new String[] { land, landname, kurs, bildString },
             new int[] { R.id.land, R.id.landname, R.id.kurs, R.id.aud});

             setListAdapter(adapter);*/
        }
    }
}
