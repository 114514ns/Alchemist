package cn.pprocket.alchemist;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.pprocket.alchemist.internal.ChestResponseBean;
import cn.pprocket.alchemist.internal.ChestType;
import cn.pprocket.alchemist.internal.Level;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static cn.pprocket.alchemist.Main.getItemInChestForGen;
import static cn.pprocket.alchemist.Main.getWearAmount;

public class GenChestList {
    static List<Chest> chests;
    static JSONObject range = null;
    static Gson gson = new Gson();

    public static void main(String[] args) {
        range = JSONObject.parseObject(ResourceUtil.readStr("range.json", Charset.defaultCharset()));
        new FileWriter(new File("chests.json")).write(gson.toJson(getChests()),false);
    }
    public static Item parseItemInChest(JSONObject object) {
        String gunName = object.getString("localized_name");
        boolean isStatTrack = gunName.contains("Stat");
        float price = Float.parseFloat(object.getString("min_price"));
        String levelText = object.getJSONObject("goods").getJSONObject("tags").getJSONObject("rarity").getString("localized_name");
        int level;
        if (levelText.contains("隐秘")) {
            level = Level.COVERT;
        } else if (levelText.contains("保密")) {
            level = Level.CLASSIFIED;
        } else if (levelText.contains("受限")) {
            level = Level.RESTRICTED;
        } else if (levelText.contains("军规级")) {
            level = Level.MIL_SPEC;
        } else if (levelText.contains("工业级")) {
            level = Level.INDUSTRIAL;
        } else if (levelText.contains("消费级")) {
            level = Level.CONSUMER;
        } else {
            level = Level.UNKNOWN;
        }
        final AtomicReference<Float> min = new AtomicReference<>((float) 0);
        final AtomicReference<Float> max = new AtomicReference<>((float) 0);
        range.forEach( (key,value) -> {
            if (gunName.contains(key)) {
                JSONObject var1 = (JSONObject) value;
                var1.forEach((key1,value1) -> {
                    JSONObject var2 = (JSONObject) value1;
                    if (gunName.contains(var2.getString("name_zh"))) {
                        min.set(Float.parseFloat(var2.getString("minla")));
                        max.set(Float.parseFloat(var2.getString("maxla")));
                    }
                });
            }
        });
        if (max.get() ==0) {
            //log.error("Error  Name   " + gunName); //range.json中中文名翻译不完全，现在大概有30多件皮肤有问题
            //暂时把有问题的皮肤磨损最高默认为1，最低为0
            min.set((float) 0);
            max.set((float) 1);
        }
        Item item = new Item(gunName,price,isStatTrack,getWearAmount(gunName),level,min.get(),max.get(),null,null);
        List<String> higher = new ArrayList<>();
        getHigher(item).forEach( var0 -> {
            higher.add(var0.name);
        });
        return item;
    }
    public static List<Chest> getChests() {
        List<Chest> chests = new ArrayList<>();
        String s = Tools.sendGet("https://buff.163.com/api/market/csgo_container_list?type=weapon_cases&page_num=1&page_size=80");
        ChestResponseBean bean = gson.fromJson(s, ChestResponseBean.class);
        bean.getData().getItems().forEach( ele -> {
            String code = ele.getValue();
            String name = ele.getName();
            ChestType type = ChestType.getType(ele.getContainer_type());
            String res = Tools.sendGet(new StringUtil().getChestUrl(code, type));
            List<Item> var1 = new ArrayList<>();
            JSONObject.parseObject(res).getJSONObject("data").getJSONArray("items").forEach(arrEle -> {
                JSONObject object = (JSONObject) arrEle;
                Item item = parseItemInChest(object);
                var1.add(item);
            });
            Chest chest = new Chest(name,var1);
            chests.add(chest);
        });
        String s1 = Tools.sendGet("https://buff.163.com/api/market/csgo_container_list?type=map_collections&page_num=1&page_size=80");
        ChestResponseBean bean1 = gson.fromJson(s1, ChestResponseBean.class);
        bean1.getData().getItems().forEach( ele -> {
            String code = ele.getValue();
            String name = ele.getName();
            ChestType type = ChestType.getType(ele.getContainer_type());
            List<Item> var1 = new ArrayList<>();
            String res = Tools.sendGet(new StringUtil().getChestUrl(code, type));
            JSONObject.parseObject(res).getJSONObject("data").getJSONArray("items").forEach(arrEle -> {
                JSONObject object = (JSONObject) arrEle;
                Item item = parseItemInChest(object);
                var1.add(item);
            });
            Chest chest = new Chest(name,var1);
            chests.add(chest);
        });
        return chests;
    }
    public static List<Item> getHigher(Item item) {
        int level = item.level;
        List<Item> result = new ArrayList<>();
        boolean found = false;
        Chest chest = getItemInChestForGen(item);
        for (int k = 0;k<chest.items.size();k++) {
            Item var3 = chest.items.get(k);
            if (var3.getLevel() == level+1) {
                result.add(var3);
            }
        }
        return result;
    }
}
