package com.note.mybatis.service.impl;

import com.note.mybatis.mapper.UsersMapper;
import com.note.mybatis.service.UsersService;
import com.note.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public void addUser(Users users) {
        usersMapper.insertUser(users);
    }

    @Override
    public List<Users> findUserAll() {
        return usersMapper.selectUsersAll();
    }

    @Override
    public Users findUserById(Integer id) {
        return usersMapper.selectUsersById(id);
    }

    @Override
    public void updateUser(Users users) {
        usersMapper.updateUser(users);
    }

    @Override
    public void deleteUserById(Integer id) {
        usersMapper.deleteUserById(id);
    }
}
