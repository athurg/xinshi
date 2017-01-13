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
 * 微信企业号公众平台客户端
 * Created by fengjianbo on 2017/1/13.
 */

public class QYClient {
    private String TAG = "QYClient";
    private String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin";

    private String corpId, corpSecret, chatSecret;

    //AccessToken缓存及其过期时间
    private long accessTokenExpireAtInMillis, chatAccessTokenExpireAtInMillis;
    private String accessTokenCache, chatAccessTokenCache;

    public QYClient(String corpId, String corpSecret, String chatSecret) {
        this.corpId = corpId;
        this.corpSecret = corpSecret;
        this.chatSecret = chatSecret;
    }

    //发送文本消息
    public JSONObject sendMessage(QYTextMessage msg) throws JSONException {
        return requestAPI("POST", "/message/send", msg.toString());
    }

    //发送企业聊天单聊文本型消息
    public JSONObject sendMessage(QYChatTextMessage msg) throws JSONException {
        return requestChatAPI("POST", "/chat/send", msg.toString());
    }

    //发送带Body数据的API请求
    private JSONObject requestAPI(String method, String path, String body) throws JSONException {
        String accessToken = getAccessToken();

        if (path.contains("?")) {
            path += "&access_token=" + accessToken;
        } else {
            path += "?access_token=" + accessToken;
        }

        return request(method, path, body);
    }

    //发送不带Body数据的API请求
    public JSONObject requestAPI(String method, String path) throws JSONException {
        return requestAPI(method, path, "");
    }

    //刷新调用凭据
    private String getAccessToken() throws JSONException {
        long nowInMillis = (new Date()).getTime();
        if (!TextUtils.isEmpty(accessTokenCache) && nowInMillis<accessTokenExpireAtInMillis) {
            return accessTokenCache;
        }

        String path = "/gettoken?corpid=" + corpId + "&corpsecret=" + corpSecret;
        JSONObject result = request("GET", path, "");
        if (result == null) {
            return accessTokenCache;
        }

        accessTokenExpireAtInMillis = nowInMillis + result.getInt("expires_in") * 1000 - 100;
        accessTokenCache = result.getString("access_token");
        return accessTokenCache;
    }

    //发送带Body数据的企业聊天会话API请求
    private JSONObject requestChatAPI(String method, String path, String body) throws JSONException {
        String accessToken = getChatAccessToken();

        if (!path.contains("?")) {
            path += "?access_token=" + accessToken;
        } else {
            path += "&access_token=" + accessToken;
        }

        return request(method, path, body);
    }

    //发送不带Body数据的企业聊天会话API请求
    public JSONObject requestChatAPI(String method, String path) throws JSONException {
        return requestChatAPI(method, path, "");
    }

    //刷新调用凭据
    private String getChatAccessToken() throws JSONException {
        long nowInMillis = (new Date()).getTime();

        if (!TextUtils.isEmpty(chatAccessTokenCache) && nowInMillis<chatAccessTokenExpireAtInMillis) {
            return chatAccessTokenCache;
        }

        String path = "/gettoken?corpid=" + corpId + "&corpsecret=" + chatSecret;
        JSONObject result = request("GET", path, "");
        if (result == null) {
            return chatAccessTokenCache;
        }

        chatAccessTokenExpireAtInMillis = nowInMillis + result.getInt("expires_in") * 1000 - 100;
        chatAccessTokenCache = result.getString("access_token");
        return chatAccessTokenCache;
    }

    private JSONObject request(String method, String path, String body) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + path);

            Log.i(TAG, "request: "+path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            if (!TextUtils.isEmpty(body)) {
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
