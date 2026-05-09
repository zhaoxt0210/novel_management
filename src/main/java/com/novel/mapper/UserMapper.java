<<<<<<< HEAD
package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT COUNT(*) FROM user WHERE role = #{role}")
    Long countByRole(Integer role);
    
    @Update("UPDATE user SET role = #{role} WHERE id = #{userId}")
    void updateRole(Long userId, Integer role);
=======
package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT COUNT(*) FROM user WHERE role = #{role}")
    Long countByRole(Integer role);
    
    @Update("UPDATE user SET role = #{role} WHERE id = #{userId}")
    void updateRole(Long userId, Integer role);
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}