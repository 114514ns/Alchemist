package cn.pprocket.alchemist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    public String name;
    public float price;
    public boolean isStatTrack;
    public WearAmount wearAmount;

}
