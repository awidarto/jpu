package com.kickstartlab.android.jayonpickup;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by awidarto on 1/9/14.
 */
public class DOTariff extends SugarRecord<DOTariff>{

    public Long _id;
    public Long do_id;
    public Long kg_from;
    public Long kg_to;
    public Long calculated_kg;
    public Long tariff_kg;
    public Long total;
    public Long app_id;

    public DOTariff(Context ctx){
        super(ctx);
    }

    public DOTariff(
        Long do_id,
        Long kg_from,
        Long kg_to,
        Long calculated_kg,
        Long tariff_kg,
        Long total,
        Long app_id
    ){
        super();
        this.do_id = do_id;
        this.kg_from = kg_from;
        this.kg_to = kg_to;
        this.calculated_kg = calculated_kg;
        this.tariff_kg = tariff_kg;
        this.total = total;
        this.app_id = app_id;
    }

    public String toString(){
        return String.valueOf(kg_from) + " kg - " + String.valueOf(kg_to) + " kg";
    }

    public Long getDo_id() {
        return do_id;
    }

    public void setDo_id(Long do_id) {
        this.do_id = do_id;
    }

    public Long getKg_from() {
        return kg_from;
    }

    public void setKg_from(Long kg_from) {
        this.kg_from = kg_from;
    }

    public Long getKg_to() {
        return kg_to;
    }

    public void setKg_to(Long kg_to) {
        this.kg_to = kg_to;
    }

    public Long getCalculated_kg() {
        return calculated_kg;
    }

    public void setCalculated_kg(Long calculated_kg) {
        this.calculated_kg = calculated_kg;
    }

    public Long getTariff_kg() {
        return tariff_kg;
    }

    public void setTariff_kg(Long tariff_kg) {
        this.tariff_kg = tariff_kg;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getApp_id() {
        return app_id;
    }

    public void setApp_id(Long app_id) {
        this.app_id = app_id;
    }
}
