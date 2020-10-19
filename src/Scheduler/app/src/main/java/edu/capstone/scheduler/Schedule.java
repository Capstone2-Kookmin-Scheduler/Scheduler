package edu.capstone.scheduler;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Schedule {
    private String name;
    private Date date;
    private Double departure_lat;
    private Double departure_lng;
    private Double arrival_lat;
    private Double arrival_lng;
    private String departure_location;
    private String arrival_location;
    private int total_time;

    public Schedule(String name, Date date, Double departure_lat, Double departure_lng, Double arrival_lat, Double arrival_lng, String departure_location, String arrival_location, int total_time){
        this.name = name;
        this.date = date;
        this.departure_lat = departure_lat;
        this.departure_lng = departure_lng;
        this.arrival_lat = arrival_lat;
        this.arrival_lng = arrival_lng;
        this.departure_location = departure_location;
        this.arrival_location = arrival_location;
        this.total_time = total_time;
    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("name",name);
        result.put("date", date);
        result.put("departure_lat",departure_lat);
        result.put("departure_lng",departure_lng);
        result.put("arrival_lat",arrival_lat);
        result.put("arrival_lng",arrival_lng);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public Double getDeparture_lat() {
        return departure_lat;
    }

    public void setDeparture_lat(Double departure_lat) {
        this.departure_lat = departure_lat;
    }

    public Double getDeparture_lng() {
        return departure_lng;
    }

    public void setDeparture_lng(Double departure_lng) {
        this.departure_lng = departure_lng;
    }

    public Double getArrival_lat() {
        return arrival_lat;
    }

    public void setArrival_lat(Double arrival_lat) {
        this.arrival_lat = arrival_lat;
    }

    public Double getArrival_lng() {
        return arrival_lng;
    }

    public void setArrival_lng(Double arrival_lng) {
        this.arrival_lng = arrival_lng;
    }


    public String getDeparture_location() {
        return departure_location;
    }

    public void setDeparture_location(String departure_location) {
        this.departure_location = departure_location;
    }

    public String getArrival_location() {
        return arrival_location;
    }

    public void setArrival_location(String arrival_location) {
        this.arrival_location = arrival_location;
    }

    public int getTotal_time() {
        return total_time;
    }

    public void setTotal_time(int total_time) {
        this.total_time = total_time;
    }
}
