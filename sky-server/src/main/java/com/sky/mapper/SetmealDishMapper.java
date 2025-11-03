package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes,@Param("id") Long id);

    List<SetmealDish> getSetmealDishesBySetmealId(Integer id);

    void deleteBySetmealId(Long id);

    List<Long> getSetmealIdsByDishId(Long id);
}
