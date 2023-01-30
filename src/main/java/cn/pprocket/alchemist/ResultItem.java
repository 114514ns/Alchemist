package cn.pprocket.alchemist;

import cn.pprocket.alchemist.internal.WearAmount;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResultItem {
    public Item originItem;
    public WearAmount amount;
    public float amountValue;
    public float rate;
}
