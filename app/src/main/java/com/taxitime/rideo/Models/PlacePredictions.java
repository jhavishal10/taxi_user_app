package com.taxitime.rideo.Models;



import java.io.Serializable;
import java.util.ArrayList;

public class PlacePredictions implements Serializable{

    public String strSourceLatitude = "";
    public String strSourceLongitude = "";
    public String strSourceLatLng = "";
    public String strSourceAddress = "";

    public String strDestLatitude = "";
    public String strDestLongitude = "";
    public String strDestLatLng = "";
    public String strDestAddress = "";

    public ArrayList<PlaceAutoComplete> getPlaces() {
        return predictions;
    }

    public void setPlaces(ArrayList<PlaceAutoComplete> places) {
        this.predictions = places;
    }

    private ArrayList<PlaceAutoComplete> predictions;

}
