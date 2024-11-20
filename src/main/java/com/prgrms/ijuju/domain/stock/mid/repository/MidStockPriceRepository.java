package com.prgrms.ijuju.domain.stock.mid.repository;

import com.prgrms.ijuju.domain.stock.mid.entity.MidStock;
import com.prgrms.ijuju.domain.stock.mid.entity.MidStockPrice;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MidStockPriceRepository extends JpaRepository<MidStockPrice, Long> {

    @Query("SELECT p FROM MidStockPrice p WHERE p.midStock.id = :stockId")
    List<MidStockPrice> findByMidStockId(@Param("stockId") Long stockId);

    @Modifying
    @Query("DELETE FROM MidStockPrice  p WHERE p.priceDate < :date")
    void deleteOldData(@Param("date")LocalDateTime date);

    @Query("SELECT p FROM MidStockPrice p WHERE p.midStock = :stock ORDER BY p.priceDate DESC LIMIT 1")
    Optional<MidStockPrice> findLatestPrice(@Param("stock") MidStock stock);

    @Query("SELECT p FROM MidStockPrice p WHERE p.midStock = :stock ORDER BY p.priceDate DESC")
    List<MidStockPrice> findPriceHistory(@Param("stock") MidStock stock, Pageable pageable);

}
