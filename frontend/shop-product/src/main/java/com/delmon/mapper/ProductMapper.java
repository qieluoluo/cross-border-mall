package com.delmon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Select("""
            SELECT
              p.*,
              c.name AS category
            FROM product p
            LEFT JOIN category c ON c.id = p.category_id
            WHERE p.status = 1
            ORDER BY p.id DESC
            """)
    IPage<java.util.Map<String, Object>> selectProductPageWithCategory(Page<Product> page);

    @Select("""
            <script>
            SELECT * FROM product
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (name LIKE CONCAT('%', #{keyword}, '%') OR sub_title LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            ORDER BY id DESC
            </script>
            """)
    IPage<Product> searchByKeyword(Page<Product> page, @Param("keyword") String keyword, @Param("status") Integer status);
}
