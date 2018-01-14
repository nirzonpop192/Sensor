package com.primetech.android.sensor.annotation;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Faisal Mohammad on 1/11/2018. email: nirzon192@gmail.com
 */

@Table(name = "Locations")
public class LocationModel extends Model {

    @Column(name = "accuracy")
    String accuracy;

    @Column(name = "latitude")
    String latitude;

    @Column(name = "longitude")
    String longitude;

    public LocationModel() {
        super();
    }

    public LocationModel(String accuracy, String latitude, String longitude) {
        super();
        this.accuracy = accuracy;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static List<LocationModel> getAll() {
        return new Select().from(LocationModel.class)
                .execute();
    }

    public String getAccuracy() {
        return accuracy;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
