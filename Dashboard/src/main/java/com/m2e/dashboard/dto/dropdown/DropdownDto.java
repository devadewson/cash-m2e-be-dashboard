package com.m2e.dashboard.dto.dropdown;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropdownDto implements Serializable {
    String code;
    String description;
}
