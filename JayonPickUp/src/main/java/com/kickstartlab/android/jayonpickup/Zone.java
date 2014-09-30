package com.kickstartlab.android.jayonpickup;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by awidarto on 1/17/14.
 */
public class Zone extends SugarRecord<Zone> {
    public String district;
    public String city;
    public String province;
    public String country;
    public Integer is_on;

    public Zone(Context ctx){
        super(ctx);
    }

    public Zone(
        String district,
        String city,
        String province,
        String country,
        Integer is_on
    ){
        super();
        this.district = district;
        this.city    = city;
        this.province = province;
        this.country = country;
        this.is_on   = is_on;
    }

    public String toString(){
        return district;
    }


    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getIs_on() {
        return is_on;
    }

    public void setIs_on(Integer is_on) {
        this.is_on = is_on;
    }
}
