package cn.pprocket.alchemist;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.pprocket.alchemist.internal.ChestResponseBean;
import cn.pprocket.alchemist.internal.ChestType;
import cn.pprocket.alchemist.internal.Result;
import cn.pprocket.alchemist.internal.WearAmount;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class Main {
    static Gson gson = null;
    public static void main(String[] args) throws URISyntaxException {
        gson = new Gson();
        List<Chest> chests = getChests();
        Tools.saveResult(gson.toJson(chests));
        System.out.println();
        for (int i = 0;i<1000000;i++) {

        }
    }

    public static boolean isGun(String str) {
        if ((!str.contains("略有磨损") && !str.contains("崭新出厂")
                && !str.contains("久经沙场") && !str.contains("破损不堪")
                && !str.contains("战痕累累")) ||
                str.contains("★")
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
                list.add(new Item(name,price,isStatTrack,getWearAmount(name)));
            }
        }
        return list;
    }
    public static Item parseItemInChest(JSONObject object) {
            String gunName = object.getString("localized_name");
            boolean isStatTrack = gunName.contains("Stat");
            float price = Float.parseFloat(object.getString("min_price"));
            Item item = new Item(gunName,price,isStatTrack,getWearAmount(gunName));
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
                var1.add(parseItemInChest(object));
            });
            Chest chest = new Chest(name,var1);
            chests.add(chest);
        });
        return chests;
    }

}

