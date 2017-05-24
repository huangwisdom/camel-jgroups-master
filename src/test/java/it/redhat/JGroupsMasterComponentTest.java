package it.redhat;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class JGroupsMasterComponentTest extends CamelTestSupport {

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
                from("camel-jgroups-master://foo")
                  .to("camel-jgroups-master://bar")
                  .to("mock:result");
            }
        };
    }
}
