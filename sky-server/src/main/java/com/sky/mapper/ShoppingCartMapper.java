package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author 冯纪元
 * @ClassName ShoppingCartMapper
 * @description: TODO
 * @datetime 2025年 11月 03日 19:07
 * @version: 1.0
 */
@Mapper
public interface ShoppingCartMapper {


    void insert(ShoppingCart shoppingCart);


    List<ShoppingCart> list(ShoppingCart shoppingCart);


    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);


    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void deleteByUserId(Long currentId);
}
