package com.rehneo.nexara.api.ra.impl;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class NexaraApiManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private ResourceAdapter resourceAdapter;
    private PrintWriter logWriter;

    public NexaraApiManagedConnectionFactory() {
    }

    public NexaraApiManagedConnectionFactory(ResourceAdapter resourceAdapter) {
        this.resourceAdapter = resourceAdapter;
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        return new NexaraApiConnectionFactoryImpl(this, cxManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new NexaraApiConnectionFactoryImpl(this, null); // Или бросить исключение, если не поддерживается
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        return new NexaraApiManagedConnection(this);
    }

    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        for (Object obj : connectionSet) {
            if (obj instanceof NexaraApiManagedConnection) {
                return (NexaraApiManagedConnection) obj;
            }
        }
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return resourceAdapter;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        if (!(ra instanceof NexaraApiResourceAdapter)) {
            throw new ResourceException("ResourceAdapter must be of type NexaraApiResourceAdapter");
        }
        this.resourceAdapter = ra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NexaraApiManagedConnectionFactory that = (NexaraApiManagedConnectionFactory) o;
        return Objects.equals(resourceAdapter, that.resourceAdapter) && Objects.equals(logWriter, that.logWriter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceAdapter, logWriter);
    }
}