package com.xiaoy.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoy.partner.common.ErrorCode;
import com.xiaoy.partner.exception.BusinessException;
import com.xiaoy.partner.model.domain.Team;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.model.domain.UserTeam;
import com.xiaoy.partner.model.dto.TeamQuery;
import com.xiaoy.partner.model.enums.TeamStatusEnum;
import com.xiaoy.partner.model.request.TeamAddRequest;
import com.xiaoy.partner.model.request.TeamJoinRequest;
import com.xiaoy.partner.model.request.TeamQuitRequest;
import com.xiaoy.partner.model.request.TeamUpdateRequest;
import com.xiaoy.partner.model.vo.TeamUserVo;
import com.xiaoy.partner.model.vo.UserVo;
import com.xiaoy.partner.service.TeamService;
import com.xiaoy.partner.mapper.TeamMapper;
import com.xiaoy.partner.service.UserService;
import com.xiaoy.partner.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;


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
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不满足要求");
        }

        //超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "时间有误");
        }
        final long userId = loginUser.getId();

        //校验用户最多创建5个队伍
        //todo 有bug，用户可能会同时创建多个
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Team::getUserId, userId);
        long hasTeamNum = count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }

        //插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean save = save(team);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍创建失败");
        }
        Long teamId = team.getId();

        //插入用户 ==》队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍创建失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)){
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            Long userId = teamQuery.getUserId();
            //根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            //根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        //不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        //关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            //脱敏用户信息
            if (user != null) {
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user, userVo);
                teamUserVo.setCreateUserVo(userVo);
            }
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId",team.getId());
            int hasJoinNum = (int) userTeamService.count(userTeamQueryWrapper);
            teamUserVo.setHasJoinNum(hasJoinNum);
            teamUserVoList.add(teamUserVo);
        }
        return teamUserVoList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        Team oldTeam = getTeamById(id);
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        //加密状态必须有密码
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
            }
        }
        //公开状态不能有密码
        if (statusEnum.equals(TeamStatusEnum.PUBLIC)) {
            if (StringUtils.isNotBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "公开状态不能设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "不能加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        RLock lock = redissonClient.getLock("xiaoyu:join_team");
        try {
            //抢到锁后并执行
            while (true){
                if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS));{
                    //查看当前用户加入的数量
                    long userId = loginUser.getId();
                    QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId", userId);
                    long hasJoinNum = userTeamService.count(queryWrapper);
                    if (hasJoinNum >= 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多加入5个队伍");
                    }
                    //不能重复加入以加入的队伍
                    queryWrapper.eq("teamId", teamId);
                    long hasJoinUserTeam = userTeamService.count(queryWrapper);
                    if (hasJoinUserTeam > 0) {
                        throw new BusinessException(ErrorCode.NULL_ERROR, "用户已加入");
                    }
                    //已加入队伍的人数
                    long teamHasJoinNum = countTeamUserByTeamId(teamId);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.NULL_ERROR, "队伍人数已满");
                    }
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            //只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    private long countTeamUserByTeamId(Long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        return userTeamService.count(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean QuitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setUserId(userId);
        queryUserTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户未加入队伍");
        }
        long teamHasJoinNum = countTeamUserByTeamId(teamId);
        //队伍只剩一人，直接解散队伍
        if (teamHasJoinNum == 1) {
            //删除队伍表中的信息和队伍关系表中的信息
            removeById(teamId);
        } else {
            //队伍存在两个人以上
            //判断是不是队长
            if (team.getUserId() == userId) {
                //将队长顺位给第二个加入队伍的人
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //更新当前队伍的队长
                Team update = new Team();
                update.setId(teamId);
                update.setUserId(userId);
                boolean result = updateById(update);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }
            }
        }
        //移除关系
        return userTeamService.remove(queryWrapper);
    }

    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id,User loginUser) {
        if (id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验队伍是否存在
        Team team = getTeamById(id);
        // 校验是不是队长
        if (team.getUserId() !=loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH,"您不是当前队伍的队长");
        }
        Long teamId = team.getId();
        //移除所有关联关系
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"解散队伍失败");
        }
        //删除队伍
        return  removeById(teamId);

    }


}




