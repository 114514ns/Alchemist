package cn.pprocket.alchemist;

import cn.pprocket.alchemist.internal.Level;
import cn.pprocket.alchemist.internal.WearAmount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Item {
    public String name;
    public float price;
    public boolean isStatTrack;
    public WearAmount wearAmount;
    public int level;
    public float min;
    public float max;
    public String getName() {
        name = name.replace("（纪念品）","");
        name = name.replace("（StatTrak™）","");
        return name;
    }

}
