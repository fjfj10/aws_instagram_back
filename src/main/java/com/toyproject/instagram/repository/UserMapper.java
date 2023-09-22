package com.toyproject.instagram.repository;

import com.toyproject.instagram.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public Integer saveUser(User user);
}
