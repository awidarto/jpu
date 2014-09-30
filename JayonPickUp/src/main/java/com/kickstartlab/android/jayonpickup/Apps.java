package com.kickstartlab.android.jayonpickup;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by awidarto on 12/16/13.
 */
public class Apps extends SugarRecord<Apps> {
    public Long _id;
    public Long app_id;
    public Long merchant_id;
    public String key;
    public String application_name;

    public Apps(Context ctx){
        super(ctx);
    }

    public Apps(
        Long app_id,
        Long merchant_id,
        String key,
        String application_name
    ){
        super();
        this.app_id = app_id;
        this.merchant_id = merchant_id;
        this.key = key;
        this.application_name = application_name;
    }

    @Override
    public String toString() {
        return  application_name;
    }

    public Long getApp_id() {
        return app_id;
    }

    public void setApp_id(Long app_id) {
        this.app_id = app_id;
    }

    public Long getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Long merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }
}
