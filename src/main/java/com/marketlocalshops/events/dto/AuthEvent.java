package com.marketlocalshops.events.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthEvent extends BaseEvent {
    private Long userId;
    private String username;
    private String email;
}
