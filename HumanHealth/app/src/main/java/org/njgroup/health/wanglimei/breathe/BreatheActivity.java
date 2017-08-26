package org.njgroup.health.wanglimei.breathe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.njgroup.health.R;
import org.njgroup.health.wanglimei.breathe.widget.SoundDiscView;

import java.io.File;

/**
 * Created by WangLiMei on 2017/8/23.
 */

public class BreatheActivity extends AppCompatActivity {
    TextView recordTV;
    Button mBlowBtn;
    private static final int TEST_START = 0;
    private static final int TESTING = 1;
    private static final int TESTEND = 2;
    private static  int utilEnd = 20;
    private boolean isPause;
    private boolean isTesting;
    private boolean isBegin;
    float volume = 10000;
    private SoundDiscView soundDiscView;
    private BrealthRecoder mRecorder;
    private static final int msgWhat = 0x1001;
    private static int count = 0;
    private static int[] sampleValue = new int[200];
    private static int frequency = 0 ;
    private static int sum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brealthe);
        mRecorder = new BrealthRecoder();
        recordTV = (TextView) findViewById(R.id.recordTV);
        mBlowBtn = (Button)findViewById(R.id.mBlowBtn);
        mBlowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilEnd = 20;
                frequency=0;
                count = 0;
                sum = 0;
                isBegin = true;
                mBlowBtn.setEnabled(false);
                mBlowBtn.setText("测量中");
                recordTV.setText("倒计时"+utilEnd+"秒");
                handler.sendEmptyMessage(TEST_START);
            }
        });

    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case TEST_START:
                    if(isBegin)
                    {
                        if(!isTesting)
                        {
                            isTesting = true;
                            new Thread(new stopRun()).start();
                            new Thread(new blowRun()).start();
                        }
                    }
                    break;
                case TESTING:
                    recordTV.setText("倒计时"+utilEnd+"秒");
                    soundDiscView.refresh();
                    break;
                case TESTEND:
                    int avg = sum/200;
                    for (int i=1;i<199;i++)
                    {
                        if(sampleValue[i]>avg&&sampleValue[i]>sampleValue[i-1]&&sampleValue[i]>sampleValue[i+1])
                            frequency++;

                    }
                    recordTV.setText("您的呼吸频率为"+frequency*3+"次每分钟");
                    mBlowBtn.setText("测试完成");
                   // mBlowBtn.setEnabled(true);
                    break;

            }
            return false;
        }
    }) ;

    /**
     * 开始记录
     * @param fFile
     */
    public void startRecord(File fFile){
        try{
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {

            }else{
                Toast.makeText(this, "启动录音失败", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Toast.makeText(this, "录音机已被占用或录音权限被禁止", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        soundDiscView = (SoundDiscView) findViewById(R.id.soundDiscView);
        File file = FileUtil.createFile("temp.amr");
        if (file != null) {
            Log.v("file", "file =" + file.getAbsolutePath());
            startRecord(file);
        } else {
            Toast.makeText(getApplicationContext(), "创建文件失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 停止记录
     */
    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        mRecorder.delete();

    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(msgWhat);
        mRecorder.delete();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isPause = false;
        onResume();
    }

    class stopRun implements Runnable{


        @Override
        public void run() {
            while(!isPause)
            {
                try{
                    Thread.sleep(1000);
                    utilEnd--;
                    //      handler.sendEmptyMessage(TESTING);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(utilEnd == 0)
                {
                    isBegin = false;
                    isTesting = false;
                   // mRecorder.stopRecording();
                    handler.sendEmptyMessage(TESTEND);
                }

            }
        }
    }
   class blowRun implements Runnable{

       @Override
       public void run() {

           while(!isPause && isTesting)
           {
               volume = mRecorder.getMaxAmplitude();  //获取声压值
               if(volume > 0 && volume < 1000000) {
                   World.setDbCount(20 * (float)(Math.log10(volume)));  //将声压值转为分贝值
                   int temp = (int)World.dbCount;
                   if(count<200)
                   {
                       sampleValue[count] = temp;
                       sum = sum + temp;
                   }

                   System.out.print("temp:"+temp + "  ");

                   count++;
                   System.out.print("count:"+count+ "  ");

               }

               try{
                   Thread.sleep(100);

               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

               handler.sendEmptyMessage(TESTING);
           }
           if(!isTesting)
           {
               handler.sendEmptyMessage(TESTEND);
           }

       }
   }

}
