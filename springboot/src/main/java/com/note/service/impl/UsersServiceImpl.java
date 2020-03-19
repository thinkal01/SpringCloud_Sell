package com.note.service.impl;

import com.note.dao.UsersRepository;
import com.note.pojo.Users;
import com.note.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * UsersService接口实现类
 */
// @Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    @Cacheable(value = "users")
    public List<Users> findUserAll() {
        return this.usersRepository.findAll();
    }

    @Override
    //@Cacheable:对当前查询的对象做缓存处理
    @Cacheable(value = "users")
    public Users findUserById(Integer id) {
        return usersRepository.findById(id).get();
    }

    @Override
    @Cacheable(value = "users", key = "#pageable.pageSize")
    public Page<Users> findUserByPage(Pageable pageable) {
        return this.usersRepository.findAll(pageable);
    }

    @Override
    //@CacheEvict(value="users",allEntries=true) 清除缓存中以users缓存策略缓存的对象
    @CacheEvict(value = "users", allEntries = true)
    public void saveUsers(Users users) {
        this.usersRepository.save(users);
    }

}
