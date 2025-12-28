package com.lingulu.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PollyService {

    private final PollyClient pollyClient;

    public PollyService(PollyClient pollyClient) {
        this.pollyClient = pollyClient;
    }

    public byte[] synthesize(String text) {

        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(text)
                .voiceId(VoiceId.JOANNA)
                .engine(Engine.NEURAL)
                .outputFormat(OutputFormat.MP3)
                .build();

        try (ResponseInputStream<SynthesizeSpeechResponse> response =
                     pollyClient.synthesizeSpeech(request)) {

            return toByteArray(response);

        } catch (IOException e) {
            throw new RuntimeException("Failed to synthesize speech", e);
        }
    }

    private byte[] toByteArray(ResponseInputStream<?> input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}