package cn.pprocket.alchemist;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
public class Chest {
    public String name;
    public List<Item> items;

}
