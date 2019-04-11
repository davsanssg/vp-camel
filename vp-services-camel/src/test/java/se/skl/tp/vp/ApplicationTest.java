package se.skl.tp.vp;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.EnableRouteCoverage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Application.class)
@EnableRouteCoverage
@DirtiesContext
public class ApplicationTest {

    @Test
    public void contextLoads(){

    }

}