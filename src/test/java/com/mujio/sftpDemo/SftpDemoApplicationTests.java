package com.mujio.sftpDemo;

import com.mujio.sftpDemo.utils.SFTPUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class SftpDemoApplicationTests {

    SFTPUtils sFTPUtils = new SFTPUtils();

    @Test
    void contextLoads() {
        boolean dirExist = sFTPUtils.isDirExist("/home/test", true);
        System.out.println(dirExist);
    }

}
