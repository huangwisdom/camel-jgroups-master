package it.redhat;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.UriEndpointComponent;

/**
 * The jgroups-master camel component ensures that only a single endpoint in a cluster is active at any
 * point in time with all other JVMs being hot standbys which wait until the master JVM dies before
 * taking over to provide high availability of a single consumer.
 */
public class JGroupsMasterComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {
        int idx = remaining.indexOf(':');
        if (idx <= 0) {
            throw new IllegalArgumentException("Missing : in URI so cannot split the group name from the actual URI for '" + remaining + "'");
        }
        // we are registering a regular endpoint
        String name = remaining.substring(0, idx);
        String childUri = remaining.substring(idx + 1);
        // we need to apply the params here
        if (params != null && params.size() > 0) {
            childUri = childUri + "?" + uri.substring(uri.indexOf('?') + 1);
        }
        JGroupsMasterEndpoint answer = new JGroupsMasterEndpoint(uri, this, name, childUri);
        return answer;
    }
}
