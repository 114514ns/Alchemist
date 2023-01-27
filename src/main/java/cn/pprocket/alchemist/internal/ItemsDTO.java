package cn.pprocket.alchemist.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ItemsDTO {
    private String container_type;
    private String description;
    private String icon_url;
    private String name;
    private Boolean newX;
    private String value;
}
