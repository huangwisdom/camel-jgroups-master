package it.redhat;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a Master endpoint which only becomes active when it obtains the groupName lock through JGroups
 */
@ManagedResource(description = "Managed Jgroups Master Endpoint")
@UriEndpoint(scheme = "jgroups-master", title = "JGroups Master", syntax="jgroups-master:groupName:consumerEndpointUri",
        consumerClass = JGroupsMasterConsumer.class,consumerOnly = true, lenientProperties = true, label = "clustering")
public class JGroupsMasterEndpoint extends DefaultEndpoint {

    private final JGroupsMasterComponent component;
    private final String consumerEndpointUri;

    @UriPath (description = "The lock group to use")
    @Metadata(required = "false")
    @UriParam(defaultValue = "lock")
    private String groupName;

    @UriPath(description = "The consumer endpoint to use in master/slave mode")
    @Metadata(required = "true")
    private final Endpoint consumerEndpoint;

    public JGroupsMasterEndpoint(String uri, JGroupsMasterComponent component, String groupName, String consumerEndpointUri) {
        super(uri, component);
        this.component = component;
        this.groupName = groupName;
        this.consumerEndpointUri = consumerEndpointUri;
        this.consumerEndpoint = getCamelContext().getEndpoint(consumerEndpointUri);
    }

    public Endpoint getEndpoint() {
        return consumerEndpoint;
    }

    public Endpoint getConsumerEndpoint() {
        return getEndpoint();
    }

    @ManagedAttribute(description = "The consumer endpoint url to use in master/slave mode", mask = true)
    public String getConsumerEndpointUri() {
        return consumerEndpointUri;
    }

    @ManagedAttribute(description = "The name of the cluster group to use")
    public String getGroupName() {
        return groupName;
    }

    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException(
                "You cannot send messages to this endpoint:" + getEndpointUri());
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new JGroupsMasterConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    @Override
    public boolean isLenientProperties() {
        return true;
    }

}
