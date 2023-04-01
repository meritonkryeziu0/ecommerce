package app.services.manufacturer.models;

import app.shared.BaseAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateManufacturer {
    @NotBlank
    @Size(max = 255)
    private String name;
    @Valid
    private BaseAddress address;
}