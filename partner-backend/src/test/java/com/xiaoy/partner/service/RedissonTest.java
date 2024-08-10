package com.xiaoy.partner.service;

import org.junit.jupiter.api.Test;
import org.redisson.RedissonScript;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * ClassName: RedissonTest
 * Description:
 *
 * @Author: xiaoyu
 * @create: 2024-08-02 11:31
 * @version: 1.0
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;


    @Test
    public void test(){
        //list
        RList<String> rList = redissonClient.getList("test");
        rList.add("abc");
        System.out.println(rList.get(0));


    }

}
