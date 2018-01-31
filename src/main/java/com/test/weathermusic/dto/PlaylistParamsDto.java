package com.test.weathermusic.dto;

import com.test.weathermusic.validator.annotation.PlaylistParamsConstraint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Created by Eduardo on 29/01/2018.
 */
@PlaylistParamsConstraint
public class PlaylistParamsDto {

    private String city;
    private Double lat;
    private Double lon;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public PlaylistParamsDto() {

    }

    public PlaylistParamsDto(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PlaylistParamsDto that = (PlaylistParamsDto) o;

        return new EqualsBuilder()
                .append(city, that.city)
                .append(lat, that.lat)
                .append(lon, that.lon)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(city)
                .append(lat)
                .append(lon)
                .toHashCode();
    }
    /*
    *  @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("OrderSyncTransactionVO [")//
                .append("transactionId=")//
                .append(transactionId)//
                .append(",transactionCode=\"")//
                .append(transactionCode).append("\"")//
                .append(",paymentDate=")//
                .append(paymentDate)//
                .append(",priority=")//
                .append(priority)//
                .append("]");
        return builder.toString();
    }*/

    @Override
    public String toString() {
        return "PlaylistParamsDto{" +
                "city='" + city + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
