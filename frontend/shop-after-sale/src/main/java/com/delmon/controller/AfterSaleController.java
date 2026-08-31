package com.delmon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.result.Result;
import com.delmon.service.AfterSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/after-sale")
@RequiredArgsConstructor
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    @GetMapping("/list")
    public Result<Page<Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return afterSaleService.list(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public Result<Object> detail(@PathVariable Long id) {
        return afterSaleService.detail(id);
    }

    @PostMapping("/handle/{id}")
    public Result<Void> handle(
            @PathVariable("id") Long id,
            @RequestParam("action") Integer action,
            @RequestParam(value = "reason", required = false, defaultValue = "") String reason) {
        return afterSaleService.handle(id, action, reason);
    }
}
