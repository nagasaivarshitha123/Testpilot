package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestUpdateModel {

    @Size(max = 255, message = "Maximum length of name is {max} characters")
    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 1024, message = "Maximum length of description is {max} characters")
    @NotBlank(message = "Description is required")
    private String description;
}
