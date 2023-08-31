package com.dyes.backend.domain.cart.repository;

import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("select c FROM Cart c join fetch c.user where c.id = :userId")
    Optional<Cart> findByUser(User user);
}
