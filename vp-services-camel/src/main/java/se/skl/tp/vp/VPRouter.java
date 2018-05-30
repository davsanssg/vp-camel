package se.skl.tp.vp;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skl.tp.vp.requestreader.RequestReaderProcessor;
import se.skl.tp.vp.vagval.VagvalProcessor;

@Component
public class VPRouter extends RouteBuilder {

    public static final String VP_HTTP_ROUTE = "vp-http-route";
    public static final String NETTY4_HTTP_FROM = "netty4-http:http://localhost:12312/vp";
    public static final String NETTY4_HTTP_TOD = "netty4-http:${exchange.getProperty('vagval')}";

    @Autowired
    VagvalProcessor vagvalProcessor;

    @Autowired
    RequestReaderProcessor requestReaderProcessor;

    @Override
    public void configure() throws Exception {

        from(NETTY4_HTTP_FROM).routeId(VP_HTTP_ROUTE)
                .process(requestReaderProcessor)
                .process(vagvalProcessor)
                .toD(NETTY4_HTTP_TOD);
    }
}
