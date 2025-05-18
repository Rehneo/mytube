package com.rehneo.nexara.api.ra.impl;

import jakarta.resource.NotSupportedException;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;

import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NexaraApiManagedConnection implements ManagedConnection {
    private final NexaraApiManagedConnectionFactory mcf;
    private PrintWriter logWriter;
    private final List<ConnectionEventListener> listeners = new ArrayList<>();
    private NexaraApiConnectionImpl connectionHandle;

    public NexaraApiManagedConnection(NexaraApiManagedConnectionFactory mcf) {
        this.mcf = mcf;
    }

    @Override
    public Object getConnection(javax.security.auth.Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        connectionHandle = new NexaraApiConnectionImpl(this);
        return connectionHandle;
    }

    @Override
    public void destroy() throws ResourceException {
        cleanup();
    }

    @Override
    public void cleanup() throws ResourceException {
        if (connectionHandle != null) {
            try {
                connectionHandle.close();
            } catch (ResourceException ignored) {
            }
            connectionHandle = null;
        }
    }

    void closeHandle(NexaraApiConnectionImpl handle) {
        if (handle == this.connectionHandle) {
            ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
            event.setConnectionHandle(handle);
            for (ConnectionEventListener listener : listeners) {
                listener.connectionClosed(event);
            }
            this.connectionHandle = null;
        }
    }


    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (!(connection instanceof NexaraApiConnectionImpl)) {
            throw new ResourceException("Invalid connection type associated");
        }
        this.connectionHandle = (NexaraApiConnectionImpl) connection;
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException("XA transactions not supported");
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("Local transactions not supported");
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new ManagedConnectionMetaData() {
            @Override
            public String getEISProductName() throws ResourceException {
                return "NexaraApi RA";
            }

            @Override
            public String getEISProductVersion() throws ResourceException {
                return "1.0";
            }

            @Override
            public int getMaxConnections() throws ResourceException {
                return 0;
            }

            @Override
            public String getUserName() throws ResourceException {
                return null;
            }
        };
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NexaraApiManagedConnection that = (NexaraApiManagedConnection) o;
        return Objects.equals(mcf, that.mcf)
                && Objects.equals(logWriter, that.logWriter)
                && Objects.equals(listeners, that.listeners)
                && Objects.equals(connectionHandle, that.connectionHandle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mcf, logWriter, listeners, connectionHandle);
    }
}