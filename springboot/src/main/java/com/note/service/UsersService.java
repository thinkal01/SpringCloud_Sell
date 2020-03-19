package com.note.service;

import com.note.pojo.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsersService {

    List<Users> findUserAll();

    Users findUserById(Integer id);

    Page<Users> findUserByPage(Pageable pageable);

    void saveUsers(Users users);
}
