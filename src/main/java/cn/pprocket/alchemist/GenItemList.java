package cn.pprocket.alchemist;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static cn.pprocket.alchemist.GenChestList.getHigher;
import static cn.pprocket.alchemist.Main.*;

public class GenItemList {
    public static void main(String[] args) {
        Main.init();
       Tools.writeFile(gson.toJson(getItemList()),"items.json");
    }
    public static boolean isGun(String str) {
        if ((!str.contains("略有磨损") && !str.contains("崭新出厂")
                && !str.contains("久经沙场") && !str.contains("破损不堪")
                && !str.contains("战痕累累")) || //有个印花的名字叫战痕累累
                (str.contains("★") || str.contains("印花") || !Tools.isChinese(str) || str.contains("伦琴射线")/*这把枪不是来自武器箱或者地图收藏品*/)
                || str.contains("诅咒")  //MAC-10 | 诅咒 这枪 有问题
                || str.contains("猎户") //USP猎户也有问题
                || str.contains("Chromatic") // 加利尔ar 迷人眼 不知道为啥 没翻译，排除掉
        )
        {
            return false;
        } else {
            return true;
        }
    }
    public static float[] getItemRange(Item item) {
        float[] arr = new float[2];
        range.forEach( (key,value) -> {
            if (item.getName().contains(key)) {
                JSONObject var1 = (JSONObject) value;
                var1.forEach((key1,value1) -> {
                    JSONObject var2 = (JSONObject) value1;
                    if (item.getName().contains(var2.getString("name_zh"))) {
                        arr[0] = Float.parseFloat(var2.getString("minla"));
                        arr[1] = Float.parseFloat(var2.getString("maxla"));
                    }
                });
            }
        });
        if (arr[1] == 0.0) {
            arr[0] = 0.0F;
            arr[1] = 1.0f;
        }
        return arr;
    }
    public static @NotNull List<Item> getItemList() {
        /*
        String[] arr = ResourceUtil.readStr("item.json", Charset.defaultCharset()).split("\n");
        List<Item> list = new ArrayList<>();
        for (String str : arr) {
            if (isGun(str))  {
                JSONObject object = JSONObject.parseObject(str);
                String name = object.getString("name");
                float price = object.getFloat("buff_reference_price");
                boolean isStatTrack = name.contains("StatTrak");
                int level = getLevel(name);
                if (level == 6){
                    continue;
                }
                Item var0 = new Item(name,price,isStatTrack,getWearAmount(name),getLevel(name),0,0,null,null);
                float[] var1 = getItemRange(var0);
                var0.chestName = getItemInChest(var0).name;
                List<String> higher = new ArrayList<>();
                getHigher(var0).forEach( var2 -> {
                    higher.add(var2.name);
                });
                var0.higherName = higher;
                var0.min = var1[0];
                var0.max = var1[1];
                list.add(var0);
            }
        }
        return list;

         */
        return gson.fromJson(ResourceUtil.readStr("items.json",Charset.defaultCharset()),new TypeToken<List<Item>>(){}.getType());
    }
}
