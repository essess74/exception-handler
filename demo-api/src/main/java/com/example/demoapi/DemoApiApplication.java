package com.example.demoapi;

import com.sun.tracing.dtrace.FunctionName;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DemoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApiApplication.class, args);
    }
}


@RestController
class TestController {


    @GetMapping("/beers/{beer}")
    public Mono<Beer> beer(@PathVariable("beer") String name) {
        return Mono.just(new Beer(name));
    }


    static class Beer {


        public Beer(String beerName) {
            if("malek".equals(beerName)){
                throw new FunctionnalException();
            }
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
    public @ResponseBody Map<String,String> handleException(){
        final Map<String,String> hello = new HashMap<>();
        hello.putIfAbsent("err","malek");
        return hello;
    }

}

class FunctionnalException extends RuntimeException{
}