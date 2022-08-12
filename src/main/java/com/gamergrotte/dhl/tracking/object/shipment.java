package com.gamergrotte.dhl.tracking.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class shipment {

    private String id;

    private String service;

    private String originCountry;
    private String destinationCountry;

    private event currentStatus;

    private List<event> historyStatus;
}
