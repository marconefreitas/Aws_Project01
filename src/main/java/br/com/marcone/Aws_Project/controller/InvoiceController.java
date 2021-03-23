package br.com.marcone.Aws_Project.controller;


import br.com.marcone.Aws_Project.model.Invoice;
import br.com.marcone.Aws_Project.model.UrlResponse;
import br.com.marcone.Aws_Project.repository.InvoiceRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Value("${aws.s3.bucket.invoice.name}")
    private String bucketName;

    private AmazonS3 amazonS3;

    private InvoiceRepository repository;

    @Autowired
    public InvoiceController( AmazonS3 amazonS3, InvoiceRepository invoiceRepository) {
        this.amazonS3 = amazonS3;
        this.repository = invoiceRepository;
    }

    @PostMapping
    public ResponseEntity<UrlResponse> createInvoiceUrl(){
        UrlResponse url = new UrlResponse();
        Instant expriration = Instant.now().plus(Duration.ofMinutes(5));
        String id = UUID.randomUUID().toString();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(this.bucketName, id)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(Date.from(expriration));
        url.setUrl(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString());
        url.setExpirationTime(expriration.getEpochSecond());


        return new ResponseEntity<UrlResponse>(url, HttpStatus.OK);
    }

    @GetMapping
    public Iterable<Invoice> findAll(){
        return repository.findAll();
    }

    @GetMapping(path = "/customerName")
    public Iterable<Invoice> findByCustomerName(@RequestParam String customerName){
        return repository.findAllByCustomerName(customerName);
    }



}
