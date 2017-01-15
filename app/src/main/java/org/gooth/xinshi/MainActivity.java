package org.gooth.xinshi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPreference();
    }

    //加载现有配置
    void loadPreference() {
        EditText editText;
        SharedPreferences preference = getSharedPreferences("wechat_config", MODE_PRIVATE);

        editText = (EditText) findViewById(R.id.mpAppIdEditText);
        editText.setText(preference.getString("wechat_mp_app_id", ""));

        editText = (EditText) findViewById(R.id.mpAppSecretEditText);
        editText.setText(preference.getString("wechat_mp_app_secret", null));

        editText = (EditText) findViewById(R.id.mpReceiverEditText);
        editText.setText(preference.getString("wechat_mp_receiver", ""));

        editText = (EditText) findViewById(R.id.qyCorpIdEditText);
        editText.setText(preference.getString("wechat_qy_corp_id", ""));

        editText = (EditText) findViewById(R.id.qySecretEditText);
        editText.setText(preference.getString("wechat_qy_secret", ""));

        editText = (EditText) findViewById(R.id.qyChatSecretEditText);
        editText.setText(preference.getString("wechat_qy_chat_secret", ""));

        editText = (EditText) findViewById(R.id.qyChatSenderEditText);
        editText.setText(preference.getString("wechat_qy_chat_sender", ""));

        editText = (EditText) findViewById(R.id.qyChatReceiverEditText);
        editText.setText(preference.getString("wechat_qy_chat_receiver", ""));

        editText = (EditText) findViewById(R.id.qyNotifyReceiverEditText);
        editText.setText(preference.getString("wechat_qy_notify_receiver", ""));

        editText = (EditText) findViewById(R.id.qyAgentIdEditText);
        Integer agentid = preference.getInt("wechat_qy_agent_id", 0);
        editText.setText(agentid.toString());
    }

    void savePreferece() {
        SharedPreferences preference = getSharedPreferences("wechat_config", MODE_PRIVATE);
        Editor editor = preference.edit();

        EditText editText;

        editText = (EditText) findViewById(R.id.mpAppIdEditText);
        editor.putString("wechat_mp_app_id", editText.getText().toString());

        editText = (EditText) findViewById(R.id.mpAppSecretEditText);
        editor.putString("wechat_mp_app_secret", editText.getText().toString());

        editText = (EditText) findViewById(R.id.mpReceiverEditText);
        editor.putString("wechat_mp_receiver", editText.getText().toString());

        editText = (EditText) findViewById(R.id.mpSmsNotifyTemplateIdEditText);
        editor.putString("wechat_mp_sms_notify_template_id", editText.getText().toString());

        editText = (EditText) findViewById(R.id.mpPhoneNotifyTemplateIdEditText);
        editor.putString("wechat_mp_phone_notify_template_id", editText.getText().toString());

        editText = (EditText) findViewById(R.id.qyCorpIdEditText);
        editor.putString("wechat_qy_corp_id", editText.getText().toString());

        editText = (EditText) findViewById(R.id.qySecretEditText);
        editor.putString("wechat_qy_secret", editText.getText().toString());

        editText = (EditText) findViewById(R.id.qyChatSecretEditText);
        editor.putString("wechat_qy_chat_secret", editText.getText().toString());

        editText = (EditText) findViewById(R.id.qyChatSenderEditText);
        editor.putString("wechat_qy_chat_sender", editText.getText().toString());

        editText = (EditText) findViewById(R.id.qyChatReceiverEditText);
        editor.putString("wechat_qy_chat_receiver", editText.getText().toString());

        editText = (EditText) findViewById(R.id.qyNotifyReceiverEditText);
        editor.putString("wechat_qy_notify_receiver", editText.getText().toString());

        editText = (EditText) findViewById(R.id.qyAgentIdEditText);
        editor.putInt("wechat_qy_agent_id", Integer.parseInt(editText.getText().toString()));

        editor.apply();
    }

    //更新配置按钮回调函数
    public void updatePreferences(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.update_preference);
        builder.setMessage(R.string.update_preference_description);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                savePreferece();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                loadPreference();
            }
        });

        builder.create().show();
    }
}