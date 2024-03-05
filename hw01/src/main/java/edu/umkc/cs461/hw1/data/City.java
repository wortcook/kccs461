package edu.umkc.cs461.hw1.data;

public class City implements Comparable<City>, Measureable<City>{

    private final String name;
    private final double latitude;
    private final double longitude;

    public City(final String name,final  double latitude, final double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public double distanceFrom(City other) {
        if(null == other){
            return Double.POSITIVE_INFINITY;
        }else{
            double latDiff = this.latitude - other.latitude;
            double longDiff = this.longitude - other.longitude;
            return Math.sqrt(latDiff * latDiff + longDiff * longDiff);
        }
    }

    @Override
    public int compareTo(City o) {
        if(null == o){
            return 1;
        }
        return this.name.compareTo(o.name);
    }

    public int hashCode(){
        return this.name.hashCode();
    }

    public boolean equals(Object o){
        if(null == o){
            return false;
        }
        if(this == o){
            return true;
        }
        if(o instanceof City){
            City other = (City)o;
            return this.name.equals(other.name);
        }
        return false;
    }
}
