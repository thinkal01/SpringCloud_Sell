package com.note.dao;

import com.note.pojo.Users;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * 
 *PagingAndSortingRepository接口
 *
 */
public interface UsersRepositoryPagingAndSorting extends PagingAndSortingRepository<Users,Integer> {

}
