package br.com.marcone.Aws_Project.repository;

import br.com.marcone.Aws_Project.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByCode(String code);

}
