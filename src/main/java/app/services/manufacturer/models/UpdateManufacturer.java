package app.services.manufacturer.models;

import app.shared.BaseAddress;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateManufacturer {
    @NotBlank
    private String name;
    @Valid
    private BaseAddress address;
}