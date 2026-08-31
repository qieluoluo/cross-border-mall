package com.delmon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.result.Result;

public interface AfterSaleService {
    Result<Page<Object>> list(Integer pageNum, Integer pageSize);
    Result<Object> detail(Long id);
    Result<Void> handle(Long id, Integer action, String reason);
}
