package cn.pprocket.alchemist;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.pprocket.alchemist.internal.Level;
import cn.pprocket.alchemist.internal.WearAmount;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.pprocket.alchemist.GenChestList.getHigher;
import static cn.pprocket.alchemist.GenItemList.getItemList;

public class Main {
    private static final Log log = LogFactory.get();
    static Gson gson = new Gson();
    static List<Chest> chests;
    static JSONObject range = null;
    public static void init() {
        range = JSONObject.parseObject(ResourceUtil.readStr("range.json",Charset.defaultCharset()));
        chests = gson.fromJson(FileReader.create(new File("chest.json")).readString(),new TypeToken<List<Chest>>(){}.getType());

    }
    public static void main(String[] args) {


        //chests = getChests();
        Main.init();
        //Tools.saveResult(gson.toJson(chests));
        List<Item> itemList = getItemList();
        //Tools.writeFile(gson.toJson(itemList),"items.json");
        //System.exit(0);
        int count = 5000;
        long start = System.currentTimeMillis();
        ExecutorService service = Executors.newFixedThreadPool(12);
        for (int j = 0;j<count;j++) {
            service.execute( () -> {
                List<Item> var0 = new ArrayList<>();
                for (int i = 0;i<10;i++) {
                    Item var1 = RandomUtil.randomEle(itemList);
                    while (getItemInChest(var1) == null || var1.getLevel() == 6 || getHigher(var1).size() == 0 || var1.getLevel() == 5) {
                        var1 = RandomUtil.randomEle(itemList);
                    }
                    var0.add(var1);
                }
                compute(var0);
                var0.clear();
            });

        }
        service.shutdown();
        while (!service.isTerminated()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println((System.currentTimeMillis()-start)/count);
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
    public static WearAmount getWearAmount(float amount) {
        if (amount<=0.06) {
            return WearAmount.FACTORY_NEW;
        } else if (amount<=0.14) {
            return WearAmount.MINIMAL_WEAR;
        } else if (amount<=0.37) {
            return WearAmount.FIELD_TESTED;
        } else if (amount<=0.45) {
            return WearAmount.WELL_WORN;
        } else  {
            return WearAmount.BATTLE_SCARRED;
        }
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
    public static List<ResultItem> compute(List<Item> items) {
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
        float averageAmount = getAverageAmount(items);

        items.forEach( var2 -> {
            var1.forEach((key,value) -> {
                String name = getItemInChest(var2).name;
                if (name.equals(key)) {
                    List<Item> higher = getHigher(var2);
                    int num = higher.size();
                    float rate = 1/num*value/10;
                    higher.forEach( var3 -> {
                        float amount = (var3.max-var3.min)*averageAmount+var3.min;
                        result.add(new ResultItem(var3,getWearAmount(amount),amount,rate));
                    });

                }
            });
        });
        if (var1.size() == 2) {
            System.currentTimeMillis();
        }

        return result;
    }

    /**
     *
     * @param item 物品
     * @return 获取这个物品所属的磨损等级的最小磨损
     */

    public static float getMinAmount(Item item) {
        String name = item.getName();
        if (item.name.contains("崭新出厂")) {
            return 0.01F;
        } else if (name.contains("略有磨损")) {
            return 0.07F;
        } else if (name.contains("久经沙场")) {
            return 0.15F;
        } else if (name.contains("破损不堪")) {
            return 0.38F;
        } else if (name.contains("战痕累累")) {
            return 0.45f;
        } else {
            return 1.14514F;
        }
    }

    /**
     *
     * @param items 物品集合
     * @return 获取List里面的物品平均磨损
     */
    public static float getAverageAmount(List<Item> items) {
        float total = 0;
        for (int i = 0;i<items.size();i++) {
            Item item = items.get(i);
            total+=getMinAmount(item);
        }
        return total/10;
    }
    public static Chest getItemInChestForGen(Item item) {
        for (int i = 0; i < chests.size(); i++) {
            Chest chest = chests.get(i);
            for (int j = 0; j < chest.items.size(); j++) {
                Item var0 = chest.items.get(j);
                if (item.getName().contains(var0.getName())) {
                    return chest;
                }
            }
        }
        return null;
    }
    public static Chest getItemInChest(Item item) {
        for (int i = 0;i<chests.size();i++) {
            Chest chest = chests.get(i);
            if (chest.name.equals(item.chestName)) {
                return chest;
            }
        }
        return null;
    }


}
@AllArgsConstructor
class Container {
    public float rate;
    public List<Item> list;
}

