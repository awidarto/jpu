package com.kickstartlab.android.jayonpickup;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by awidarto on 1/9/14.
 */
public class CODTariff extends SugarRecord<CODTariff> {
    public Long _id;
    public Long cod_id;
    public Long app_id;
    public Long from_price;
    public Long to_price;
    public Long surcharge;

    public CODTariff(Context ctx){
        super(ctx);
    }

    public CODTariff(
            Long app_id,
            Long cod_id,
            Long from_price,
            Long to_price,
            Long surcharge
    ){
        super();
        this.app_id    = app_id;
        this.cod_id    = cod_id;
        this.from_price = from_price;
        this.to_price  = to_price;
        this.surcharge = surcharge;
    }

    public String toString(){
        return "IDR " + String.valueOf(from_price) + " - IDR " + String.valueOf(to_price) + " kg";
    }

    public Long getApp_id() {
        return app_id;
    }

    public void setApp_id(Long app_id) {
        this.app_id = app_id;
    }

    public Long getCod_id() {
        return cod_id;
    }

    public void setCod_id(Long cod_id) {
        this.cod_id = cod_id;
    }

    public Long getFrom_price() {
        return from_price;
    }

    public void setFrom_price(Long from_price) {
        this.from_price = from_price;
    }

    public Long getTo_price() {
        return to_price;
    }

    public void setTo_price(Long to_price) {
        this.to_price = to_price;
    }

    public Long getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(Long surcharge) {
        this.surcharge = surcharge;
    }
}
