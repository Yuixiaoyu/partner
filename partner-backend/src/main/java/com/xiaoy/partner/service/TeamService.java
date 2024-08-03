package com.xiaoy.partner.service;

import com.xiaoy.partner.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoy.partner.model.domain.User;

/**
* @author 张飞宇
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-08-02 19:31:51
*/
public interface TeamService extends IService<Team> {


    long addTeam(Team team, User loginUser);
}
