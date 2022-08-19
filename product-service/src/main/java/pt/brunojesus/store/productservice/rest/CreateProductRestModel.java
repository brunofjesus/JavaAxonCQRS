package pt.brunojesus.store.productservice.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRestModel {

    private String title;
    private BigDecimal price;
    private Integer quantity;

}
