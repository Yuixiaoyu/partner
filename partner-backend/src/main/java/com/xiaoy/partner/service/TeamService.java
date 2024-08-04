package com.xiaoy.partner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoy.partner.model.domain.Team;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.model.dto.TeamQuery;
import com.xiaoy.partner.model.request.TeamJoinRequest;
import com.xiaoy.partner.model.request.TeamUpdateRequest;
import com.xiaoy.partner.model.vo.TeamUserVo;

import java.util.List;

/**
* @author 张飞宇
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-08-02 19:31:51
*/
public interface TeamService extends IService<Team> {


    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍信息
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
