package com.project.himdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.project.himdemo.MainActivity;
import com.project.himdemo.R;

/**
 * Created by shaomiao on 2017/10/27.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText mUserNameEt,mPasswordEt;
    private String name,password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUserNameEt = (EditText) findViewById(R.id.et_username);
        mPasswordEt = (EditText) findViewById(R.id.et_password);
    }

    public void login(View view) {
//        EventBus.getDefault().post(new EventBusMsg("我是EventBus发送的数据"));
//        finish();
        if (!getTextAndCheck()){ return;}
        EMClient.getInstance().login(name, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                //以上两个方法是为了保证进入主页面后本地会话和群组都 load 完毕。
                Log.e("shaomiao","succes");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onError(int code, String error) {
                Toast.makeText(LoginActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress, String status) {
                Toast.makeText(LoginActivity.this, "在进行", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void register(View view) {
        if (!getTextAndCheck()){ return;}
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().createAccount(name, password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginActivity.this, "出现异常了", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private boolean getTextAndCheck() {
        name = mUserNameEt.getText().toString().trim();
        password = mPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            mUserNameEt.setError("用户名不能为空");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordEt.setError("密码不能为空");
            return false;
        }
        return true;
    }
}
