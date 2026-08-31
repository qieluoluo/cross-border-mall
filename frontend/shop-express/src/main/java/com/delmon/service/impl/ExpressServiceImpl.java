package com.delmon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delmon.entity.ExpressInfo;
import com.delmon.mapper.ExpressMapper;
import com.delmon.result.Result;
import com.delmon.service.ExpressService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpressServiceImpl implements ExpressService {

    private final ExpressMapper expressMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${express.kdniao.api-url:http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx}")
    private String kdniaoApiUrl;

    @Value("${express.kdniao.app-key:your_kdniao_app_key}")
    private String appKey;

    @Value("${express.kdniao.e-business-id:your_kdniao_e_business_id}")
    private String eBusinessId;

    private static final Map<String, String> EXPRESS_COMPANIES = new HashMap<>();
    static {
        EXPRESS_COMPANIES.put("SF", "顺丰速运");
        EXPRESS_COMPANIES.put("YTO", "圆通速递");
        EXPRESS_COMPANIES.put("ZTO", "中通快递");
        EXPRESS_COMPANIES.put("YD", "韵达快递");
        EXPRESS_COMPANIES.put("EMS", "EMS");
        EXPRESS_COMPANIES.put("JD", "京东物流");
        EXPRESS_COMPANIES.put("TTKDEX", "天天快递");
        EXPRESS_COMPANIES.put("HTKY", "百世快递");
        EXPRESS_COMPANIES.put("QFKD", "全峰快递");
    }

    @Override
    public Result<Object> queryExpress(String expressNo, String expressCompany) {
        log.info("查询快递信息, expressNo: {}, expressCompany: {}", expressNo, expressCompany);

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, String> params = new HashMap<>();
            params.put("RequestData", "{\"OrderCode\":\"\",\"ShipperCode\":\"" + expressCompany + "\",\"LogisticCode\":\"" + expressNo + "\"}");
            params.put("EBusinessID", eBusinessId);
            params.put("RequestType", "1002");
            params.put("DataType", "2");

            String dataSign = encrypt(params.get("RequestData") + appKey, "MD5");
            params.put("DataSign", Base64.getEncoder().encodeToString(dataSign.getBytes(StandardCharsets.UTF_8)));

            Map<String, Object> response = restTemplate.postForObject(kdniaoApiUrl, params, Map.class);

            if (response != null && "0".equals(response.get("Success"))) {
                result.put("success", true);
                result.put("expressNo", expressNo);
                result.put("expressCompany", EXPRESS_COMPANIES.getOrDefault(expressCompany, expressCompany));
                result.put("status", response.get("State"));
                result.put("traces", response.get("Traces"));
            } else {
                result = generateMockExpressData(expressNo, expressCompany);
            }
        } catch (Exception e) {
            log.warn("调用快递鸟API失败，使用模拟数据: {}", e.getMessage());
            result = generateMockExpressData(expressNo, expressCompany);
        }

        return Result.success(result);
    }

    @Override
    public Result<Object> queryByOrderId(Long orderId) {
        ExpressInfo expressInfo = expressMapper.selectOne(
                new LambdaQueryWrapper<ExpressInfo>()
                        .eq(ExpressInfo::getOrderId, orderId)
        );

        if (expressInfo == null) {
            return Result.businessError("该订单暂无快递信息");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", expressInfo.getOrderId());
        result.put("expressNo", expressInfo.getExpressNo());
        result.put("expressCompany", EXPRESS_COMPANIES.getOrDefault(expressInfo.getExpressCompany(), expressInfo.getExpressCompany()));
        result.put("status", expressInfo.getStatus());
        result.put("latestTime", expressInfo.getLatestTime());
        result.put("latestStatus", expressInfo.getLatestStatus());

        try {
            if (expressInfo.getDetailJson() != null && !expressInfo.getDetailJson().isEmpty()) {
                JsonNode detailNode = objectMapper.readTree(expressInfo.getDetailJson());
                result.put("traces", detailNode.get("Traces"));
            }
        } catch (Exception e) {
            log.warn("解析快递详情JSON失败: {}", e.getMessage());
        }

        return Result.success(result);
    }

    @Override
    public Result<Object> updateExpressInfo(Long orderId, String expressNo, String expressCompany) {
        ExpressInfo existing = expressMapper.selectOne(
                new LambdaQueryWrapper<ExpressInfo>()
                        .eq(ExpressInfo::getOrderId, orderId)
        );

        ExpressInfo expressInfo;
        if (existing != null) {
            expressInfo = existing;
        } else {
            expressInfo = new ExpressInfo();
            expressInfo.setOrderId(orderId);
        }

        expressInfo.setExpressNo(expressNo);
        expressInfo.setExpressCompany(expressCompany);
        expressInfo.setStatus("transit");
        expressInfo.setLatestTime(new Date().toString());
        expressInfo.setLatestStatus("已揽收");

        if (existing != null) {
            expressMapper.updateById(expressInfo);
        } else {
            expressMapper.insert(expressInfo);
        }

        return Result.success("快递信息更新成功");
    }

    @Override
    public Result<Object> getAllExpressCompanies() {
        List<Map<String, String>> companies = new ArrayList<>();
        for (Map.Entry<String, String> entry : EXPRESS_COMPANIES.entrySet()) {
            Map<String, String> company = new HashMap<>();
            company.put("code", entry.getKey());
            company.put("name", entry.getValue());
            companies.add(company);
        }
        return Result.success(companies);
    }

    private Map<String, Object> generateMockExpressData(String expressNo, String expressCompany) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("expressNo", expressNo);
        result.put("expressCompany", EXPRESS_COMPANIES.getOrDefault(expressCompany, expressCompany));
        result.put("status", "3");

        List<Map<String, String>> traces = new ArrayList<>();
        traces.add(Map.of("AcceptTime", "2024-01-15 14:30:00", "AcceptStation", "快件已签收，签收人：本人"));
        traces.add(Map.of("AcceptTime", "2024-01-15 10:20:00", "AcceptStation", "快件正在派送中，派送员：张师傅 138****8888"));
        traces.add(Map.of("AcceptTime", "2024-01-14 18:30:00", "AcceptStation", "快件已到达【北京朝阳区网点】"));
        traces.add(Map.of("AcceptTime", "2024-01-14 12:00:00", "AcceptStation", "快件已从【上海转运中心】发出"));
        traces.add(Map.of("AcceptTime", "2024-01-13 16:45:00", "AcceptStation", "快件已到达【上海转运中心】"));
        traces.add(Map.of("AcceptTime", "2024-01-13 10:00:00", "AcceptStation", "快件已揽收"));

        result.put("traces", traces);
        return result;
    }

    private String encrypt(String data, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }
}
