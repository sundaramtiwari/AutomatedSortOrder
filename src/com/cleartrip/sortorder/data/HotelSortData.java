package com.cleartrip.sortorder.data;

import java.io.Serializable;
import java.util.Date;

/**
 * HotelSortData
 * @author sundaramtiwari
 *
 */
public final class HotelSortData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hotelId;
    private String city;
    private int noOfBookings;
    private int roomNights;
    private int roomNightsWithCB;
    private double discount;
    private double markUp;
    private Date bookingDate;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bookingDate == null) ? 0 : bookingDate.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        long temp;
        temp = Double.doubleToLongBits(discount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((hotelId == null) ? 0 : hotelId.hashCode());
        temp = Double.doubleToLongBits(markUp);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + roomNights;
        result = prime * result + roomNightsWithCB;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HotelSortData other = (HotelSortData) obj;
        if (bookingDate == null) {
            if (other.bookingDate != null)
                return false;
        } else if (!bookingDate.equals(other.bookingDate))
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (Double.doubleToLongBits(discount) != Double.doubleToLongBits(other.discount))
            return false;
        if (hotelId == null) {
            if (other.hotelId != null)
                return false;
        } else if (!hotelId.equals(other.hotelId))
            return false;
        if (Double.doubleToLongBits(markUp) != Double.doubleToLongBits(other.markUp))
            return false;
        if (roomNights != other.roomNights)
            return false;
        if (roomNightsWithCB != other.roomNightsWithCB)
            return false;
        return true;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String cityId) {
        this.city = cityId;
    }

    public int getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(int roomNights) {
        this.roomNights = roomNights;
    }

    public int getRoomNightsWithCB() {
        return roomNightsWithCB;
    }

    public void setRoomNightsWithCB(int roomNightsWithCB) {
        this.roomNightsWithCB = roomNightsWithCB;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getMarkUp() {
        return markUp;
    }

    public void setMarkUp(double markUp) {
        this.markUp = markUp;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getNoOfBookings() {
        return noOfBookings;
    }

    public void setNoOfBookings(int noOfBookings) {
        this.noOfBookings = noOfBookings;
    }

}
