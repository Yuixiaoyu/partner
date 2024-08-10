package com.xiaoy.partner.tools;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.service.UserService;
import org.springframework.util.DigestUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ClassName: InsertTestDataToDB
 * Description:
 *
 * @Author: xiaoyu
 * @create: 2024-07-28 20:55
 * @version: 1.0
 */
public class InsertTestDataToDB {

    private  UserService userService;

    public InsertTestDataToDB(UserService userService) {
        this.userService = userService;
    }

    //因为账号与编号不能重复这里就做特殊处理，下面两个set（线程安全）用来筛选
    Set<String> userAccountSet = ConcurrentHashMap.newKeySet();
    Set<String> planetCodeSet = ConcurrentHashMap.newKeySet();
    Set<String> emailSet = ConcurrentHashMap.newKeySet();

    //这些放这里主要是便于查看
    private static final List<String> genders = Arrays.asList("男", "女");
    private static final List<String> directions = Arrays.asList("Java", "C++", "Go", "前端");
    private static final List<String> learningList = Arrays.asList("Spring","SpringBoot","Redis");
    private static final List<String> goals = Arrays.asList("考研", "春招", "秋招", "社招", "考公", "竞赛（蓝桥杯）", "转行", "跳槽");
    private static final List<String> ranks = Arrays.asList("初级", "中级", "高级", "王者");
    private static final List<String> identities = Arrays.asList("小学", "初中", "高中", "大一", "大二", "大三", "大四", "学生", "待业", "已就业", "研一", "研二", "研三");
    private static final List<String> statuses = Arrays.asList("乐观", "有点丧", "一般", "单身", "已婚", "有对象");


    private String url = "jdbc:mysql://localhost:3306/partner?useServerPrepStmts=false&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=UTF-8";
    private String user = "root";
    private String password = "admin";


    /**
     * 多线程的方式 实测10w数据7秒
     * @throws InterruptedException
     */
    public void processBatchInsert(int dateCount) throws InterruptedException {
        int totalUsers = dateCount; //总共待生成的数据
        int numThreads = 20; // 可以根据需要调整线程数
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        //每个线程的工作量
        int partitionSize = totalUsers / numThreads;
        //每个批次的大小
        int batchSize = 1000; // 可以根据需要调整批次大小
        long start = System.currentTimeMillis();
        // 使用多线程存储数据
        for (int i = 0; i < numThreads; i++) {
            //提交任务
            executor.submit(() -> {
                //创建一个列表用于存储批量插入的数据
                List<User> batchList = new ArrayList<>();
                for (int j = 0; j < partitionSize; j++) {
                    // 并发地生成数据并存入集合
                    User user = generateData();
                    //将数据添加到批量列表中
                    batchList.add(user);
                    //如果批量列表的大小达到了批次大小，就进行批量插入
                    if (batchList.size() == batchSize) {
                        //使用 insertBatchSomeColumn 方法，只插入非空字段
                        userService.saveBatch(batchList);
                        //清空批量列表，为下一批次做准备
                        batchList.clear();
                    }
                }
                //如果批量列表还有剩余的数据，也进行批量插入
                if (!batchList.isEmpty()) {
                    userService.saveBatch(batchList);
                }
            });
        }
        //如果没有executor.shutdown() 方法，那么线程池会一直等待新的任务，所以 executor.awaitTermination(10, TimeUnit.SECONDS) 方法会一直阻塞直到超时。
        executor.shutdown();
        //时间设置长一点
        boolean res = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        System.out.println(res);
        long end = System.currentTimeMillis();
        System.out.println("最终耗时："+(end-start));
        System.out.println(userService.count());
    }

