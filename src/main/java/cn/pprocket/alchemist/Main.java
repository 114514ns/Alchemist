package cn.pprocket.alchemist;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.pprocket.alchemist.internal.*;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    private static final Log log = LogFactory.get();
    static Gson gson = null;
    static List<Chest> chests;
    static JSONObject range = null;
    public static void main(String[] args) {
        range = JSONObject.parseObject(ResourceUtil.readStr("range.json",Charset.defaultCharset()));
        gson = new Gson();
        //chests = getChests();
        List<Item> var0 = new ArrayList<>();

        chests = gson.fromJson(FileReader.create(new File("chest.json")).readString(),new TypeToken<List<Chest>>(){}.getType());
        //Tools.saveResult(gson.toJson(chests));
        List<Item> itemList = getItemList();
        for (int i = 0;i<10;i++) {
            Item var1 = RandomUtil.randomEle(itemList);
            while (getItemInChest(var1) == null) {
                var1 = RandomUtil.randomEle(itemList);
            }
            var0.add(var1);
        }
        compute(var0);
        System.out.println();
    }

    public static boolean isGun(String str) {
        if ((!str.contains("略有磨损") && !str.contains("崭新出厂")
                && !str.contains("久经沙场") && !str.contains("破损不堪")
                && !str.contains("战痕累累")) || //有个印花的名字叫战痕累累
                (str.contains("★") || str.contains("印花"))
        )
        {
            return false;
        } else {
            return true;
        }
    }
    public static WearAmount getWearAmount(@NotNull String name) {
        if (name.contains("崭新出厂")) {
            return WearAmount.FACTORY_NEW;
        } else if (name.contains("略有磨损")) {
            return WearAmount.MINIMAL_WEAR;
        } else if (name.contains("久经沙场")) {
            return WearAmount.FIELD_TESTED;
        } else if (name.contains("破损不堪")) {
            return WearAmount.WELL_WORN;
        } else if (name.contains("战痕累累")) {
            return WearAmount.BATTLE_SCARRED;
        } else {
            return WearAmount.ERROR;
        }
    }
    public static @NotNull List<Item> getItemList() {
        String[] arr = ResourceUtil.readStr("item.json", Charset.defaultCharset()).split("\n");
        List<Item> list = new ArrayList<>();
        for (String str : arr) {
            if (isGun(str)) {
                JSONObject object = JSONObject.parseObject(str);
                String name = object.getString("name");
                float price = object.getFloat("buff_reference_price");
                boolean isStatTrack = name.contains("StatTrak");
                list.add(new Item(name,price,isStatTrack,getWearAmount(name),getLevel(name),0,0));
            }
        }
        return list;
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
            Item item = new Item(gunName,price,isStatTrack,getWearAmount(gunName),level,min.get(),max.get());
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
                    var1.add(parseItemInChest(object));
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
                    var1.add(parseItemInChest(object));
            });
            Chest chest = new Chest(name,var1);
            chests.add(chest);
        });
        return chests;
    }
    public static int getLevel(String name) {
        for (int i = 0;i<chests.size();i++) {
            Chest c = chests.get(i);
            for (int j = 0;j<c.items.size();j++) {
                Item item = c.items.get(j);
                String v1 = name;
                String v2 = item.getName();
                if (v1.contains(v2) || v2.contains(v1)) {

                    return item.getLevel();
                }
            }
        }
        //System.out.println("getLevel 返回Unknown  名字  " + name);
        return Level.UNKNOWN;
    }
    public static Result compute(List<Item> items) {
        List<ResultItem> result = new ArrayList<>();
        Map<String,Integer> var1 = new TreeMap<>();
        List<Item> var0 = new ArrayList<>();
        for (Item item : items) {
            String name = "";
            try {
                name = getItemInChest(item).name;
            } catch (NullPointerException e) {
                log.error("eRROR ");
            }
            if (!var1.containsKey(name)) {
                var1.put(name,1);
                if (!var0.contains(item)) {
                    var0.add(item);
                }
            } else {
                int var2 = var1.get(name);
                if (!var0.contains(item)) {
                    var0.add(item);
                }
                var1.replace(name,var2+1);
            }
        }
        int count = var1.size();  //判断传进来的东西来自多少个收藏品
        if (count != 10) {
            return new Result();
        }
        var1.forEach( (key,value) -> {
            //getHigher()
        });
        items.forEach( var2 -> {

            var1.forEach((key,value) -> {
                String name = getItemInChest(var2).name;
                if (name.equals(key)) {
                    List<Item> higher = getHigher(var2);
                    int num = higher.size();
                    float rate = 1/num*value/10;
                    higher.forEach( var3 -> {
                        //result.add(new ResultItem(var3,rate));
                    });

                }
            });
        });

        return null;
    }
    public static List<Item> getHigher(Item item) {
        int level = 0;
        List<Item> result = new ArrayList<>();
        boolean found = false;
        Chest chest = getItemInChest(item);
        for (int k = 0;k<chest.items.size();k++) {
            Item var3 = chest.items.get(k);
            if (var3.getLevel() == level+1) {
                result.add(var3);
            }
        }
        return result;
    }
    public static Chest getItemInChest(Item item) {
        Chest chest = null;
        for (int i = 0;i<chests.size();i++) {
            Chest var1 = chests.get(i);
            for (int j = 0;j<var1.items.size();j++) {
                Item var3 = var1.items.get(j);
                String var4 = item.getName().replace(" ",""); // buff箱子返回的皮肤名字和iflow.work的数据不完全一致，可能多个空格少个空格
                String var5 = var3.name.replace(" ","");
                if (var4.contains(var5) || var5.contains(var4)) {
                    chest = var1;
                    break;
                }
            }
        }
        if (chest == null) {
            //log.error("Error : {}" + item);
        }
        return chest;
    }
    public static List<Item> getItemByLevel(Chest chest,int level) {
        List<Item> list = new ArrayList<>();
        chest.items.forEach( ele -> {
            if (getLevel(ele.getName()) == level) {
                list.add(ele);
            }
        });
        return list;
    }


}
@AllArgsConstructor
class Container {
    public float rate;
    public List<Item> list;
}

