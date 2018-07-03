package com.tangzhe.mamabike.bike.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by tangzhe 2017/9/12.
 */
@Document(collection = "bike-position")
public class BikeLocation {

    private String id;

    private Long bikeNumber;

    private int status;

    private Double[] coordinates;

    private Double distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(Long bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Double[] coordinates) {
        this.coordinates = coordinates;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
