package it.redhat;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JGroupsMaster producer.
 */
public class JGroupsMasterProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsMasterProducer.class);
    private JGroupsMasterEndpoint endpoint;

    public JGroupsMasterProducer(JGroupsMasterEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
