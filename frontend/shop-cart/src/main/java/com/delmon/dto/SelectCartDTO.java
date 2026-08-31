package com.delmon.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SelectCartDTO implements Serializable {
    private Long id;
    private Integer selected;
}