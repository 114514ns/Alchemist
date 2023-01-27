package cn.pprocket.alchemist;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;

public class Tools {
    public static String sendGet(String url) {
        int time = RandomUtil.randomInt(800,2000);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return HttpUtil.get(url);
    }
}
