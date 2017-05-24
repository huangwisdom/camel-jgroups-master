package it.redhat;

import java.util.Date;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.SuspendableService;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.apache.camel.util.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A consumer which is only really active while it holds the master lock
 */
@ManagedResource(description = "Managed JGroups Master Consumer")
public class JGroupsMasterConsumer extends DefaultConsumer {

    private static final transient Logger LOG = LoggerFactory.getLogger(JGroupsMasterConsumer.class);
    private final JGroupsMasterEndpoint endpoint;
    private final Processor processor;
    private Consumer delegate;
    private SuspendableService delegateService;


    public JGroupsMasterConsumer(JGroupsMasterEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.processor = processor;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        LOG.info("Attempting to become master for endpoint: " + endpoint.getEndpoint() + " in " + endpoint.getCamelContext() + " with groupName: " + endpoint.getGroupName());
        acquireLock();
        if (delegate == null) {
            try {
                // ensure endpoint is also started
                LOG.info("Elected as master. Starting consumer: {}", endpoint.getConsumerEndpoint());
                ServiceHelper.startService(endpoint.getConsumerEndpoint());

                delegate = endpoint.getConsumerEndpoint().createConsumer(processor);
                delegateService = null;
                if (delegate instanceof SuspendableService) {
                    delegateService = (SuspendableService) delegate;
                }

                ServiceHelper.startService(delegate);
            } catch (Exception e) {
                LOG.error("Failed to start master consumer for: " + endpoint, e);
            }

            LOG.info("Elected as master. Consumer started: {}", endpoint.getConsumerEndpoint());
        }
    }

    private void acquireLock() {
        //TODO
    }

    @Override
    protected void doStop() throws Exception {
        try {
            stopConsumer();
        } finally {
            ServiceHelper.stopAndShutdownServices(delegateService);
        }
        super.doStop();
    }

    @Override
    protected void doResume() throws Exception {
        if (delegateService != null) {
            delegateService.resume();
        }
        super.doResume();
    }

    @Override
    protected void doSuspend() throws Exception {
        if (delegateService != null) {
            delegateService.suspend();
        }
        super.doSuspend();
    }

    private void stopConsumer() throws Exception {
        ServiceHelper.stopAndShutdownServices(delegate);
        ServiceHelper.stopAndShutdownServices(endpoint.getConsumerEndpoint());
        delegate = null;
        delegateService = null;
    }

}
