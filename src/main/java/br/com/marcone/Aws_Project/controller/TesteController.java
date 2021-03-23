package br.com.marcone.Aws_Project.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teste")
public class TesteController {

    private static final Logger LOG = LoggerFactory.getLogger(TesteController.class);

    @GetMapping("/dog/{name}")
    public ResponseEntity<?> dogTest(@PathVariable String name){
        LOG.info("Teste Controller - name:{}", name);

        return ResponseEntity.ok("Name: " + name);

    }

    @GetMapping("/cat/{name}")
    public ResponseEntity<?> getCat(@PathVariable String name){
        LOG.info("Teste Controller - Gato name:{}", name);

        return ResponseEntity.ok("Name: " + name);

    }
}
