package com.delmon.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UpdateCartDTO implements Serializable {
    private Long id;
    private Integer quantity;
}