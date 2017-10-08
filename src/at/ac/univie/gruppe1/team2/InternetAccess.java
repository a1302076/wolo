package at.ac.univie.gruppe1.team2;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class InternetAccess {


    String jsonresponse;
    //String url = "http://api.fixer.io/latest";
    String url = null;

    public InternetAccess() {

        this.jsonresponse = null;
    }

    public String getList() {

        try {
            url = "http://s18354693.onlinehome-server.info/wolo_files/login.php";
            // http client..
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();
            jsonresponse = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonresponse;
    }
}

