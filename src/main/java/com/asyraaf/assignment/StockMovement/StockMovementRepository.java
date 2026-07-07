package com.asyraaf.assignment.StockMovement;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    List<StockMovement> findByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    boolean existsByUserId(UUID userId);

    @Query("select sm from StockMovement sm join fetch sm.product p join fetch sm.user u "
            + "where p.company.id = :companyId order by sm.createdAt desc")
    List<StockMovement> findLatestByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);
}
