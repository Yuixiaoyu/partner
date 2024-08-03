package com.xiaoy.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoy.partner.common.ErrorCode;
import com.xiaoy.partner.exception.BusinessException;
import com.xiaoy.partner.model.domain.Team;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.model.domain.UserTeam;
import com.xiaoy.partner.model.enums.TeamStatusEnum;
import com.xiaoy.partner.service.TeamService;
import com.xiaoy.partner.mapper.TeamMapper;
import com.xiaoy.partner.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * @author 张飞宇
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-08-02 19:31:51
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //是否登录
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        //校验信息
        //队伍人数>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //队伍标题<=20
        String teamName = team.getName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        //描述<=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //status是否公开（int），不传默认0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //如果status是加密状态，一定要有密码，且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)&&(StringUtils.isBlank(password)|| password.length()>32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍密码不满足要求");
        }

        //超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"时间有误");
        }
        final long userId = loginUser.getId();

        //校验用户最多创建5个队伍
        //todo 有bug，用户可能会同时创建多个
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Team::getUserId,userId);
        long hasTeamNum = count(queryWrapper);
        if (hasTeamNum>=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个队伍");
        }

        //插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean save = save(team);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍创建失败");
        }
        Long teamId = team.getId();

        //插入用户 ==》队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍创建失败");
        }
        return teamId;
    }
}




