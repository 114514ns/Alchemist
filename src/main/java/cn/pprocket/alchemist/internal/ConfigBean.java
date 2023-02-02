package cn.pprocket.alchemist.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigBean {
    public float price; //最高成本
    public int count;  //计算次数
    public int mostResult; //最多结果条数
    public boolean isStatTrack; //是否包括暗金
    public float rate;
}
