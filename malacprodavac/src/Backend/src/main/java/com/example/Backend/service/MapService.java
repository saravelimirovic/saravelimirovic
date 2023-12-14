package com.example.Backend.service;

import com.example.Backend.dto.CoordinatesDTO;
import com.example.Backend.dto.RouteMapResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

@Service
public class MapService {
    public CoordinatesDTO getCoordinates(String city, String street, String number, String postalCode) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        String query = String.format("%s %s %s", city, street, number);
        String encodedQuery = URLEncoder.encode(query, "UTF-8"); // Enkodiranje upita

        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedQuery;

        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);

        // Obrada odgovora i dobijanje koordinata iz JSON-a
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());

        if (rootNode.isArray() && rootNode.size() > 0) {
            JsonNode firstResult = rootNode.get(0);
            double latitude = firstResult.get("lat").asDouble();
            double longitude = firstResult.get("lon").asDouble();

            return new CoordinatesDTO(longitude, latitude);
        }
        else {
            String query1 = String.format("%s %s", city, postalCode);
            String encodedQuery1 = URLEncoder.encode(query1, "UTF-8"); // Enkodiranje upita
            String url1 = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedQuery1;

            HttpGet request1 = new HttpGet(url1);
            HttpResponse response1 = httpClient.execute(request1);

            // Obrada odgovora i dobijanje koordinata iz JSON-a
            ObjectMapper objectMapper1 = new ObjectMapper();
            JsonNode rootNode1 = objectMapper1.readTree(response1.getEntity().getContent());

            JsonNode firstResult = rootNode1.get(0);
            double latitude = firstResult.get("lat").asDouble();
            double longitude = firstResult.get("lon").asDouble();

            return new CoordinatesDTO(longitude, latitude);
        }
    }

    public RouteMapResponseDTO getRouteCoordinates(String startCity, String startPostalCode, String endCity, String endPostalCode) throws Exception {
        HttpClient httpClient1 = HttpClients.createDefault();
        String query1 = String.format("%s %s", startCity, startPostalCode);
        String encodedQuery1 = URLEncoder.encode(query1, "UTF-8"); // Enkodiranje upita
        String url1 = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedQuery1;

        HttpGet request1 = new HttpGet(url1);
        HttpResponse response1 = httpClient1.execute(request1);

        // Obrada odgovora i dobijanje koordinata iz JSON-a
        ObjectMapper objectMapper1 = new ObjectMapper();
        JsonNode rootNode1 = objectMapper1.readTree(response1.getEntity().getContent());

        JsonNode firstResult = rootNode1.get(0);
        double latitude1 = firstResult.get("lat").asDouble();
        double longitude1 = firstResult.get("lon").asDouble();

        HttpClient httpClient2 = HttpClients.createDefault();
        String query2 = String.format("%s %s", endCity, endPostalCode);
        String encodedQuery2 = URLEncoder.encode(query2, "UTF-8"); // Enkodiranje upita
        String url2 = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedQuery2;

        HttpGet request2 = new HttpGet(url2);
        HttpResponse response2 = httpClient2.execute(request2);

        // Obrada odgovora i dobijanje koordinata iz JSON-a
        ObjectMapper objectMapper2 = new ObjectMapper();
        JsonNode rootNode2 = objectMapper2.readTree(response2.getEntity().getContent());

        JsonNode firstResult2 = rootNode2.get(0);
        double latitude2 = firstResult2.get("lat").asDouble();
        double longitude2 = firstResult2.get("lon").asDouble();

        return new RouteMapResponseDTO(longitude1, latitude1, longitude2, latitude2);
    }
}
