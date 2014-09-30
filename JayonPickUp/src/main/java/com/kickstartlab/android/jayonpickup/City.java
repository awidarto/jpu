package com.kickstartlab.android.jayonpickup;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by awidarto on 1/12/14.
 */
public class City extends SugarRecord<City> {
    public String name;

    public City(Context ctx){
        super(ctx);
    }

    public City(String name){
        super();
        this.name = name;
    }

    public String toString(){
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
