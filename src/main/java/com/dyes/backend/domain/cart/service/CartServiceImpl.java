package com.dyes.backend.domain.cart.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.request.ContainProductOptionRequest;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    final private CartRepository cartRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ContainProductOptionRepository containProductOptionRepository;
    final private AuthenticationService authenticationService;
    public void containProductIntoCart(ContainProductRequestForm requestForm) throws NullPointerException {
        log.info("containProductIntoCart start");
        // 유저가 장바구니가 있는지 확인
        User user = authenticationService.findUserByUserToken(requestForm.getUserToken());

        Optional<Cart> maybeCart = cartRepository.findByUser(user);

        final Cart cart;

        if (maybeCart.isEmpty()) {
            // 없으면 생성
            log.info("maybeCart isEmpty");
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
            log.info("save new cart");
        } else {
            // 있으면 가져오기
            cart = maybeCart.get();
            log.info("cart: " + cart);
        }
        // 옵션 리스트에 옵션 아이디와 옵션 갯수 넣기
        for (ContainProductOptionRequest request : requestForm.getRequestList()) {
            log.info("ContainProductOptionRequest: " + request);
            Optional<ProductOption> maybeOption = productOptionRepository.findById(request.getProductOptionId());
            if (maybeOption.isEmpty()) {
                log.info("this option doesn't exist");
                log.info("containProductIntoCart end");
                return;
            }
            ProductOption productOption = maybeOption.get();
            log.info("ProductOption: " + productOption);
            Optional<List<ContainProductOption>> savedOptionList = containProductOptionRepository.findAllByCart(cart);
            if (savedOptionList.isEmpty()) {
                log.info("savedOptionList isEmpty");
                ContainProductOption containProductOption = ContainProductOption.builder()
                        .productOption(productOption)
                        .cart(cart)
                        .optionCount(request.getOptionCount())
                        .build();
                containProductOptionRepository.save(containProductOption);
                log.info("containProductIntoCart end");
                return;
            }
            Optional<ContainProductOption> result = savedOptionList.get().stream()
                    .filter(option -> option.getId().equals(request.getProductOptionId()))
                    .findFirst();
            // 장바구니에 동일한 옵션이 있어서 수량만 변경할 때
            if (result.isPresent()) {
                log.info("result: " + result);
                ContainProductOption containProductOption = result.get();
                if (containProductOption.getCart() == cart){
                    containProductOption.setOptionCount(request.getOptionCount());
                    containProductOptionRepository.save(containProductOption);
                    log.info("containProductIntoCart end");
                }
            }
        }
    }
}
