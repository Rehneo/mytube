package com.rehneo.nexara.api.ra;

import jakarta.resource.Referenceable;
import jakarta.resource.ResourceException;

import java.io.Serializable;

public interface NexaraApiConnectionFactory extends Serializable, Referenceable {
    NexaraApiConnection getConnection() throws ResourceException;
}
