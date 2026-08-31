package com.delmon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.ProductSearchDTO;
import com.delmon.entity.Product;
import com.delmon.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Result<Page<Object>> list(Integer pageNum, Integer pageSize);
    Result<Object> detail(Long id);
    Result<Void> add(Product product);
    Result<Void> update(Product product);
    Result<Void> delete(Long id);
    Result<Page<Product>> search(ProductSearchDTO searchDTO, Integer pageNum, Integer pageSize);
    /**
     * 批量导入商品（Excel文件）
     * @param file Excel文件
     * @return 导入结果
     */
    Result<Object> batchImport(MultipartFile file);
}
