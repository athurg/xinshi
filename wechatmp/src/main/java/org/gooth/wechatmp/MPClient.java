package org.gooth.wechatmp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * 微信公众平台客户端
 * Created by fengjianbo on 2017/1/13.
 */

public class MPClient {
    private String TAG = "MPClient";
    private String BASE_URL = "https://api.weixin.qq.com/cgi-bin";

    private String appid, secret;
    private long expireAtInMillis;
    private String accessTokenCache;

    public MPClient(String appid, String secret) {
        this.appid = appid;
        this.secret = secret;
    }

    //发送模板消息
    // 使用范例：
    //         TemplateMessage message = new TemplateMessage(templateId);
    //         message.addData("data1", "data with default color");
    //         message.addData("data1", "data with color", "#RRGGBB");
    //         new MPClient().sendMessage(message);
    public void sendMessage(TemplateMessage msg) throws JSONException {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "获取AccessToken失败");
            return;
        }

        //构造并发送消息
        String path = "/message/template/send?access_token=" + accessToken;
        JSONObject result = request("POST", path, msg.toString());
    }

    //发送带Body数据的API请求
    private void requestAPI(String method, String path, String body) throws JSONException {
        String accessToken = getAccessToken();

        if (path.contains("?")) {
            path += "&access_token=" + accessToken;
        } else {
            path += "?access_token=" + accessToken;
        }

        request(method, path, "");
    }

    //发送不带Body数据的API请求
    public void requestAPI(String method, String path) throws JSONException {
        requestAPI(method, path, "");
    }

    //刷新调用凭据
    private String getAccessToken() throws JSONException {
        long nowInMillis = (new Date()).getTime();
        if (!TextUtils.isEmpty(accessTokenCache) && nowInMillis<expireAtInMillis) {
            return accessTokenCache;
        }

        String path = "/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
        JSONObject result = request("GET", path, "");
        if (result == null) {
            return accessTokenCache;
        }

        expireAtInMillis = nowInMillis + result.getInt("expires_in") * 1000 - 100;
        accessTokenCache = result.getString("access_token");
        return accessTokenCache;
    }

    private JSONObject request(String method, String path, String body) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            if (!TextUtils.isEmpty(body)) {
                conn.setDoOutput(true);
                conn.getOutputStream().write(body.getBytes());
            }

            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            conn.disconnect();
            return new JSONObject(response.toString());
        } catch (Exception e) {
            Log.e(TAG, "HTTP请求错误");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}