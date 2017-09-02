package org.njgroup.health.ear_listen;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;


/**
 * 听力测试数据产生类
 *
 * @author ChunYan
 */
public class GenerVoiceData {
    public static final float DURATION = 0.1f;
    public static final float LISTEN_SAMP_AMP = 16000.0f;
    public static final int LISTEN_SAMP_RATE = 48000;
    public static GenerVoiceData instance = null;
    AudioTrack audio;

    public static GenerVoiceData getInstance() {
        if (instance == null) {
            instance = new GenerVoiceData();
        }
        return instance;
    }

    /**
     * 根据输入的频率，返回播放的数据
     *
     * @param f 想要播放的频率
     */
    /*DURATION=0.1,LISTEN_SAMP_AMP=16000.0,LISTEN_SAMP_RATE=48000,此三值可以根据需要修改 48K采样率播放*/
    public short[] GetFreqData(double f) {
        int zq_num = (int) (DURATION * f + 0.5);
        double t = zq_num / f;

        int data_len = (int) (t * LISTEN_SAMP_RATE);
        short[] pdata = new short[data_len];
        if (pdata == null)
            return null;
        ;
        for (int i = 0; i < data_len; i++)
            pdata[i] = (short) (LISTEN_SAMP_AMP * Math.sin(Math.PI * 2 * i * f / LISTEN_SAMP_RATE) + 0.5);

        //dlen=data_len;

        return pdata;
    }

    /**
     * 播放制定频率的声音
     *
     * @param f 指定频率
     */
    public void playDefineFreqData(double f) throws Exception {

        stopPlay();
        int minBufSize = AudioTrack.getMinBufferSize(48000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audio = new AudioTrack(
                AudioManager.STREAM_MUSIC, // 指定在流的类型
                48000, // 设置音频数据的采样率 48k，如果是44.1k就是44100
                AudioFormat.CHANNEL_OUT_MONO, // 设置输出声道为双声道立体声，而CHANNEL_OUT_MONO类型是单声道
                AudioFormat.ENCODING_PCM_16BIT, // 设置音频数据块是8位还是16位，这里设置为16位。好像现在绝大多数的音频都是16位的了
                minBufSize,
                AudioTrack.MODE_STREAM // 设置模式类型，在这里设置为流类型，另外一种MODE_STATIC貌似没有什么效果
        );
        audio.play(); // 启动音频设备，下面就可以真正开始音频数据的播放了
        // 打开mp3文件，读取数据，解码等操作省略 ...
        short[] tempdata = GetFreqData(f);

        short playdata[] = new short[tempdata.length * 30];
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < tempdata.length; j++) {
                playdata[i * tempdata.length + j] = tempdata[j];
            }

        }
        short[] buffer = new short[2048];
        Log.d("LISTENTEST", "长度为：" + playdata.length);
        int count = 0;
        while (true) {
            // 最关键的是将解码后的数据，从缓冲区写入到AudioTrack对象中
            //audio.write(buffer, 0, 4096);
            for (int i = 0; i < 2048; i++) {
                buffer[i] = playdata[count * 2048 + i];
            }
            audio.write(buffer, 0, 2048);
            count++;
            if ((count + 1) * 2048 >= playdata.length) break;
        }
        // 最后别忘了关闭并释放资源
        audio.stop();
        audio.release();
    }

    /**
     * 停止播放，并释放资源
     */
    public void stopPlay() {
        if (audio != null) {
            try {
                audio.stop();
                audio.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
