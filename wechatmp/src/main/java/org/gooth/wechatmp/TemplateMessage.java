package org.gooth.wechatmp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 微信企业号模板消息类
 * Created by fengjianbo on 2017/1/13.
 */

public class TemplateMessage extends JSONObject {
    private String templateId, toUser, url;
    private JSONObject data=new JSONObject();

    public TemplateMessage(String templateId) throws JSONException {
        this.put("template_id", templateId);
        this.put("data", data);
    }

    public TemplateMessage(String templateId, String toUser) throws JSONException {
        this.put("template_id", templateId);
        this.put("touser", toUser);
        this.put("data", data);
    }

    public TemplateMessage(String templateId, String toUser, String url) throws JSONException {
        this.put("template_id", templateId);
        this.put("touser", toUser);
        this.put("url", url);
        this.put("data", data);
    }

    public void addData(String key, String value, String color) throws JSONException {
        JSONObject newData = new JSONObject();
        newData.put("value", value);
        newData.put("color", color);
        data.put(key, newData);

        this.put("data", data);
    }

    public void addData(String key, String value) throws JSONException {
        addData(key, value, "#0000FF");
    }

    public void setToUser(String toUser) throws JSONException {
        this.put("touser", toUser);
    }
}