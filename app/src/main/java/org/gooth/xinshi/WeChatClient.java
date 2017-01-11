package org.gooth.xinshi;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Athurg on 2017/1/11.
 */

public class WeChatClient {
    private String corpId, secret, chatSecret, sender, receiver, text;

    public WeChatClient(String corpId, String secret, String chatSecret) {
        this.corpId = corpId;
        this.secret = secret;
        this.chatSecret = chatSecret;
    }


    //获取常规微信API所需的AccessToken
    public String getAccessToken(){
        return getToken(secret);
    }

    //获取企业聊天相关微信API所需的AccessToken
    public String getChatAccessToken(){
        return getToken(chatSecret);
    }

    //发送企业聊天单聊文本型消息
    public void sendSingleChatTextMessage(String sender, String receiver, String content) {
        //获取发送消息的AccessToken
        String accessToken = getChatAccessToken();
        if (accessToken==null) {
            Log.i("WECHAT", "获取AccessToken失败");
            return;
        }

        //构造并发送消息
        try {
            JSONObject receiverObj = new JSONObject();
            receiverObj.put("type", "single");
            receiverObj.put("id", receiver);

            JSONObject textObj = new JSONObject();
            textObj.put("content", content);

            JSONObject requestParam = new JSONObject();
            requestParam.put("sender", sender);
            requestParam.put("msgtype", "text");
            requestParam.put("text", textObj);
            requestParam.put("receiver", receiverObj);

            JSONObject result = request("POST", "/chat/send?access_token=" + accessToken, requestParam.toString());
            Log.d("WECHAT", "发送企业单聊消息响应：" + result.toString());
        } catch (JSONException e) {
            Log.e("WECHAT", "JSON参数处理错误");
            e.printStackTrace();
            return;
        }
    }

    private String getToken(String secret) {
        //获取发送消息的AccessToken
        JSONObject result = request("GET", "/gettoken?corpid=" + corpId + "&corpsecret=" + secret, "");
        if (result == null) {
            return null;
        }
        try {
            return result.getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject request(String method, String path, String body) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://qyapi.weixin.qq.com/cgi-bin" + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            connection.getOutputStream().write(body.getBytes());

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return new JSONObject(response.toString());
        } catch (Exception e) {
            Log.i("WECHAT", "HTTP请求错误");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