    /**
     * 采用jdbc的方式直接插入（单线程10w数据大概耗时18秒）
     */
    public void insertBigData(int dataCount) {
        //定义连接、statement对象
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            //加载jdbc驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //连接mysql
            conn = DriverManager.getConnection(url, user, password);
            //将自动提交关闭
            conn.setAutoCommit(false);
            //编写sql
            String sql = "INSERT INTO user " +
                    "(username,id,userAccount,avatarUrl,gender,userPassword,phone,email,planetCode,tags) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)";
            //预编译sql
            pstm = conn.prepareStatement(sql);
            //判断循环多少次
            int dateCount = dataCount / 1000;
            //开始总计时
            long bTime1 = System.currentTimeMillis();
            //循环10次，每次1k数据，一共1万
            for(int i=0,j=0;i<dateCount;i++) {
                //开始循环
                while (j<=1000) {
                    User insertUser = generateData();
                    //赋值
                    pstm.setString(1, insertUser.getUsername());
                    pstm.setLong(2, insertUser.getId());
                    pstm.setString(3, insertUser.getUserAccount());
                    pstm.setString(4,insertUser.getAvatarUrl());
                    pstm.setInt(5, insertUser.getGender());
                    pstm.setString(6, insertUser.getUserPassword());
                    pstm.setString(7, insertUser.getPhone());
                    pstm.setString(8, insertUser.getEmail());
                    pstm.setString(9, insertUser.getPlanetCode());
                    pstm.setString(10, insertUser.getTags());
                    //添加到同一个批处理中
                    pstm.addBatch();
                    j++;
                }
                j=0;
                //执行批处理
                pstm.executeBatch();
                //提交事务
                conn.commit();
            }
            //关闭总计时
            long eTime1 = System.currentTimeMillis();
            //输出
            System.out.println("插入数据共耗时："+(eTime1-bTime1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }
    //生成数据
    FakeValuesService fakeValuesService = new FakeValuesService(
            new Locale("zh-CN"), new RandomService());
    //设置为 china ，随机出来的名称符合我们的习惯
    Faker faker = new Faker(Locale.CHINA);

    public User generateData () {
        return generateUsers(faker,fakeValuesService);
    }

    //生成用户
    //排除了数据库默认生成数据的字段
    private User generateUsers(Faker faker, FakeValuesService fakeValuesService){
        User user = new User();
        //设置用户id,这里采用雪花算法来生成id，
        //如果不想设置id，可以删掉下面代码，直接采用数据库自增
        user.setId(IdUtil.getSnowflake().nextId());

        //设置用户昵称：这里为中文
        user.setUsername(faker.name().name());

        //设置用户账户8位
        String userAccount;
        do {
            userAccount = RandomUtil.randomNumbers(8);
        }while (userAccountSet.contains(userAccount));
        userAccountSet.add(userAccount);
        user.setUserAccount(userAccount);

        //设置用户头像 https://picsum.photos/200/200?random= 是一个随机生成图片的网站
        String fakeAvatarUrl = "https://picsum.photos/200/200?random=" + faker.random().nextInt(3);
        user.setAvatarUrl(fakeAvatarUrl);

        //随机生成 1 0 来生成男女
        //String gender = faker.random().nextInt(2) == 0 ? "男" : "女";
        user.setGender(faker.random().nextInt(2));

        // 这里密码加密采用和注册时的密码加密方式一样的md5，同时加盐处理和注册保持一样
        // 我为了测试数据能够直接使用登录，所以所有的密码直接写死就不自动生成了，不然测试数据真的就是只能看了
        user.setUserPassword(DigestUtils.md5DigestAsHex(("xiaoy" + "12345678").getBytes()));

        //随机生成电话：regexify 是利用正则表达式来生成
        String regex = "1[3456789]\\d{9}"; // 第一位为1，第二位为3或5，后面9位为数字
        user.setPhone(fakeValuesService.regexify(regex));

        //随机生成邮箱
        String email;
        do {
            email = RandomUtil.randomNumbers(10);
        }while (emailSet.contains(email));
        user.setEmail(email+"qq.com"); //这里的邮箱后缀也可以写在列表中随机读取一个，我为了偷懒😄

        //随机生成8位编号
        String planetCode;
        do {
            planetCode = RandomUtil.randomNumbers(8);
        }while (planetCodeSet.contains(planetCode));
        planetCodeSet.add(planetCode);
        user.setPlanetCode(planetCode);
        //随机标签
        String tags = generateTags();
        user.setTags(tags);
        return user;
    }

    //添加标签
    private  String generateTags() {
        String gender = RandomUtil.randomEle(genders);
        String direction = RandomUtil.randomEle(directions);
        String learning = RandomUtil.randomEle(learningList);
        String goal = RandomUtil.randomEle(goals);
        String rank = RandomUtil.randomEle(ranks);
        String identity = RandomUtil.randomEle(identities);
        String status = RandomUtil.randomEle(statuses);
        List<String> list = Arrays.asList(gender, direction, learning, goal, rank, identity, status);
        return  list.stream().collect(Collectors.joining("\",\"", "[\"", "\"]"));
    }

}
