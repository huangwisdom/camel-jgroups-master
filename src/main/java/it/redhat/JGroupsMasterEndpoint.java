package it.redhat;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a JGroupsMaster endpoint.
 */
@UriEndpoint(scheme = "camel-jgroups-master", title = "JGroupsMaster", syntax="camel-jgroups-master:name", consumerClass = JGroupsMasterConsumer.class, label = "JGroupsMaster")
public class JGroupsMasterEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = "true")
    @UriParam(defaultValue = "lock")

    private String name;

    public JGroupsMasterEndpoint() {
    }

    public JGroupsMasterEndpoint(String uri, JGroupsMasterComponent component) {
        super(uri, component);
    }

    public JGroupsMasterEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new JGroupsMasterProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new JGroupsMasterConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
