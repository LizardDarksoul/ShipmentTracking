package com.gamergrotte.dhl.tracking.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class event {

    private LocalDateTime timestamp;

    private String statusCode;
    private String statusName;
    private String statusDesc;
}
