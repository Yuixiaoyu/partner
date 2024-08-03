package com.xiaoy.partner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoy.partner.model.domain.UserTeam;
import com.xiaoy.partner.service.UserTeamService;
import com.xiaoy.partner.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 张飞宇
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-08-02 19:33:13
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




