package pt.brunojesus.store.productservice.command.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRestModel {

    @NotBlank(message = "Product title is a required field")
    private String title;

    @Min(value = 1, message = "Price must be greater than 1")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be greater than 1")
    @Max(value = 5, message = "Quantity must not be larger than 5")
    private Integer quantity;

}
