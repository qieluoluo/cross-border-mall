package com.delmon.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.entity.AfterSale;
import com.delmon.mapper.AfterSaleMapper;
import com.delmon.result.Result;
import com.delmon.service.AfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AfterSaleServiceImpl implements AfterSaleService {

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    @Override
    public Result<Page<Object>> list(Integer pageNum, Integer pageSize) {
        Page<AfterSale> page = new Page<>(pageNum, pageSize);
        // 按申请时间倒序
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AfterSale> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(AfterSale::getApplyTime);
        
        Page<AfterSale> afterSalePage = afterSaleMapper.selectPage(page, queryWrapper);

        Page<Object> resultPage = new Page<>();
        resultPage.setCurrent(afterSalePage.getCurrent());
        resultPage.setSize(afterSalePage.getSize());
        resultPage.setTotal(afterSalePage.getTotal());
        resultPage.setPages(afterSalePage.getPages());
        resultPage.setRecords(afterSalePage.getRecords().stream()
            .map(afterSale -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                String userName = afterSaleMapper.selectUserNameByUserId(afterSale.getUserId());
                map.put("id", afterSale.getId());
                map.put("orderNo", afterSale.getOrderNo());
                map.put("userName", (userName == null || userName.isBlank()) ? "-" : userName);
                map.put("reason", afterSale.getReason());
                map.put("type", afterSale.getType() == 1 ? "仅退款" : "退货退款");
                
                // 状态转换
                String statusText = switch (afterSale.getStatus()) {
                    case 0 -> "待处理";
                    case 1 -> "处理中";
                    case 2 -> "已完成";
                    case 3 -> "已拒绝";
                    default -> "未知";
                };
                map.put("status", statusText);
                map.put("createTime", afterSale.getApplyTime());
                return (Object) map;
            })
            .collect(java.util.stream.Collectors.toList()));

        return Result.success(resultPage);
    }

    @Override
    public Result<Object> detail(Long id) {
        AfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null) {
            return Result.error(com.delmon.enums.ResultCode.DATA_NOT_EXIST, "售后单不存在");
        }

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        String userName = afterSaleMapper.selectUserNameByUserId(afterSale.getUserId());
        map.put("id", afterSale.getId());
        map.put("afterSaleNo", afterSale.getAfterSaleNo());
        map.put("orderNo", afterSale.getOrderNo());
        map.put("userName", (userName == null || userName.isBlank()) ? "-" : userName);
        map.put("type", afterSale.getType() == 1 ? "仅退款" : "退货退款");
        map.put("reason", afterSale.getReason());
        map.put("description", afterSale.getDescription());
        map.put("refundAmount", afterSale.getRefundAmount());
        
        String statusText = switch (afterSale.getStatus()) {
            case 0 -> "待处理";
            case 1 -> "处理中";
            case 2 -> "已完成";
            case 3 -> "已拒绝";
            default -> "未知";
        };
        map.put("status", statusText);
        map.put("applyTime", afterSale.getApplyTime());
        map.put("handleTime", afterSale.getHandleTime());
        map.put("completeTime", afterSale.getCompleteTime());

        return Result.success(map);
    }

    @Override
    public Result<Void> handle(Long id, Integer action, String reason) {
        AfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null) {
            return Result.error(com.delmon.enums.ResultCode.DATA_NOT_EXIST, "售后单不存在");
        }

        if (afterSale.getStatus() != 0) {
            return Result.error(com.delmon.enums.ResultCode.BUSINESS_ERROR, "该售后单已处理");
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        if (action == 1) {
            // 同意售后
            afterSale.setStatus(2); // 已完成
            afterSale.setCompleteTime(now);
        } else if (action == 2) {
            // 拒绝售后
            afterSale.setStatus(3); // 已拒绝
            afterSale.setRejectReason(reason != null ? reason : "不符合售后条件");
        } else {
            return Result.error(com.delmon.enums.ResultCode.BAD_REQUEST, "无效的操作类型");
        }
        
        afterSale.setHandleTime(now);
        afterSaleMapper.updateById(afterSale);
        
        return Result.success();
    }
}
