package com.delmon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.ProductSearchDTO;
import com.delmon.entity.Category;
import com.delmon.entity.Product;
import com.delmon.mapper.CategoryMapper;
import com.delmon.result.Result;
import com.delmon.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<Page<Object>> list(
            @RequestParam(name="pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(name="pageSize",defaultValue = "10") Integer pageSize) {
        return productService.list(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public Result<Object> detail(@PathVariable("id") Long id) {
        return productService.detail(id);
    }

    /**
     *  添加商品（不用传id和时间）
     * @param product
     * @return·
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Product product) {
        return productService.add(product);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Product product) {
        return productService.update(product);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable(name="id") Long id) {
        return productService.delete(id);
    }

    @GetMapping("/search")
    public Result<Page<Product>> search(
            @RequestParam(name="keyword", required = false) String keyword,
            @RequestParam(name="status", required = false) Integer status,
            @RequestParam(name="pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize) {
        ProductSearchDTO searchDTO = new ProductSearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setStatus(status);
        return productService.search(searchDTO, pageNum, pageSize);
    }

    /**
     * 批量导入商品（Excel文件）
     * @param file Excel文件
     */
    @PostMapping("/batch-import")
    public Result<Object> batchImport(@RequestParam("file") MultipartFile file) {
        return productService.batchImport(file);
    }

    /**
     * 获取所有分类列表
     */
    @GetMapping("/category/list")
    public Result<List<Category>> getCategoryList() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1);
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categoryList = categoryMapper.selectList(wrapper);
        return Result.success(categoryList);
    }
}
