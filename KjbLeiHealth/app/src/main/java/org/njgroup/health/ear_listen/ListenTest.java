package org.njgroup.health.ear_listen;

/**
 * 听力测试辅助工具类
 *
 * @author ChunYan
 */
public class ListenTest {
    public static final float minHZ = 20f;//人耳能听到的最低频率
    public static final float maxHZ = 20000f;//人耳能听到的最高频率
    float lowvalue;//能听到的最低频率
    float higvalue;//能听到的最高频率
    float testHz;//从20HZ 开始检测
    boolean isTestFinish;//判断测试是否结束

    public ListenTest() {
        this.lowvalue = 0;
        this.higvalue = 0;
        this.testHz = 0;
        this.isTestFinish = false;
    }

    /**
     * 根据当前能听到的声音的频率来设置能听到的最低频率或最高频率
     *
     * @param hearHZ
     */
    public void hearVoice(float hearHZ) {
        if (getLowvalue() == 0) {
            setLowvalue(hearHZ);
            setTestHz(maxHZ);
        } else {
            setHigvalue(hearHZ);
            setTestFinish(true);
        }
    }


    public void setTestFinish(boolean isTestFinish) {
        this.isTestFinish = isTestFinish;
    }

    /**
     * 测试是否已经结束了
     *
     * @return
     */
    public boolean isTestFinish() {
        boolean result = false;
        if (this.isTestFinish) {
            return true;
        } else {
            //听到的最大声小于3000也认为测试完成
            if (this.getLowvalue() > 0) {
                result = getTestHz() <= 3000;
            }

        }
        return result;

    }

    /**
     * 获取接下来要播放的声音的频率
     *
     * @return
     */
    public float getNextPlayFreq() {
        if (getTestHz() <= 3000) {
            setTestHz(this.testHz + 40);
        } else {
            setTestHz(this.testHz - 200);
        }
        return getTestHz();
    }

    public float getLowvalue() {
        return lowvalue;
    }

    public void setLowvalue(float lowvalue) {
        this.lowvalue = lowvalue;
    }

    public float getHigvalue() {
        return higvalue;
    }

    public void setHigvalue(float higvalue) {
        this.higvalue = higvalue;
    }

    public float getTestHz() {
        return testHz;
    }

    public void setTestHz(float testHz) {
        this.testHz = testHz;
    }
}
