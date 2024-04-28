package com.xxl.job.scheduler.repo.newJob.model;

import lombok.Data;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 5:14 下午
 */
@Data
public class UserInfo {
    private int id;
    private String username;		// 账号
    private String password;		// 密码
    private int role;				// 角色：0-普通用户、1-管理员
    private String permission;	// 权限：执行器ID列表，多个逗号分割
}
