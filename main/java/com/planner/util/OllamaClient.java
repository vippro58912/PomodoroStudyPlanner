package com.planner.util;

import java.net.http.*;
import java.net.URI;
import java.io.IOException;
import java.util.stream.Stream;

public class OllamaClient {
//    Set url to call the local AI using "llma2"
    private static final String ENDPOINT = "http://localhost:11434/api/generate";
    private static final String MODEL = "llama2";

    // input prompt from UI
    public static String getResponseFromPrompt(String prompt) throws IOException, InterruptedException {
        String json = """
    {
      "model": "%s",
      "prompt": "%s"
    }
    """.formatted(MODEL, prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<Stream<String>> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofLines());

        StringBuilder result = new StringBuilder();
        response.body().forEach(line -> {
            int start = line.indexOf("\"response\":\"") + 12;
            int end = line.indexOf("\"", start);
            if (start > 11 && end > start) {
                String chunk = line.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
                result.append(chunk);
            }
        });

        return result.toString().isEmpty() ? "Can't receive AI content." : result.toString();
    }

}
