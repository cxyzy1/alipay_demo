package com.cxyzy.tools.alipay.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private final Handler handler = new InnerHandler(this);
    private static final int ALIPAY_RESULT = 1;
    /**
     * 支付宝支付成功返回码
     */
    public static String ALIPAY_SUCCESS = "9000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderInfo = "";//支付的关键字符串，来自自己服务器返回信息
                gotoAliPay(MainActivity.this, orderInfo, handler);
            }
        });

    }

    private static class InnerHandler extends MyHandler<Activity> {

        InnerHandler(MainActivity activity) {
            super(activity);
        }

        @Override
        public void handle(Activity activity, Message msg) {
            if (msg.what == ALIPAY_RESULT) {
                handleAliPayResult((MainActivity) activity, msg.obj);
            }
        }
    }

    /**
     * 调用支付宝支付
     * @param activity
     * @param orderInfo
     * @param handler
     */
    public void gotoAliPay(final Activity activity, final String orderInfo, final Handler handler) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask payTask = new PayTask(activity);
                Map<String, String> result = payTask.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = ALIPAY_RESULT;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 处理支付返回结果
     * @param activity
     * @param msgObj
     */
    public static void handleAliPayResult(MainActivity activity, Object msgObj) {
        if (msgObj == null) {
            Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
            return;
        }
        PayResult payResult = new PayResult((Map<String, String>) msgObj);
        String resultInfo = payResult.getResult();
        String resultMemo = payResult.getMemo();
        String resultStatus = payResult.getResultStatus();
        boolean isSuccess = TextUtils.equals(resultStatus, ALIPAY_SUCCESS);
        if(isSuccess)
        {
            Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(activity, "支付失败:"+resultInfo, Toast.LENGTH_SHORT).show();
        }
    }


}
