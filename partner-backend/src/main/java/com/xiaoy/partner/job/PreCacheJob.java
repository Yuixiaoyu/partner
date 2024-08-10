package com.xiaoy.partner.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: PreCacheJob
 * Description:
 *
 * @Author: xiaoyu
 * @create: 2024-08-01 8:51
 * @version: 1.0
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;


    List<Long> mainUserList = Arrays.asList(1L);


    @Scheduled(cron = "0 59 18 * * * ")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("partner:precachejob:docache:lock");
        try {
            //尝试获取锁，获取成功执行业务
            if (lock.tryLock(0,-1, TimeUnit.SECONDS)) {
                Thread.sleep(1000000);
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("partner:recommend:%s", userId);
                    try {
                        redisTemplate.opsForValue().set(redisKey, userPage, 1, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

}
