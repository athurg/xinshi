package org.gooth.wechatmp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 企业号企业会话text消息
 * Created by fengjianbo on 2017/1/13.
 */

public class QYChatTextMessage extends JSONObject {
    private Integer agentId;
    private String content;
    private Boolean safe;
    private JSONObject textObj = new JSONObject();
    private JSONObject receiverObj = new JSONObject();

    public QYChatTextMessage(String sender, String receiver, String receiverType, String content) throws JSONException {
        textObj.put("content", content);
        put("text", textObj);

        receiverObj.put("id", receiver);
        receiverObj.put("type", receiverType);
        put("receiver", receiverObj);

        put("sender", sender);
        put("msgtype", "text");
    }

    static public QYChatTextMessage newSingleMessage(String sender, String receiver, String content) throws JSONException {
        return new QYChatTextMessage(sender, receiver, "single", content);
    }

    static public QYChatTextMessage newGroupMessage(String sender, String receiver, String content) throws JSONException {
        return new QYChatTextMessage(sender, receiver, "group", content);
    }
}