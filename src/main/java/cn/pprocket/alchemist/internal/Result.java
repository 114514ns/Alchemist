package cn.pprocket.alchemist.internal;

import cn.pprocket.alchemist.Item;
import cn.pprocket.alchemist.ResultItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Result {
    public List<ResultItem> list;
    public float spend;
    public List<Item> input;
    public float earnRate;
}
