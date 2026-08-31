package com.delmon.service;

import com.delmon.result.Result;

import java.util.Map;

public interface ExpressService {
    Result<Object> queryExpress(String expressNo, String expressCompany);
    
    Result<Object> queryByOrderId(Long orderId);
    
    Result<Object> updateExpressInfo(Long orderId, String expressNo, String expressCompany);
    
    Result<Object> getAllExpressCompanies();
}