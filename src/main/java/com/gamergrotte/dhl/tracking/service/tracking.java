package com.gamergrotte.dhl.tracking.service;

import com.gamergrotte.dhl.tracking.exception.AuthorizationException;
import com.gamergrotte.dhl.tracking.exception.NotFoundException;
import com.gamergrotte.dhl.tracking.exception.TrackingStandardException;
import com.gamergrotte.dhl.tracking.object.event;
import com.gamergrotte.dhl.tracking.object.shipment;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class tracking {

    @Setter
    private String authKey;

    public tracking(String authKey) {
        this.authKey = authKey;
    }

    public shipment track(String trackingNum) throws URISyntaxException, IOException, InterruptedException, AuthorizationException, NotFoundException, TrackingStandardException {
        shipment shipment;

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest;

        httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api-eu.dhl.com/track/shipments?trackingNumber=" + trackingNum))
                .setHeader("DHL-API-Key", authKey)
                .GET()
                .build();

        HttpResponse httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            JSONObject object = new JSONObject(httpResponse.body().toString());

            JSONObject shipmentJSON = object.getJSONArray("shipments").getJSONObject(0);

            JSONArray array = shipmentJSON.getJSONArray("events");
            List<event> events = processEvents(array);
            events.sort(Comparator.comparing(event::getTimestamp));

            event event = parseEvent(shipmentJSON.getJSONObject("status"));

            String originCountry = shipmentJSON.getJSONObject("origin").getJSONObject("address").getString("countryCode");
            String destinationCountry = shipmentJSON.getJSONObject("destination").getJSONObject("address").getString("countryCode");

            shipment = new shipment(shipmentJSON.getString("id"), shipmentJSON.getString("service"), originCountry, destinationCountry, event, events);

            return shipment;
        } else if (httpResponse.statusCode() == 401) {
            throw new AuthorizationException("Authkey " + authKey + " is not authorized for this resource");
        } else if (httpResponse.statusCode() == 404) {
            throw new NotFoundException("Shipment with tracking number " + trackingNum + " not found");
        } else {
            throw new TrackingStandardException();
        }
    }

    private List<event> processEvents(JSONArray array) {
        List<event> events = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            event event = parseEvent(object);

            events.add(event);
        }

        return events;
    }

    private event parseEvent(JSONObject object) {
        LocalDateTime dateTime = LocalDateTime.parse(object.getString("timestamp"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return new event(dateTime, object.getString("statusCode"), object.getString("status"), object.getString("description"));
    }
}
