package com.marketlocalshops.events.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MarketEvent extends BaseEvent {
    private Long marketId;
    private String name;
    private String status;
}
