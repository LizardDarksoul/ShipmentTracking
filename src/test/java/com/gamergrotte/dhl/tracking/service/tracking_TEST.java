package com.gamergrotte.dhl.tracking.service;

import com.gamergrotte.dhl.tracking.exception.AuthorizationException;
import com.gamergrotte.dhl.tracking.exception.NotFoundException;
import com.gamergrotte.dhl.tracking.exception.TrackingStandardException;
import com.gamergrotte.dhl.tracking.object.shipment;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class tracking_TEST {

    private tracking tracking;

    /* Set your authkey here for testing */
    private String authkey = "";

    @BeforeEach
    public void before() {
        tracking = new tracking(authkey);
    }

    @AfterEach
    public void after() {

    }

    @Test
    public void tracking() throws AuthorizationException, NotFoundException, URISyntaxException, IOException, TrackingStandardException, InterruptedException {
        String trackingnum = "00340434668060506568";
        shipment shipment = tracking.track(trackingnum);

        Assertions.assertEquals(trackingnum, shipment.getId());
    }

}
