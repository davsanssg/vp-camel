package se.skl.tp.vp.constants;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import se.skl.tp.vp.errorhandling.VpCodeMessages;
import se.skl.tp.vp.exceptions.VpSemanticErrorCodeEnum;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VpCodeMessagesTest {

  @Autowired
  VpCodeMessages vpCodeMessages;

  @Test
  public void messageByKeyTest() throws Exception {
    String result = vpCodeMessages.getMessage("VP001");
    assertEquals("No RIV version configured", result);
  }

  @Test
  public void messageByErrorCodeTest() throws Exception {
    String result = vpCodeMessages.getMessage(VpSemanticErrorCodeEnum.VP001);
    assertEquals("No RIV version configured", result);
  }

  @Configuration
  @ComponentScan(basePackages = {"se.skl.tp.vp.errorhandling"})
  static class VpCodeMessagesTestConfiguration {

  }
}