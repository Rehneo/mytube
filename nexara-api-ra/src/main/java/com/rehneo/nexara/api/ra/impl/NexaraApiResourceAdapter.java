package com.rehneo.nexara.api.ra.impl;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;

import javax.transaction.xa.XAResource;
import java.io.Serializable;
import java.util.Objects;

@Connector(
        description = "Nexara Api Resource Adapter",
        displayName = "NexaraApiRA",
        vendorName = "Nexara"
)
public class NexaraApiResourceAdapter implements ResourceAdapter, Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NexaraApiResourceAdapter that = (NexaraApiResourceAdapter) o;
        return Objects.equals(bootstrapContext, that.bootstrapContext);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bootstrapContext);
    }

    public NexaraApiResourceAdapter(BootstrapContext bootstrapContext) {
        this.bootstrapContext = bootstrapContext;
    }

    private static final long serialVersionUID = 1L;

    private transient BootstrapContext bootstrapContext;

    public NexaraApiResourceAdapter() {
    }

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        this.bootstrapContext = ctx;
    }

    @Override
    public void stop() {
        this.bootstrapContext = null;
    }

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) throws ResourceException {
        throw new UnsupportedOperationException("Endpoint activation is not supported by this resource adapter.");
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return null;
    }
}