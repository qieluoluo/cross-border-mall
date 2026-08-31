package com.delmon.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.ProductSearchDTO;
import com.delmon.entity.Product;
import com.delmon.enums.ResultCode;
import com.delmon.mapper.ProductMapper;
import com.delmon.result.Result;
import com.delmon.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper productMapper;
    
    @Override
    public Result<Page<Object>> list(Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.metadata.IPage<java.util.Map<String, Object>> productPage =
                productMapper.selectProductPageWithCategory(page);
        Page<Object> resultPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        resultPage.setRecords(productPage.getRecords().stream().map(item -> (Object) item).toList());
        return Result.success(resultPage);
    }
    
    @Override
    public Result<Object> detail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "商品不存在");
        }
        // 检查商品是否上架
        if (product.getStatus() == 0) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "商品已下架");
        }
        return Result.success(product);
    }
    
    @Override
    public Result<Void> add(Product product) {
        productMapper.insert(product);
        return Result.success();
    }
    
    @Override
    public Result<Void> update(Product product) {
        Product existProduct = productMapper.selectById(product.getId());
        if (existProduct == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "商品不存在");
        }
        productMapper.updateById(product);
        return Result.success();
    }
    
    @Override
    public Result<Void> delete(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(ResultCode.DATA_NOT_EXIST, "商品不存在");
        }
        productMapper.deleteById(id);
        return Result.success();
    }

    @Override
    public Result<Page<Product>> search(ProductSearchDTO searchDTO, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        
        String keyword = searchDTO.getKeyword();
        Integer status = searchDTO.getStatus();
        
        // 如果没有指定状态，默认只显示上架的商品
        if (status == null) {
            status = 1;
        }
        
        log.info("搜索商品, keyword={}, status={}", keyword, status);
        
        com.baomidou.mybatisplus.core.metadata.IPage<Product> searchResult = 
                productMapper.searchByKeyword(page, keyword, status);
        
        Page<Product> resultPage = new Page<>(searchResult.getCurrent(), searchResult.getSize(), searchResult.getTotal());
        resultPage.setRecords(searchResult.getRecords());
        
        return Result.success(resultPage);
    }

    @Override
    public Result<Object> batchImport(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error(ResultCode.BAD_REQUEST, "请选择要上传的文件");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return Result.error(ResultCode.BAD_REQUEST, "只支持Excel文件(.xlsx或.xls格式)");
        }
        
        List<Product> products = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = fileName.endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            
            // 从第二行开始读取（第一行是表头）
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Product product = parseRowToProduct(row, i + 1);
                    products.add(product);
                } catch (Exception e) {
                    failedCount++;
                    Map<String, Object> error = new HashMap<>();
                    error.put("row", i + 1);
                    error.put("message", e.getMessage());
                    errors.add(error);
                }
            }
            
            // 批量插入成功的数据
            for (Product product : products) {
                try {
                    productMapper.insert(product);
                    successCount++;
                } catch (Exception e) {
                    failedCount++;
                    Map<String, Object> error = new HashMap<>();
                    error.put("row", "数据行");
                    error.put("message", "插入数据库失败: " + e.getMessage());
                    errors.add(error);
                }
            }
            
        } catch (Exception e) {
            log.error("解析Excel文件失败", e);
            return Result.error(ResultCode.BAD_REQUEST, "解析Excel文件失败: " + e.getMessage());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        result.put("errors", errors);
        
        return Result.success(result);
    }
    
    private Product parseRowToProduct(Row row, int rowNum) {
        Product product = new Product();
        
        // 商品名称（必填）
        Cell nameCell = row.getCell(0);
        if (nameCell == null || getCellStringValue(nameCell).trim().isEmpty()) {
            throw new RuntimeException("商品名称不能为空");
        }
        product.setName(getCellStringValue(nameCell).trim());
        
        // 分类ID（必填）
        Cell categoryIdCell = row.getCell(1);
        if (categoryIdCell == null) {
            throw new RuntimeException("分类ID不能为空");
        }
        try {
            long categoryId = (long) getCellNumericValue(categoryIdCell);
            if (categoryId <= 0) {
                throw new RuntimeException("分类ID必须大于0");
            }
            product.setCategoryId(categoryId);
        } catch (Exception e) {
            throw new RuntimeException("分类ID格式不正确");
        }
        
        // 价格（必填）
        Cell priceCell = row.getCell(2);
        if (priceCell == null) {
            throw new RuntimeException("价格不能为空");
        }
        try {
            double price = getCellNumericValue(priceCell);
            if (price <= 0) {
                throw new RuntimeException("价格必须大于0");
            }
            product.setPrice(BigDecimal.valueOf(price));
        } catch (Exception e) {
            throw new RuntimeException("价格格式不正确");
        }
        
        // 库存（必填）
        Cell stockCell = row.getCell(3);
        if (stockCell == null) {
            throw new RuntimeException("库存不能为空");
        }
        try {
            int stock = (int) getCellNumericValue(stockCell);
            if (stock < 0) {
                throw new RuntimeException("库存不能为负数");
            }
            product.setStock(stock);
        } catch (Exception e) {
            throw new RuntimeException("库存格式不正确");
        }
        
        // 销量（可选，默认为0）
        Cell salesCell = row.getCell(4);
        if (salesCell != null) {
            try {
                int sales = (int) getCellNumericValue(salesCell);
                if (sales < 0) sales = 0;
                product.setSales(sales);
            } catch (Exception e) {
                product.setSales(0);
            }
        } else {
            product.setSales(0);
        }
        
        // 副标题（可选）
        Cell subTitleCell = row.getCell(5);
        if (subTitleCell != null) {
            product.setSubTitle(getCellStringValue(subTitleCell).trim());
        }
        
        // 主图（可选）
        Cell mainImageCell = row.getCell(6);
        if (mainImageCell != null) {
            product.setMainImage(getCellStringValue(mainImageCell).trim());
        }
        
        // 状态（可选，默认为1上架）
        Cell statusCell = row.getCell(7);
        if (statusCell != null) {
            try {
                int status = (int) getCellNumericValue(statusCell);
                product.setStatus(status == 0 ? 0 : 1);
            } catch (Exception e) {
                product.setStatus(1);
            }
        } else {
            product.setStatus(1);
        }
        
        return product;
    }
    
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
    
    private double getCellNumericValue(Cell cell) {
        if (cell == null) return 0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("数字格式不正确");
                }
            default:
                throw new RuntimeException("数字格式不正确");
        }
    }
}
