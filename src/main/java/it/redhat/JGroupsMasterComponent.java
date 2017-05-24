package it.redhat;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;

/**
 * Represents the component that manages {@link JGroupsMasterEndpoint}.
 */
public class JGroupsMasterComponent extends UriEndpointComponent {
    
    public JGroupsMasterComponent() {
        super(JGroupsMasterEndpoint.class);
    }

    public JGroupsMasterComponent(CamelContext context) {
        super(context, JGroupsMasterEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new JGroupsMasterEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
