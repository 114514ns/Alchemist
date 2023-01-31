package cn.pprocket.alchemist;

import cn.pprocket.alchemist.internal.Level;
import cn.pprocket.alchemist.internal.WearAmount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString

public class Item {
    private static List<Item> itemList = GenItemList.getItemList();
    public String name;
    public float price;
    public boolean isStatTrack;
    public WearAmount wearAmount;
    public int level;
    public float min;
    public float max;
    public String chestName;
    public List<String> higherName;
    public String getName() {
        name = name.replace("（纪念品）","");
        name = name.replace("（StatTrak™）","");
        return name;
    }
    public static Item forName(String name) {
        for (int i =0;i<itemList.size();i++) {
            Item var0 = itemList.get(i);
            if (name.contains(var0.getName())) {
                return var0;
            }
        }
        return null;
    }


}
