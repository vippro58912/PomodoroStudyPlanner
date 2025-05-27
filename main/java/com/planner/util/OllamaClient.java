package com.planner.util;

import java.net.http.*;
import java.net.URI;
import java.io.IOException;
import java.util.stream.Stream;

public class OllamaClient {
    private static final String ENDPOINT = "http://localhost:11434/api/generate";
    private static final String MODEL = "llama2";

    public static String getStudyTip() throws IOException, InterruptedException {
        String json = """
        {
          "model": "%s",
          "prompt": "Give me a study tip to improve focus and productivity."
        }
        """.formatted(MODEL);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<Stream<String>> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofLines());

        // Json that have "respone"
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

        return result.toString().isEmpty() ? "Cant receive AI content." : result.toString();
    }


    private static String extractResponseText(String json) {
        int start = json.indexOf("\"response\":\"") + 12;
        int end = json.indexOf("\"", start);
        if (start > 11 && end > start) {
            return json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
        }
        return "Cant receive AI content.";
    }
}
