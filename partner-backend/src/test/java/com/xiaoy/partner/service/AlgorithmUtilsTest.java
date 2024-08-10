package com.xiaoy.partner.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: AlgorithmUtilsTest
 * Description: 算法工具类测试
 *
 * @Author: xiaoyu
 * @create: 2024-08-08 16:51
 * @version: 1.0
 */
@SpringBootTest
public class AlgorithmUtilsTest {

    @Resource
    private UserService userService;

    @Test
    public void AlgorithmTest(){
        User user1 = userService.getById(1818257398869258241L);
        User user2 = userService.getById(1818257399007670272L);
        String tags1 = user1.getTags();
        String tags2 = user2.getTags();
        System.out.println(tags1);
        System.out.println(tags2);
        Gson gson = new Gson();
        List<String> tagList1 = gson.fromJson(tags1, new TypeToken<List<String>>() {
        }.getType());
        List<String> tagList2 = gson.fromJson(tags2, new TypeToken<List<String>>() {
        }.getType());
        System.out.println(AlgorithmUtils.minDistance(tagList1,tagList2));
        //List<String> list1 = Arrays.asList("java", "大一", "男");
        //List<String> list2 = Arrays.asList("java", "大二", "男");
        //List<String> list3 = Arrays.asList("vue", "大二", "女");
        //System.out.println(AlgorithmUtils.minDistance(list1, list2)); //1
        //System.out.println(AlgorithmUtils.minDistance(list1, list3)); //3
        //System.out.println(AlgorithmUtils.minDistance(list2, list3)); //2

    }


}
