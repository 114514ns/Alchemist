package cn.pprocket.alchemist;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;

import java.io.File;

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
    @SneakyThrows
    public static void saveResult(String content) {
        File result = new File("chest.json");
        if (!result.exists()) {
            result.createNewFile();
        }
        FileWriter writer = FileWriter.create(result);
        writer.write(content,false);
    }
}
