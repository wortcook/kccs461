package edu.umkc.cs461.hw1.data;

import java.util.List;

/*
 * A class that represents a city. It contains the name of the city and the latitude and longitude
 */
public class City implements Comparable<City>, Measureable<City>{

    private final String name;
    private final double latitude;
    private final double longitude;

    public City(final String name,final  double latitude, final double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Used for searching by city name
    public City(final String name){
        this(name, 0, 0);
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

    /*
     * Returns the distance from this city to the other city
     * as the Euclidean distance between the two cities.
     * @param other The other city
     */
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


    /*
     * Utility method to calculate the distance through a list of cities.
     * Equal to the sum of the distances between each city in the list
     * @param cities The list of cities
     */
    public static double distanceThrough(final List<City> cities){
        double distance = 0;
        for(int i = 0; i < cities.size() - 1; i++){
            distance += cities.get(i).distanceFrom(cities.get(i+1));
        }
        return distance;
    }
}
