package com.note.dao;

import com.note.pojo.Users;
import org.springframework.data.repository.CrudRepository;

/**
 * CrudRepository接口
 *
 *
 */
public interface UsersRepositoryCrudRepository extends CrudRepository<Users, Integer> {

}
