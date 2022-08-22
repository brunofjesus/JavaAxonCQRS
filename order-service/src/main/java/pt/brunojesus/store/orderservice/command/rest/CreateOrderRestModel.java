package pt.brunojesus.store.orderservice.command.rest;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderRestModel {

    @NotBlank(message = "Product Id is a required field")
    private final String productId;

    @Min(value = 1, message = "Quantity needs to be at least 1")
    private final int quantity;

    @NotBlank(message = "Address Id is a required field")
    private final String addressId;
}
