package org.turnxcel.spring.turnxcel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
@ComponentScan(basePackages = {"org.turnxcel.spring.turnxcel.controller", "org.turnxcel.turnxcel.xmlreader", "org.turnxcel.turnxcel.converter","org.turnxcel.spring.turnxcel.config"})
public class TurnXCelApplication {

    private static final Logger logger = LoggerFactory.getLogger(TurnXCelApplication.class);

    public static void main(String[] args) {
        logger.info("Starting TurnXCelApplication...");
        SpringApplication.run(TurnXCelApplication.class, args);
        try {
            System.out.println("Starting health loop");
            for(int i=0;i>=0;i++) {
                Thread.sleep(60000);
                getHealth();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    private static void getHealth() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://turnxcel.onrender.com/health"))
//                .uri(URI.create("http://localhost:8080/health"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Status code: " + response.statusCode());
//        System.out.println("Body: " + response.body());
    }

}
