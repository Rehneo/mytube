package com.rehneo.nexara.api.ra;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;

public interface NexaraApiConnection extends Connection {
    String transcribe(byte[] videoData, String apiKey) throws ResourceException;
}
