package cn.pprocket.alchemist.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DataDTO {
    private Integer page_num;
    private Integer page_size;
    private Integer total_count;
    private Integer total_page;
    private List<ItemsDTO> items;
}
