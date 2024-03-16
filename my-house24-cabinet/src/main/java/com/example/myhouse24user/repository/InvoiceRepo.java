package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @Async
    CompletableFuture<List<Invoice>> findInvoicesByApartmentAndCreationDateBetween(Apartment apartment, Instant dateFrom, Instant dateTo);

    List<Invoice> findInvoicesByApartment(Apartment apartment);
}
