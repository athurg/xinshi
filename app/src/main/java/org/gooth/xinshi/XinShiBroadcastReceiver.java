package org.gooth.xinshi;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.gooth.wechatmp.MPClient;
import org.gooth.wechatmp.QYChatTextMessage;
import org.gooth.wechatmp.QYClient;
import org.gooth.wechatmp.QYTextMessage;
import org.gooth.wechatmp.TemplateMessage;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * 系统广播监听类
 * <p>
 * 用于接收系统广播的短信、通话等消息
 * <p>
 * Created by Athurg on 2017/1/11.
 */

public class XinShiBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "XinShiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preference = context.getSharedPreferences("wechat_config", MODE_PRIVATE);
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        String action = intent.getAction();

        //接听电话事件
        if (action.equals("android.intent.action.PHONE_STATE")) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                String phoneNumber = intent.getStringExtra("incoming_number");
                //新来电时、来电未接听但对方挂断时，都会收到该广播消息。但后者的广播不会设置来电号码。
                if (phoneNumber == null || phoneNumber.equals("")) {
                    return;
                }

                //企业号聊天通知
                try {
                    String sender = preference.getString("wechat_qy_chat_sender", null);
                    String receiver = preference.getString("wechat_qy_chat_receiver", null);
                    QYChatTextMessage message = QYChatTextMessage.newSingleMessage(sender, receiver, "新来电：" + phoneNumber);

                    notifyByQYChatTextMessage(preference, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //企业号消息通知
                try {
                    Integer agentId = preference.getInt("wechat_qy_agent_id", 0);
                    String receiver = preference.getString("wechat_qy_notify_receiver", null);
                    QYTextMessage message = new QYTextMessage(agentId, "新来电：" + phoneNumber);
                    message.setToUser(receiver);

                    notifyByQYTextMessage(preference, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //公众号模板消息通知
                try {
                    String receiver = preference.getString("wechat_mp_receiver", null);
                    String templateid = preference.getString("wechat_mp_phone_notify_template_id", null);

                    TemplateMessage message = new TemplateMessage(templateid, receiver);
                    message.addData("phone_number", phoneNumber);
                    message.addData("time", timeFormat.format(new Date()));
                    notifyByMPTemplateMessage(preference, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        //接收短信事件
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();

            //获取短信数据
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) {
                Log.e("XINSHI", "短信广播中未找到短信数据");
                return;
            }

            String format = (String) bundle.get("format");

            String notifyText = "";
            String smsText = "";
            String prevAddress = "";
            Date date = null;
            HashMap<String, String> smss = new HashMap<String, String>();

            for (Object pdu : pdus) {
                SmsMessage message = null;

                //不同API版本createFromPdu不一样
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    message = SmsMessage.createFromPdu((byte[]) pdu, format);
                } else {
                    message = SmsMessage.createFromPdu((byte[]) pdu);
                }

                //短信可能由多个部分组成，如果发件人一致则直接合并内容，否则视为新信息
                String address = message.getOriginatingAddress();
                if (!address.equals(prevAddress)) {
                    date = new Date(message.getTimestampMillis());
                    notifyText += "\n发件人：" + address + "\n时间：" + timeFormat.format(date) + "\n内容：\n";
                    prevAddress = address;
                }
                smsText += message.getMessageBody();
                notifyText += message.getMessageBody();
            }

            //去掉多余的换行符
            notifyText = notifyText.trim().replaceAll("\r\n", "\n");

            //企业号聊天通知
            try {
                String sender = preference.getString("wechat_qy_chat_sender", null);
                String receiver = preference.getString("wechat_qy_chat_receiver", null);
                QYChatTextMessage message = QYChatTextMessage.newSingleMessage(sender, receiver, notifyText);

                notifyByQYChatTextMessage(preference, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //企业号消息通知
            try {
                Integer agentId = preference.getInt("wechat_qy_agent_id", 0);
                String receiver = preference.getString("wechat_qy_notify_receiver", null);
                QYTextMessage message = new QYTextMessage(agentId, notifyText);
                message.setToUser(receiver);

                notifyByQYTextMessage(preference, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //公众号模板消息通知
            try {
                String receiver = preference.getString("wechat_mp_receiver", null);
                String templateid = preference.getString("wechat_mp_sms_notify_template_id", null);

                TemplateMessage message = new TemplateMessage(templateid, receiver);
                message.addData("sender", prevAddress);
                message.addData("time", timeFormat.format(date));
                message.addData("content", smsText);
                notifyByMPTemplateMessage(preference, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyByQYTextMessage(final SharedPreferences preference, final QYTextMessage message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String corpId = preference.getString("wechat_qy_corp_id", null);
                String secret = preference.getString("wechat_qy_secret", null);
                String chatSecret = preference.getString("wechat_qy_chat_secret", null);

                try {
                    QYClient client = new QYClient(corpId, secret, chatSecret);
                    client.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }

    private void notifyByQYChatTextMessage(final SharedPreferences preference, final QYChatTextMessage message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String corpId = preference.getString("wechat_qy_corp_id", null);
                String chatSecret = preference.getString("wechat_qy_chat_secret", null);

                try {
                    QYClient client = new QYClient(corpId, "", chatSecret);
                        client.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }

    private void notifyByMPTemplateMessage(final SharedPreferences preference, final TemplateMessage message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String appid = preference.getString("wechat_mp_app_id", null);
                String secret = preference.getString("wechat_mp_app_secret", null);

                try {
                    new MPClient(appid, secret).sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }
}
