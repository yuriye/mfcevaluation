package ru.ys.mfc.mkgu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import ru.ys.mfc.Settings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpAdapter {
    private static HttpAdapter instance;
    private static Settings settings = Settings.getInstance();

    private final Gson gson = new Gson();

    public static HttpAdapter getInstance() {
        if (instance == null) {
            instance = new HttpAdapter();
        }
        return instance;
    }

    public Map<String, String> getMkguFormVersion(String orderNumber) {
        Object result = new HashMap();

        try {
            InputStreamReader isr;
            String urlString = settings.getMkguUrlString() + settings.getGetMkguFormVersion() + "?orderNumber=" + URLEncoder.encode(orderNumber, "UTF-8");

            System.out.println(urlString);

            URL url = new URL(urlString);
            isr = new InputStreamReader(url.openStream(), "UTF-8");

            try {
                Type queueModelType = (new TypeToken<Map<String, String>>() {
                }).getType();
                result = (Map) this.gson.fromJson(isr, queueModelType);
            } finally {
                isr.close();
            }
        } catch (IOException var11) {
        }
        return (Map) result;
    }

    public List<MkguQuestionnaires> getMkguQuestionnaires() {
        List result = new ArrayList();

        try {
            URL url = new URL(settings.getMkguUrlString() + settings.getGetMkguQuestionnaires());
            InputStreamReader isr;

            isr = new InputStreamReader(url.openStream(), "UTF-8");

            try {

                result = (List) this.gson.fromJson(isr, (new TypeToken<ArrayList<MkguQuestionnaires>>() {
                }).getType());
            } finally {
                isr.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int postAnswers(String version, String orderNumber, String query) {
        try {
            String urlString = settings.getMkguUrlString() + settings.getSendMkguFormAnswers();
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(urlString);
            post.setHeader("User-Agent", "UserAgent");
            List<BasicNameValuePair> urlParameters = new ArrayList();
            urlParameters.add(new BasicNameValuePair("xmls[]", query));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = client.execute(post);
            return response.getStatusLine().getStatusCode();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
