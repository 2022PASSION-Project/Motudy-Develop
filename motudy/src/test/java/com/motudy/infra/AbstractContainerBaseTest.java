package com.motudy.infra;

import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.MySQLContainer;

public abstract class AbstractContainerBaseTest {

    static final MySQLContainer MY_SQL_CONTAINER;

    @Value("${mysql.container.image.name}")
    static private String imageName;

    static {
        MY_SQL_CONTAINER = new MySQLContainer(imageName);
        MY_SQL_CONTAINER.start();
    }
}
