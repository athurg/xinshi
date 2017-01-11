package org.gooth.xinshi;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        editText = (EditText) findViewById(R.id.corpIdEditText);
        editText.setText(preference.getString("wechat_corp_id", ""));

        editText = (EditText) findViewById(R.id.chatSecretEditText);
        editText.setText(preference.getString("wechat_chat_secret", ""));

        editText = (EditText) findViewById(R.id.senderEditText);
        editText.setText(preference.getString("wechat_notify_sender", ""));

        editText = (EditText) findViewById(R.id.receiverEditText);
        editText.setText(preference.getString("wechat_notify_receiver", ""));
    }

    //更新配置按钮回调函数
    public void updatePreferences(View view) {
        SharedPreferences preference = getSharedPreferences("wechat_config", MODE_PRIVATE);
        Editor editor = preference.edit();

        EditText editText;
        editText = (EditText) findViewById(R.id.corpIdEditText);
        editor.putString("wechat_corp_id", editText.getText().toString());

        editText = (EditText) findViewById(R.id.chatSecretEditText);
        editor.putString("wechat_chat_secret", editText.getText().toString());

        editText = (EditText) findViewById(R.id.senderEditText);
        editor.putString("wechat_notify_sender", editText.getText().toString());

        editText = (EditText) findViewById(R.id.receiverEditText);
        editor.putString("wechat_notify_receiver", editText.getText().toString());

        editor.apply();
    }
}
