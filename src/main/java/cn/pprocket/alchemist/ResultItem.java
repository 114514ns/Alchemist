package cn.pprocket.alchemist;

import cn.pprocket.alchemist.internal.WearAmount;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ResultItem {
    public Item originItem;
    public WearAmount amount;
    public float amountValue;
    public float rate;
}
