package com.primetech.android.sensor.annotation;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Faisal Mohammad on 1/11/2018. email: nirzon192@gmail.com
 */

@Table(name = "Locations", id = "_id")
public class LocationModel extends Model {

    @Column(name = "latitude")
    String latitude;

    @Column(name = "longitude")
    String longitude;

    public LocationModel() {
        super();
    }

    public LocationModel(String latitude, String longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
