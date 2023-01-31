package cn.pprocket.alchemist;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @SneakyThrows
    public static void writeFile(String content,String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileWriter writer = FileWriter.create(f);
        writer.write(content,false);
    }
    public static boolean isChinese(String str)
    {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
