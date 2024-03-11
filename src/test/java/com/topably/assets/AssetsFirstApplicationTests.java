package com.topably.assets;

import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@SpringBootTest(classes = AssetsFirstApplication.class, properties = {"spring.cache.type=caffeine"})
class AssetsFirstApplicationTests extends IntegrationTestBase {

    @Test
    void contextLoads() {
    }

}
