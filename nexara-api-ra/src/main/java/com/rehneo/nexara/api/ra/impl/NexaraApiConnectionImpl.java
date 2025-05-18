package com.rehneo.nexara.api.ra.impl;

import com.rehneo.nexara.api.ra.NexaraApiConnection;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.ConnectionMetaData;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class NexaraApiConnectionImpl implements NexaraApiConnection {
    private final NexaraApiManagedConnection managedConnection;

    public NexaraApiConnectionImpl(NexaraApiManagedConnection managedConnection) {
        this.managedConnection = managedConnection;
    }

    @Override
    public String transcribe(byte[] videoData, String apiKey) throws ResourceException {
        String boundary = "----NexaraBoundary" + System.currentTimeMillis();
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            URL url = new URL("https://api.nexara.ru/api/v1/audio/transcriptions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                PrintWriter writer = new PrintWriter(os, true);

                writer.append(twoHyphens).append(boundary).append(lineEnd);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"video.mp4\"").append(lineEnd);
                writer.append("Content-Type: video/mp4").append(lineEnd);
                writer.append(lineEnd).flush();
                os.write(videoData);
                os.flush();
                writer.append(lineEnd).flush();

                writer.append(twoHyphens).append(boundary).append(lineEnd);
                writer.append("Content-Disposition: form-data; name=\"response_format\"").append(lineEnd);
                writer.append(lineEnd);
                writer.append("json").append(lineEnd).flush();

                writer.append(twoHyphens).append(boundary).append(twoHyphens).append(lineEnd);
                writer.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return parseTranscriptionText(response.toString());
                }
            } else {
                throw new ResourceException("Failed to transcribe: HTTP " + responseCode);
            }
        } catch (IOException e) {
            throw new ResourceException("Error during transcription: " + e.getMessage(), e);
        }
    }

    private String parseTranscriptionText(String jsonResponse) throws ResourceException {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getString("text");
        } catch (JSONException e) {
            throw new ResourceException("Failed to parse transcription response: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws ResourceException {
    }

    @Override
    public jakarta.resource.cci.Interaction createInteraction() throws ResourceException {
        throw new ResourceException("Interaction not supported");
    }

    @Override
    public jakarta.resource.cci.LocalTransaction getLocalTransaction() throws ResourceException {
        throw new ResourceException("LocalTransaction not supported");
    }

    @Override
    public jakarta.resource.cci.ResultSetInfo getResultSetInfo() throws ResourceException {
        throw new ResourceException("ResultSetInfo not supported");
    }

    @Override
    public ConnectionMetaData getMetaData() throws ResourceException {
        throw new ResourceException("MetaData not supported");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NexaraApiConnectionImpl that = (NexaraApiConnectionImpl) o;
        return Objects.equals(managedConnection, that.managedConnection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managedConnection);
    }
}
