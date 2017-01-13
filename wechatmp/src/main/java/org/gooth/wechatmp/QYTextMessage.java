package org.gooth.wechatmp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 企业号text消息
 * Created by fengjianbo on 2017/1/13.
 */

public class QYTextMessage extends JSONObject {
    private JSONObject text = new JSONObject();

    public QYTextMessage(Integer agentId, String content) throws JSONException {
        text.put("content", content);
        put("text", text);
        put("msgtype", "text");
        put("agentid", agentId);
    }

    public void setToUser(String to) throws JSONException {
        put("touser", to);
    }

    public void setToParty(String to) throws JSONException {
        put("touser", to);
    }

    public void setToTag(String to) throws JSONException {
        put("touser", to);
    }

    public void setSafe(boolean safe) throws JSONException {
        put("safe", safe ? 1 : 0);
    }
}