package cn.pprocket.alchemist.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChestResponseBean {

    private String code;
    private DataDTO data;
    private Object msg;
}
