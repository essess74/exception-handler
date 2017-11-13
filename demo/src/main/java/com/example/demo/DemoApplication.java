package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@SpringBootApplication
public class DemoApplication {


    @Bean
    WebClient webClient() {
        return WebClient.create("http://localhost:8081/beers");
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@RestController
class TestController {

    @Autowired
    private WebClient webClient;

    @GetMapping("/beers/{beer}")
    public @ResponseBody
    Mono<Beer> getBeer(@PathVariable String beer) {
        return webClient.get().uri("/" + beer).exchange().flatMap(clientResponse -> {
            if (clientResponse.statusCode().is4xxClientError()) {
                throw new FunctionnalException(clientResponse.bodyToMono(Map.class));
            }
            return clientResponse.bodyToMono(Beer.class);
        });
    }


    static class Beer {
        public Beer() {
        }

        public Beer(String beerName) {
            this.beerName = beerName;
        }

        private String beerName;

        public String getBeerName() {
            return beerName;
        }

        public void setBeerName(String beerName) {
            this.beerName = beerName;
        }
    }
}

@ControllerAdvice
class ExceptionHandling {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = FunctionnalException.class)
    public @ResponseBody
    Mono<Error> handleException(final FunctionnalException fe) {
        return fe.getEr().map(map -> new Error(400, (String) map.get("err")));
    }
}

class Error {

    int code;
    String message;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

class FunctionnalException extends RuntimeException {

    private final Mono<Map> er;

    public FunctionnalException(Mono<Map> error) {
        er = error;
    }

    public Mono<Map> getEr() {
        return er;
    }
}