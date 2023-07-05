package com.anthonyzero.snowflake;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SnowflakeIdFactoryTest {

    @Autowired
    private SnowflakeIdFactory snowflakeIdFactory;

    @Test
    void generateTest() {
        System.out.println(snowflakeIdFactory.create());
    }


}
