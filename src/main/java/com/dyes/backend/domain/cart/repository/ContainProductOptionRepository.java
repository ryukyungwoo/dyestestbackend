package com.dyes.backend.domain.cart.repository;

import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContainProductOptionRepository extends JpaRepository<ContainProductOption, Long> {
    Optional<List<ContainProductOption>> findAllByCart (Cart cart);
}
