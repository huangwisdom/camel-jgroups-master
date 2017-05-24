package it.redhat;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.remote.SftpEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;

public class JGroupsMasterComponentTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    public void testChildIsWorking() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        template.sendBody("foo");
        mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
        template.getCamelContext().stop();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("jgroups-master:lock:direct:start")
                  .to("mock:result");
            }
        };
    }

    @Test
    public void testRawPropertiesOnChild() throws Exception {
        final String uri = "jgroups-master://name:sftp://myhost/inbox?password=RAW(_BEFORE_AMPERSAND_&_AFTER_AMPERSAND_)&username=jdoe";

        DefaultCamelContext ctx = new DefaultCamelContext();
        JGroupsMasterEndpoint master = (JGroupsMasterEndpoint) ctx.getEndpoint(uri);
        SftpEndpoint sftp = (SftpEndpoint) master.getEndpoint();

        assertEquals("_BEFORE_AMPERSAND_&_AFTER_AMPERSAND_", sftp.getConfiguration().getPassword());
        ctx.stop();
    }
}
