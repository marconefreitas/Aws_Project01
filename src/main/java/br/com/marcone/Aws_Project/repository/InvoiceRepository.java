package br.com.marcone.Aws_Project.repository;

import br.com.marcone.Aws_Project.model.Invoice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findAllByCustomerName(String name);

}
