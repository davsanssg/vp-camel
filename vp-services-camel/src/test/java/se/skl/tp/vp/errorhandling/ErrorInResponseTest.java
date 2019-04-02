package se.skl.tp.vp.errorhandling;

import static org.apache.camel.test.junit4.TestSupport.assertStringContains;
import static se.skl.tp.vp.util.soaprequests.RoutingInfoUtil.createRoutingInfo;
import static se.skl.tp.vp.util.takcache.TestTakDataDefines.RIV20;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import se.skl.tp.vp.constants.HttpHeaders;
import se.skl.tp.vp.TestBeanConfiguration;
import se.skl.tp.vp.integrationtests.utils.MockProducer;
import se.skl.tp.vp.util.soaprequests.TestSoapRequests;
import se.skltp.takcache.RoutingInfo;
import se.skltp.takcache.TakCache;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = TestBeanConfiguration.class)
@TestPropertySource("classpath:application.properties")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ErrorInResponseTest {

  public static final String REMOTE_EXCEPTION_MESSAGE = "Fel fel fel";
  public static final String VP_ADDRESS = "http://localhost:12312/vp";
  public static final String NO_EXISTING_PRODUCER = "http://localhost:12100/vp";
  public static final String MOCK_PRODUCER_ADDRESS = "http://localhost:12126/vp";

  public static final String NETTY4_HTTP = "netty4-http:";

  @Autowired
  private CamelContext camelContext;

  @EndpointInject(uri = "mock:result")
  protected MockEndpoint resultEndpoint;

  @Produce(uri = "direct:start")
  protected ProducerTemplate template;


  @MockBean
  TakCache takCache;

  private static MockProducer mockProducer;

  private static boolean isContextStarted = false;

  @Before
  public void setUp() throws Exception {
    if(!isContextStarted){
      mockProducer = new MockProducer(camelContext, MOCK_PRODUCER_ADDRESS);
      addConsumerRoute(camelContext);
      camelContext.start();
      isContextStarted=true;
    }
    resultEndpoint.reset();

  }

  @Test //Test för när ett SOAP-fault kommer från Producenten
  public void errorInResponseTest() throws Exception {
    mockProducer.setResponseHttpStatus(500);
    mockProducer.setResponseBody(SoapFaultHelper.generateSoap11FaultWithCause(REMOTE_EXCEPTION_MESSAGE));

    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(MOCK_PRODUCER_ADDRESS, RIV20));
    setTakCacheMockResult(list);

    resultEndpoint.expectedBodiesReceived(SoapFaultHelper.generateSoap11FaultWithCause(REMOTE_EXCEPTION_MESSAGE));

    template.sendBody(TestSoapRequests.GET_CERTIFICATE_TO_UNIT_TEST_SOAP_REQUEST);
    resultEndpoint.assertIsSatisfied();
  }

  @Test //Test för när en Producent inte går att nå
  public void noProducerOnURLResponseTest() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(NO_EXISTING_PRODUCER, RIV20));
    setTakCacheMockResult(list);

    template.sendBody(TestSoapRequests.GET_CERTIFICATE_TO_UNIT_TEST_SOAP_REQUEST);
    String resultBody = resultEndpoint.getExchanges().get(0).getIn().getBody(String.class);
    assertStringContains(resultBody, "VP009");
    assertStringContains(resultBody, "address");
    assertStringContains(resultBody, "Exception Caught by Camel when contacting producer.");
    resultEndpoint.assertIsSatisfied();
  }

  @Test //Test för när en Producent svarar med ett tomt svar
  public void emptyResponseTest() throws Exception {
    mockProducer.setResponseHttpStatus(500);
    mockProducer.setResponseBody("");

    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(MOCK_PRODUCER_ADDRESS, RIV20));
    setTakCacheMockResult(list);

    template.sendBody(TestSoapRequests.GET_CERTIFICATE_TO_UNIT_TEST_SOAP_REQUEST);
    String resultBody = resultEndpoint.getExchanges().get(0).getIn().getBody(String.class);
    assertStringContains(resultBody, "VP009");
    assertStringContains(resultBody, "address");
    assertStringContains(resultBody, "Empty message when server responded with status code:");
    resultEndpoint.assertIsSatisfied();
  }

  @Test //Test för när en Producent svarar med annat än SOAP tex ett exception, kontrolleras inte av VP
  public void nonSOAPResponseTest() throws Exception {
    mockProducer.setResponseHttpStatus(500);
    mockProducer.setResponseBody(createExceptionMessage());

    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(MOCK_PRODUCER_ADDRESS, RIV20));
    setTakCacheMockResult(list);

    template.sendBody(TestSoapRequests.GET_CERTIFICATE_TO_UNIT_TEST_SOAP_REQUEST);
    String resultBody = resultEndpoint.getExchanges().get(0).getIn().getBody(String.class);
    assertStringContains(resultBody, "java.lang.NullPointerException");
    resultEndpoint.assertIsSatisfied();
  }

  private void setTakCacheMockResult(List<RoutingInfo> list) {
    Mockito.when(takCache.getRoutingInfo("urn:riv:insuranceprocess:healthreporting:GetCertificateResponder:1", "UnitTest"))
        .thenReturn(list);
    Mockito
        .when(takCache.isAuthorized("UnitTest", "urn:riv:insuranceprocess:healthreporting:GetCertificateResponder:1", "UnitTest"))
        .thenReturn(true);
  }


  private void addConsumerRoute(CamelContext camelContext) throws Exception {
    camelContext.addRoutes(new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("direct:start").routeDescription("Consumer").id("Consumer")
            .setHeader(HttpHeaders.X_VP_SENDER_ID, constant("UnitTest"))
            .setHeader(HttpHeaders.X_VP_INSTANCE_ID, constant("dev_env"))
            .setHeader("X-Forwarded-For", constant("1.2.3.4"))
            .to("netty4-http:"+VP_ADDRESS)
            .to("mock:result"); ;
      }
    });
  }

  private String createExceptionMessage(){
    try {
      String.valueOf(null);
    } catch (NullPointerException e) {
      return(e.toString());
    }
    return "Should not happen";
  }

}