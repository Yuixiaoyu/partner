package com.xiaoy.partner.service;

// [编程学习交流圈](https://www.code-nav.cn/) 连接万名编程爱好者，一起优秀！20000+ 小伙伴交流分享、40+ 大厂嘉宾一对一答疑、100+ 各方向编程交流群、4000+ 编程问答参考

import com.alibaba.excel.EasyExcel;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.tools.InsertTestDataToDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务测试
 *
 * @author  
 * @from   
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 测试添加用户
     */
    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("xiaoy");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }



    /**
     * 测试更新用户
     */
    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("xiaoy");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    /**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }



    /**
     * 测试获取用户
     */
    @Test
    public void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    /**
     * 测试用户注册
     */
    @Test
    void userRegister() {
        String userAccount = "xiaoy";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xiao";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xiaoy";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xiao y";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "dogxiaoy";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xiaoy";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
    }

    @Test
    public void searchUserByTags(){
        List<String> list = Arrays.asList("java");
        List<User> users = userService.searchUserByTags(list);
        System.out.println(users);
    }
    @Test
    public void testUserDate(){
        InsertTestDataToDB insertTestDateToDB = new InsertTestDataToDB(userService);
        List<User> users = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            users.add(insertTestDateToDB.generateData());
        }
        long end = System.currentTimeMillis();
        System.out.println("共用时："+(end-start)+"ms");
        System.out.println(users.size());
        System.out.println(users.get(1));
    }

    /**
     * 向user表中添加模拟数据
     */
    @Test
    public void userInsertTestData(){
        InsertTestDataToDB dataToDB = new InsertTestDataToDB(userService);
        //try {
        //    dataToDB.processBatchInsert(1000);
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        //}
        dataToDB.insertBigData(1000);
    }
    @Test
    public void exportData(){
        // 设置文件导出的路径
        String realPath = "/";
        File folder = new File(realPath);
        if (!folder.isDirectory()){
            folder.mkdirs();
        }
        long start = System.currentTimeMillis();
        String fileName = "testExcel.xlsx";
        EasyExcel.write(fileName, User.class).sheet("用户表").doWrite(userService.list());
        long end = System.currentTimeMillis();
        System.out.println("导出用时："+(end-start));
    }

    @Test
    public void testCode(){
        String str1 = "abc";
        String str2 = "qe";
        List<String> list = Arrays.asList(str1, str2);
        String result = list.stream().collect(Collectors.joining("\",\"", "[\"", "\"]"));
        System.out.println(result);
    }

    @Test
    public void testRedis(){
        redisTemplate.opsForValue().set("aa","bb");
        System.out.println("写入完成");

    }

}