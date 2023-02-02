package cn.pprocket.alchemist;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.pprocket.alchemist.internal.ConfigBean;
import cn.pprocket.alchemist.internal.Level;
import cn.pprocket.alchemist.internal.Result;
import cn.pprocket.alchemist.internal.WearAmount;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jdi.request.BreakpointRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static cn.pprocket.alchemist.GenChestList.getHigher;
import static cn.pprocket.alchemist.GenItemList.getItemList;


public class Main {
    private static final Log log = LogFactory.get();
    static Gson gson = new Gson();
    static List<Chest> chests;
    static JSONObject range = null;
    static float mostSpend;
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
        long start = System.currentTimeMillis();
        ConfigBean bean = checkConfig();
        int count = bean.getCount()/5;
        mostSpend = bean.getPrice();
        List<Result> results = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(12);
        for (int l = 0;l<=4;l++) {
            for (int j = 0;j<count;j++) {

                int finalL = l;
                service.execute( () -> {
                    List<Item> var0 = new ArrayList<>();
                    for (int i = 0;i<10;i++) {
                        Item var1 = RandomUtil.randomEle(itemList);
                    /*
                    while (getItemInChest(var1) == null || var1.getLevel() == 6 || getHigher(var1).size() == 0 || var1.getLevel() == 5 || var1.isStatTrack  ) {
                        var1 = RandomUtil.randomEle(itemList);
                    }

                     */
                        if (var1.getLevel() == finalL && var1.higherName.size() != 0 && var1.isStatTrack()== bean.isStatTrack()) {
                            var0.add(var1);
                        } else {
                            i--;
                        }
                    }
                    Result compute = compute(var0);
                    if (compute.spend <= bean.getPrice() && compute.earnRate >= bean.getRate()) {
                        results.add(compute);
                    }
                    var0.clear();
                });

            }
        }
        service.shutdown();
        while (!service.isTerminated()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        writeResult(results);

        System.out.println((System.currentTimeMillis()-start)*1.0f/count);
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
    public static void writeResult(List<Result> list) {
        File file = new File("result.json");
        FileWriter writer = FileWriter.create(file);
        writer.write("",false); //清空
        writer.write(gson.toJson(list));
    }
    @SneakyThrows
    public static ConfigBean checkConfig() {
        File file = new File("config.json");
        if (!file.exists()) {
            file.createNewFile();
            FileWriter writer = FileWriter.create(file);
            ConfigBean bean = new ConfigBean(500,114514,100,false,0.6f);
            writer.write(gson.toJson(bean,ConfigBean.class),false);
            return bean;
        } else {
            FileReader reader = new FileReader(file);
            return gson.fromJson(reader.readString(), ConfigBean.class);
        }
    }
    public static boolean checkLevel(List<Item> item) {
        int first = item.get(0).getLevel();
        boolean result = true;
        for (int i =0;i< item.size();i++) {
            if (item.get(i).level != first) {
                result = false;
            }
        }
        return result;
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
    public static Result compute(List<Item> items) {
        float spend = getAllPrice(items);
        List<ResultItem> result = new ArrayList<>();
        Map<String,Integer> var1 = new TreeMap<>();
        List<Item> var0 = new ArrayList<>();
        Result result1 = new Result();
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
        AtomicReference<Float> earn = new AtomicReference<>((float) 0);
        AtomicReference<Float> earnRate = new AtomicReference<>((float) 0);
        List<Item> alreadyAddHigher = new ArrayList<>();
        List<ResultItem> var4 = new ArrayList<>();
        items.forEach( var2 -> {
            var1.forEach((key,value) -> {
                String name = getItemInChest(var2).name;
                if (name.equals(key)) {
                    List<Item> higher = getHigher(var2);
                    int num = higher.size();
                    float rate = (float) (1.0/num*value/10);
                    for (int i = 0;i< higher.size();i++) {
                        Item var3 = higher.get(i);
                        if (alreadyAddHigher.contains(var3)) {
                            break;
                        } else {
                            alreadyAddHigher.add(var3);
                        }
                        float amount = (var3.max - var3.min) * averageAmount + var3.min;
                        if (var3.getPrice() >= spend) {
                            earnRate.updateAndGet(v -> new Float((float) (v + rate)));
                        }
                        var4.add(new ResultItem(var3, getWearAmount(amount), amount, rate));
                    }
                }
                result1.setSpend(spend);
                result1.setInput(items);
                result1.setList(var4);
                result1.setEarnRate(earnRate.get());
                System.currentTimeMillis();
            });
        });
        return result1;
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
    public static float getAllPrice(List<Item> items) {
        float num = 0;
        for (Item item : items) {
            num+=item.getPrice();
        }
        return num;
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

