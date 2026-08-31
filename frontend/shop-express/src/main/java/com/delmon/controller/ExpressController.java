package com.delmon.controller;

import com.delmon.result.Result;
import com.delmon.service.ExpressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/express")
@RequiredArgsConstructor
@Slf4j
public class ExpressController {

    private final ExpressService expressService;

    /**
     * 查询快递物流信息
     */
    @GetMapping("/query")
    public Result<Object> queryExpress(
            @RequestParam(name = "expressNo") String expressNo,
            @RequestParam(name = "expressCompany", required = false, defaultValue = "ZTO") String expressCompany) {
        log.info("查询快递: expressNo={}, expressCompany={}", expressNo, expressCompany);
        return expressService.queryExpress(expressNo, expressCompany);
    }

    /**
     * 根据订单ID查询快递信息
     */
    @GetMapping("/order/{orderId}")
    public Result<Object> queryByOrderId(@PathVariable(name = "orderId") Long orderId) {
        log.info("根据订单ID查询快递: orderId={}", orderId);
        return expressService.queryByOrderId(orderId);
    }

    /**
     * 更新订单快递信息
     */
    @PostMapping("/update")
    public Result<Object> updateExpressInfo(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String expressNo = params.get("expressNo").toString();
        String expressCompany = params.getOrDefault("expressCompany", "ZTO").toString();
        
        log.info("更新快递信息: orderId={}, expressNo={}, expressCompany={}", orderId, expressNo, expressCompany);
        return expressService.updateExpressInfo(orderId, expressNo, expressCompany);
    }

    /**
     * 获取所有支持的快递公司列表
     */
    @GetMapping("/companies")
    public Result<Object> getAllExpressCompanies() {
        return expressService.getAllExpressCompanies();
    }
}