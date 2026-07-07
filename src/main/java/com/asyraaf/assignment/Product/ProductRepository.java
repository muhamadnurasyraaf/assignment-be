package com.asyraaf.assignment.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    long countByCompanyId(UUID id);

    @Modifying
    @Query("update Product p set p.quantity = p.quantity + :delta where p.id = :id and p.quantity + :delta >= 0")
    int adjustQuantity(@Param("id") UUID id, @Param("delta") int delta);

    List<Product> findTop5ByCompanyIdOrderByCreatedAtDesc(UUID companyId);

    List<Product> findByCompanyId(UUID companyId);

    boolean existsByCreatedById(UUID userId);

}
