package com.rehneo.nexara.api.ra.impl;

import com.rehneo.nexara.api.ra.NexaraApiConnection;
import com.rehneo.nexara.api.ra.NexaraApiConnectionFactory;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionSpec;
import jakarta.resource.cci.RecordFactory;
import jakarta.resource.cci.ResourceAdapterMetaData;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ManagedConnectionFactory;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.util.Objects;

public class NexaraApiConnectionFactoryImpl implements NexaraApiConnectionFactory {
    private final ManagedConnectionFactory managedConnectionFactory;
    private final ConnectionManager connectionManager;
    private Reference reference;

    public NexaraApiConnectionFactoryImpl(ManagedConnectionFactory managedConnectionFactory, ConnectionManager connectionManager) {
        this.managedConnectionFactory = managedConnectionFactory;
        this.connectionManager = connectionManager;
    }

    @Override
    public NexaraApiConnection getConnection() throws ResourceException {
        if (connectionManager == null) {
            throw new ResourceException("ConnectionManager is not available. Cannot provide a managed connection.");
        }
        return (NexaraApiConnection) connectionManager.allocateConnection(
                managedConnectionFactory,
                null
        );
    }

    @Override
    public Reference getReference() throws NamingException {
        return reference;
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        throw new ResourceException("getMetaData not supported");
    }

    public RecordFactory getRecordFactory() throws ResourceException {
        throw new ResourceException("getRecordFactory not supported");
    }

    public Connection getConnection(ConnectionSpec properties) throws ResourceException {
        throw new ResourceException("getConnection with ConnectionSpec not supported");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NexaraApiConnectionFactoryImpl that = (NexaraApiConnectionFactoryImpl) o;
        return Objects.equals(
                managedConnectionFactory,
                that.managedConnectionFactory
        ) && Objects.equals(
                connectionManager,
                that.connectionManager
        ) && Objects.equals(
                reference,
                that.reference
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(managedConnectionFactory, connectionManager, reference);
    }
}