package org.njgroup.health.gaochunyan.hearing;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.njgroup.health.R;

import java.util.Timer;
import java.util.TimerTask;


public class HearingActivity  extends Activity implements OnClickListener{
    public static final String TEXT_PLAY="开始测试";
    public static final String TEXT_STOP="停止测试";
    TextView tv_show;
    TextView tv_curHz;
    TextView tv_alarm;
    Button bt_play;
    Button bt_hearvoice;
    Timer timer_TestLow;//测试能听到的低频率的计时器
    Timer timer_TestHigh;//测试能听到的高频率的计时器
    ListenTest listenTest;
    Handler msgHandler;
    int testTime=0;//测试的次数,分高频测试和低频测试两次

    float curTestHZ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing);
        initView();
    }

    private void initView() {


        bt_play=(Button) findViewById(R.id.bt_play);
        bt_hearvoice=(Button) findViewById(R.id.bt_hearvoice);
        tv_show=(TextView) findViewById(R.id.tv_show);
        tv_alarm= (TextView) findViewById(R.id.tv_alarm);
        tv_curHz=(TextView) findViewById(R.id.tv_curHz);
        bt_play.setText(TEXT_PLAY);
        tv_show.setText("");
        tv_alarm.setText("点击开始测试");
        testTime=0;
        msgHandler=new Handler(getMainLooper());
        bt_hearvoice.setOnClickListener(this);
        bt_play.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_play:
                if(bt_play.getText().toString().equals(TEXT_PLAY)){
                    bt_play.setText(TEXT_STOP);
                    tv_alarm.setText("第一次测试");
                    tv_show.setTextColor(Color.GRAY);
                    listenTest=new ListenTest();
                    tv_curHz.setText(listenTest.getNextPlayFreq()+"HZ");
                    if(listenTest.getLowvalue()<=0){
                        //低频测试
                        if(timer_TestLow!=null){
                            timer_TestLow.cancel();
                        }
                        timer_TestLow=new Timer();
                        timer_TestLow.schedule(new TimerTask() {

                            @Override
                            public void run() {

                                curTestHZ=listenTest.getNextPlayFreq();
                                msgHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        //显示当前播放的频率
                                        tv_curHz.setText(curTestHZ+"HZ");
                                    }
                                });
                                //播放声音
                                GenerVoiceData.getInstance().playDefineFreqData(curTestHZ);


                            }
                        }, 100,4000);
                    }
                    else{

                    }


                }else{
                    bt_play.setText(TEXT_PLAY);
                    if(timer_TestLow!=null){
                        timer_TestLow.cancel();
                        timer_TestLow=null;
                    }
                    if(timer_TestHigh!=null){
                        timer_TestHigh.cancel();
                        timer_TestHigh=null;
                    }
                }
                break;
            case R.id.bt_hearvoice:
                if(listenTest.getLowvalue()<=0){
                    listenTest.hearVoice(listenTest.getTestHz());
                    if(timer_TestLow!=null){
                        timer_TestLow.cancel();
                        timer_TestLow=null;
                    }
                    tv_alarm.setText("第二次测试");
                    if(timer_TestLow!=null){
                        timer_TestLow.cancel();
                        timer_TestLow=null;
                    }
                    if(listenTest.getHigvalue()<=0){
                        //高频测试
                        if(timer_TestHigh!=null){
                            timer_TestHigh.cancel();
                        }

                        timer_TestHigh=new Timer();
                    }
                    timer_TestHigh.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            curTestHZ=listenTest.getNextPlayFreq();
                            msgHandler.post(new Runnable() {

                                @Override
                                public void run() {

                                    if(listenTest.isTestFinish()){
                                        tv_show.setTextColor(Color.RED);
                                        tv_alarm.setText("测试完成");
                                        if(listenTest.getLowvalue()==0||listenTest.getHigvalue()==0){
                                            tv_show.setText("您的测试可能结果有问题，建议重新测试~");
                                        }else{
                                            tv_show.setText("经过测试您能听到的声音的最低频率为:"+listenTest.getLowvalue()+"HZ,您能听到的最高频率为:"+listenTest.getHigvalue()+"HZ");
                                        }
                                        if(timer_TestHigh!=null){
                                            timer_TestHigh.cancel();
                                            timer_TestHigh=null;
                                        }
                                    }else{
                                        tv_curHz.setText(curTestHZ+"HZ");
                                    }


                                }
                            });
                            if(listenTest.isTestFinish()){
                                //如果测试频率已经达到20000HZ，则测试结束，停止播音
                                timer_TestHigh.cancel();
                                timer_TestHigh=null;


                            }else{
                                GenerVoiceData.getInstance().playDefineFreqData(curTestHZ);
                            }

                        }
                    }, 100,4000);
                }
                else if(listenTest.getLowvalue()>0&&listenTest.getHigvalue()<=0){
                    //测试完成
                    tv_alarm.setText("测试完成");
                    bt_play.setText(TEXT_PLAY);
                    listenTest.hearVoice(listenTest.getTestHz());
                    tv_show.setTextColor(Color.RED);
                    if(listenTest.getLowvalue()==0||listenTest.getHigvalue()==0){
                        tv_show.setText("您的测试可能结果有问题，建议重新测试~");
                    }else{
                        tv_show.setText("经过测试您能听到的声音的最低频率为:"+listenTest.getLowvalue()+"HZ,您能听到的最高频率为:"+listenTest.getHigvalue()+"HZ");
                    }
                    if(timer_TestLow!=null){
                        timer_TestLow.cancel();
                        timer_TestLow=null;
                    }
                    if(timer_TestHigh!=null){
                        timer_TestHigh.cancel();
                        timer_TestHigh=null;
                    }
                }
                break;
        }




    }
}
