package com.dyes.backend.domain.product.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterRequest {
    private String productName;
    private String productDescription;
    private String cultivationMethod;
}
