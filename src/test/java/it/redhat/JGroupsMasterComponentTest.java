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

    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:results")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "seda:bar")
    protected ProducerTemplate template;

    @Test
    public void testJGroupsMaster() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       
        
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("camel-jgroups-master:lock://foo")
                  .to("mock:result");
            }
        };
    }

    @Test
    public void testEndpoint() throws Exception {
        // check the endpoint configuration
        List<Route> registeredRoutes = camelContext.getRoutes();
        assertEquals("number of routes", 1, registeredRoutes.size());
        JGroupsMasterEndpoint endpoint = (JGroupsMasterEndpoint) registeredRoutes.get(0).getEndpoint();
        assertEquals("wrong endpoint uri", "seda:bar", endpoint.getConsumerEndpointUri());

        String expectedBody = "<matched/>";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        // lets wait for the entry to be registered...
        Thread.sleep(5000);

        template.sendBodyAndHeader(expectedBody, "foo", "bar");

        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @Test
    public void testRawPropertiesOnChild() throws Exception {
        final String uri = "jgroups-master://name:sftp://myhost/inbox?password=RAW(_BEFORE_AMPERSAND_&_AFTER_AMPERSAND_)&username=jdoe";

        DefaultCamelContext ctx = new DefaultCamelContext();
        JGroupsMasterEndpoint master = (JGroupsMasterEndpoint) ctx.getEndpoint(uri);
        SftpEndpoint sftp = (SftpEndpoint) master.getEndpoint();

        assertEquals("_BEFORE_AMPERSAND_&_AFTER_AMPERSAND_", sftp.getConfiguration().getPassword());
    }
}
