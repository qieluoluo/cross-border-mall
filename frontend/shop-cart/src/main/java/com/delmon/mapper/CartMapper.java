package com.delmon.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delmon.dto.CartListDTO;
import com.delmon.entity.Cart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    @Select("""
            <script>
            SELECT
                c.id,
                c.user_id AS userId,
                c.product_id AS productId,
                c.sku_id AS skuId,
                c.quantity,
                COALESCE(NULLIF(u.nickname, ''), u.username, CONCAT('用户', c.user_id)) AS userName,
                COALESCE(p.name, CONCAT('商品', c.product_id)) AS productName,
                COALESCE(CASE WHEN ps.product_id = c.product_id THEN ps.price END, p.price, 0) AS price,
                c.quantity * COALESCE(CASE WHEN ps.product_id = c.product_id THEN ps.price END, p.price, 0) AS totalPrice,
                p.main_image AS productImage,
                c.create_time AS createTime
            FROM cart c
            LEFT JOIN db_user.user u ON u.id = c.user_id
            LEFT JOIN db_product.product p ON p.id = c.product_id
            LEFT JOIN db_product.product_sku ps ON ps.id = c.sku_id
            <where>
                <if test="queryUserId != null">
                    AND c.user_id = #{queryUserId}
                </if>
                <if test="username != null and username != ''">
                    AND (
                        u.username LIKE CONCAT('%', #{username}, '%')
                        OR u.nickname LIKE CONCAT('%', #{username}, '%')
                    )
                </if>
            </where>
            ORDER BY c.id DESC
            </script>
            """)
    IPage<CartListDTO> selectCartPage(Page<CartListDTO> page, @Param("username") String username, @Param("queryUserId") Long queryUserId);
}
