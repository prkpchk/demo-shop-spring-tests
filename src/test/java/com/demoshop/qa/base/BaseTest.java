package com.demoshop.qa.base;

import com.demoshop.qa.TestApplication;
import com.demoshop.qa.core.config.AppConfig;
import com.demoshop.qa.core.junit.extensions.RetryExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Root base class for all tests. Boots the Spring test context (no web server)
 * to provide DI for config beans, JdbcTemplate, and RetrofitConfig.
 */
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(RetryExtension.class)
public abstract class BaseTest {

    @Autowired
    protected AppConfig appConfig;

    @Autowired(required = false)
    protected JdbcTemplate jdbcTemplate;
}
