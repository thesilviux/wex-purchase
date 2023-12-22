package com.wex.gateways.silvio.purchase.repository;

import org.springframework.data.repository.CrudRepository;

import com.wex.gateways.silvio.purchase.model.Purchase;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {
}
