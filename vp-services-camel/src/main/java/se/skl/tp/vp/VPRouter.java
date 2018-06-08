package se.skl.tp.vp;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import se.skl.tp.vp.requestreader.RequestReaderProcessor;
import se.skl.tp.vp.vagval.VagvalProcessor;

@Component
public class VPRouter extends RouteBuilder {

    public static final String VP_HTTP_ROUTE = "vp-http-route";
    public static final String VP_ROUTE = "vp-route";
    public static final String NETTY4_HTTP_FROM = "netty4-http:{{vp.http.route.url}}";
    public static final String NETTY4_HTTP_TOD = "netty4-http:${exchange.getProperty('vagval')}";
    public static final String DIRECT_VP = "direct:vp";
    public static final String VP_HTTPS_ROUTE = "vp-https-route";
    public static final String NETTY4_HTTPS_INCOMING = "netty4-http:https://localhost:443/vp?sslContextParameters=#incomingSSLContextParameters&ssl=true";
    public static final String NETTY4_HTTPS_OUTGOING_TOD = "netty4-http:${exchange.getProperty('vagval')}?sslContextParameters=#outgoingSSLContextParameters&ssl=true";

    @Autowired
    Environment env;

    @Autowired
    VagvalProcessor vagvalProcessor;

    @Autowired
    RequestReaderProcessor requestReaderProcessor;

    @Override
    public void configure() throws Exception {
        from(NETTY4_HTTPS_INCOMING).routeId(VP_HTTPS_ROUTE)
                .to(DIRECT_VP);

        from(NETTY4_HTTP_FROM).routeId(VP_HTTP_ROUTE)
                .to(DIRECT_VP);

        from(DIRECT_VP).routeId(VP_ROUTE)
                .process(requestReaderProcessor)
                .process(vagvalProcessor)
                .choice()
                    .when(exchangeProperty("vagval").contains("https://"))
                        .toD(NETTY4_HTTPS_OUTGOING_TOD)
                    .otherwise()
                        .toD(NETTY4_HTTP_TOD)
                .endChoice();
    }
}
