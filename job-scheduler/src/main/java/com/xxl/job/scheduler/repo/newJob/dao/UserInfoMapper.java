package com.xxl.job.scheduler.repo.newJob.dao;

import com.xxl.job.scheduler.repo.newJob.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 5:14 下午
 */
@Mapper
interface UserInfoMapper {

    List<UserInfo> pageList(@Param("offset") int offset,
                                   @Param("pagesize") int pagesize,
                                   @Param("username") String username,
                                   @Param("role") int role);
    int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("username") String username,
                             @Param("role") int role);

    UserInfo loadByUserName(@Param("username") String username);

    int save(UserInfo xxlJobUser);

    int update(UserInfo xxlJobUser);

    int delete(@Param("id") int id);
}
