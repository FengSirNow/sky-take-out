package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 冯纪元
 * @ClassName SetmealServiceImpl
 * @description: TODO
 * @datetime 2025年 10月 23日 11:05
 * @version: 1.0
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        setmealDishMapper.insertBatch(setmealDTO.getSetmealDishes(),setmeal.getId());
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        // 使用PageHelper进行分页，设置当前页码和每页显示数量
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        List<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.size(),page);
    }

    @Override
    public SetmealVO selectById(Integer id) {
        SetmealVO setmealVO = setmealMapper.selectById(id);
        setmealVO.setSetmealDishes(setmealDishMapper.getSetmealDishesBySetmealId(id));
        return setmealVO;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        if(status == StatusConstant.ENABLE){
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            for (Dish dish : dishList) {
                if(dish.getStatus() == StatusConstant.DISABLE){
                    //有停售的菜品，不能启售套餐
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        setmealMapper.startOrStop(status,id);
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        setmealDishMapper.insertBatch(setmealDTO.getSetmealDishes(),setmealDTO.getId());
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            SetmealVO setmeal = setmealMapper.selectById(Integer.valueOf(id.toString()));
            if(StatusConstant.ENABLE == setmeal.getStatus()){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        for (Long id : ids){
            setmealDishMapper.deleteBySetmealId(id);
            setmealMapper.delete(id);
        }
    }


    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
