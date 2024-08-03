package com.xiaoy.partner.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;

import java.io.File;

/**
 * ClassName: ImportExcel
 * Description: 导入excel数据
 *
 * @Author: fy
 * @create: 2024-07-28 19:56
 * @version: 1.0
 */
public class ImportExcel {

    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "G:\\Code\\partnerCode\\partner-backend\\src\\main\\resources\\testExcel.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, ExcelData.class, new ExcelDataListener()).sheet().doRead();
    }

}
