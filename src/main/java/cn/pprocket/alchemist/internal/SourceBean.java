package cn.pprocket.alchemist.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class SourceBean {

    private IdDTO _id;
    private Integer buff_id;
    private Integer updated_at;
    private Double weighted_ratio;
    private Integer igxe_id;
    private Integer c5_id;
    private Integer market_id;
    private String hash_name;
    private Double buff_reference_price;
    private Integer buff_buy_num;
    private Integer buff_sell_num;
    private String name;
    private Integer created_at;
    private Integer appid;
    private String game;
    private Integer count_in_24;
    private Double buff_optimal_price;
    private Double buff_safe_price;
    private Double igxe_optimal_price;
    private Double igxe_safe_price;
    private Integer c5_optimal_price;
    private Integer c5_safe_price;
    private Double optimal_buy_price;
    private Double safe_buy_price;
    private Double optimal_sell_price;
    private Double safe_sell_price;
    private Double buff_optimal_buy_ratio;
    private Double igxe_optimal_buy_ratio;
    private Double c5_optimal_buy_ratio;
    private Double buff_optimal_sell_ratio;
    private Double igxe_optimal_sell_ratio;
    private Double c5_optimal_sell_ratio;
    private Double buff_safe_buy_ratio;
    private Double igxe_safe_buy_ratio;
    private Double c5_safe_buy_ratio;
    private Double buff_safe_sell_ratio;
    private Double igxe_safe_sell_ratio;
    private Double c5_safe_sell_ratio;
    private Double effective_ratio;
    private Double effective_price;
    private Double uuyp_optimal_price;
    private Double uuyp_safe_price;
    private Double uuyp_optimal_buy_ratio;
    private Double uuyp_optimal_sell_ratio;
    private Double uuyp_safe_buy_ratio;
    private Double uuyp_safe_sell_ratio;
    private Integer uuyp_id;
    private List<List<Double>> buff_sell_list;
    private List<List<Double>> buy_order_list;
    private List<List<Double>> sell_order_list;
    private List<List<Double>> igxe_sell_list;
    private List<List<Integer>> c5_sell_list;
    private List<List<Double>> uuyp_sell_list;
}
