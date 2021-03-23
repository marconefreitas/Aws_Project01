package br.com.marcone.Aws_Project.controller;


import br.com.marcone.Aws_Project.enums.EventType;
import br.com.marcone.Aws_Project.model.Product;
import br.com.marcone.Aws_Project.repository.ProductRepository;
import br.com.marcone.Aws_Project.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductRepository productRepository;
    private ProductPublisher publisher;

    @Autowired
    public ProductController(ProductRepository productRepository,
                             ProductPublisher publisher){
        this.productRepository = productRepository;
        this.publisher = publisher;
    }

    @GetMapping
    public Iterable<Product> findAll(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id){
        Optional<Product> optProd = productRepository.findById(id);
        if (optProd.isPresent()) {
            return new ResponseEntity<>(optProd.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product){
        Product productSaved = productRepository.save(product);
        publisher.publishProductEvent(productSaved, EventType.PRODUCT_CREATED, "marconecreate");
        return new ResponseEntity<>(productSaved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProd(@PathVariable("id") long id,
                                              @RequestBody Product prod){
        if (productRepository.existsById(id) ){
            prod.setId(id);
            Product productUpdated = productRepository.save(prod);
            publisher.publishProductEvent(productUpdated, EventType.PRODUCT_UPDATE, "marconeupdate");
            return  new ResponseEntity<>(productUpdated, HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id ){
        Optional<Product> opt = productRepository.findById(id);
        if (opt.isPresent()){
            Product prdDel = opt.get();
            productRepository.delete(prdDel);
            publisher.publishProductEvent(prdDel, EventType.PRODUCT_DELETED, "marconedelete");

            return new ResponseEntity<>(prdDel, HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/porcodigo")
    public ResponseEntity<Product> findByCode(@RequestParam String code){
        Optional<Product> prod = productRepository.findByCode(code);
        if(prod.isPresent()){
            return new ResponseEntity<>(prod.get(), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }

}
