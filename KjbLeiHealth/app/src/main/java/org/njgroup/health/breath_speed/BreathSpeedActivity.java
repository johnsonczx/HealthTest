package org.njgroup.health.breath_speed;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.njgroup.health.BaseActivity;
import org.njgroup.health.R;
import org.njgroup.health.heart_rate.PermitTool;

import java.io.File;

/**
 * @author WangLiMei
 */
public class BreathSpeedActivity extends BaseActivity implements View.OnClickListener{
    TextView recordTV;
    Button mBlowBtn;
    private static int utilEnd = 20;
    float volume = 10000;
    private SoundDiscView soundDiscView;
    private BreathRecoder mRecorder;
    private static final int msgWhat = 0x1001;
    private static int count = 0;
    private static int[] sampleValue = new int[200];
    private int frequency = 0;
    private static int sum = 0;
    private Handler handler1 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breath_speed);
        initTitleBar("呼吸频率", true);
        mRecorder = new BreathRecoder();
        recordTV = (TextView) findViewById(R.id.recordTV);
        mBlowBtn = (Button) findViewById(R.id.mBlowBtn);
        mBlowBtn.setOnClickListener(this);
        //安卓新版本中，敏感权限必须动态申请，此处进行权限申请
        PermitTool.verifyRecord(this);
        PermitTool.verifySdcard(this);
    }


    /**
     * 开始记录
     *
     * @param fFile
     */
    public void startRecord(File fFile) {
        try {
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {
                World.setDbCount(30);
                soundDiscView.refresh();
            } else {
                Toast.makeText(this, "启动录音失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "录音机已被占用或录音权限被禁止", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        World.setDbCount(30);
        soundDiscView.refresh();
        handler1.removeCallbacks(blowThread);
        mRecorder.delete();

    }

    @Override
    protected void onDestroy() {
        mRecorder.delete();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
      //  onResume();
        World.setDbCount(30);
        soundDiscView.refresh();
    }

    @Override
    public void onClick(View v) {
        utilEnd = 20;
        frequency = 0;
        count = 0;
        sum = 0;
        for (int i:sampleValue)
        {
            i = 0;
        }
        mBlowBtn.setEnabled(false);
        mBlowBtn.setText("测量中");
        handler1.post(blowThread);
    }



     Runnable blowThread = new Runnable(){
        @Override
        public void run()
        {

                volume = mRecorder.getMaxAmplitude();  //获取声压值
                if (volume > 0 && volume < 1000000)
                {
                    World.setDbCount(20 * (float) (Math.log10(volume)));  //将声压值转为分贝值
                    int temp = (int) World.dbCount;
                    if (count < 200)
                    {
                        sampleValue[count] = temp;
                        sum = sum + temp;
                    }

                    System.out.print("temp:" + temp + "  ");

                    count++;
                    System.out.print("count:" + count + "  ");

                }

                try {
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                soundDiscView.refresh();

            if(count == 200){
                int avg = sum / 200;
                for (int i = 1; i < 199; i++) {
                    if (sampleValue[i] > avg && sampleValue[i] >= sampleValue[i - 1] && sampleValue[i] > sampleValue[i + 1])
                        frequency++;

                }
                recordTV.setText("您的呼吸频率为" + frequency * 3 + "次每分钟");
                mBlowBtn.setText("开始");
                mBlowBtn.setEnabled(true);
                handler1.removeCallbacks(blowThread);
            }else {
                if (count % 10 == 0 && utilEnd != 1) {
                    utilEnd--;
                    recordTV.setText("倒计时" + utilEnd + "秒");
                }

                handler1.post(blowThread);
            }

        }
    };

}
