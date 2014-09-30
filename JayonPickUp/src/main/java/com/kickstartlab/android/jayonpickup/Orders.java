package com.kickstartlab.android.jayonpickup;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by awidarto on 12/15/13.
 */
public class Orders extends SugarRecord<Orders>{
    public Long _id;
    public Long order_id;
    public Long merchant_id;
    public Long app_id;
    public String trx_id;
    public String create_time;
    public String create_datetime;
    public String assignment_datetime;
    public String sync_time;
    public String sync_status;
    public String delivery_type;
    public String buyer_name;
    public String recipient_name;
    public String buyerdeliveryzone;
    public String buyerdeliverycity;
    public String phone;
    public String mobile1;
    public String mobile2;
    public String width;
    public String height;
    public String length;
    public String weight;
    public Long actual_weight;

    public String unit_description;
    public Long unit_price;
    public Integer unit_quantity;
    public Long unit_total;
    public Long deliverycost;
    public Long codsurcharge;
    public String pic_address;
    public String pic_1;
    public String pic_2;
    public String pic_3;
    public String pic_4;
    public String pickup_status;

    public String delivery_id;


    public Orders(Context ctx){
        super(ctx);
    }

    public Orders(
            Long order_id,
            Long merchant_id,
            Long app_id,
            String trx_id,
            String create_time,
            String create_datetime,
            String sync_time,
            String sync_status,
            String delivery_type,
            String buyer_name,
            String recipient_name,
            String buyerdeliveryzone,
            String buyerdeliverycity,
            String phone,
            String mobile1,
            String mobile2,
            String width,
            String height,
            String length,
            String weight,
            Long actual_weight,
            String unit_description,
            Long unit_price,
            Integer unit_quantity,
            Long unit_total,
            Long deliverycost,
            Long codsurcharge,
            String pic_address,
            String pic_1,
            String pic_2,
            String pic_3,
            String pic_4,
            String pickup_status

    ){
        super();
        this.order_id = order_id;
        this.merchant_id = merchant_id;
        this.app_id = app_id;
        this.trx_id = trx_id;
        this.create_time = create_time;
        this.create_datetime = create_datetime;
        this.sync_time  = sync_time;
        this.sync_status = sync_status;
        this.delivery_type = delivery_type;
        this.buyer_name = buyer_name;
        this.recipient_name = recipient_name;
        this.buyerdeliveryzone = buyerdeliveryzone;
        this.buyerdeliverycity = buyerdeliverycity;
        this.phone = phone;
        this.mobile1 = mobile1;
        this.mobile2 = mobile2;
        this.width = width;
        this.height = height;
        this.length = length;
        this.weight = weight;
        this.actual_weight = actual_weight;
        this.unit_description = unit_description;
        this.unit_price = unit_price;
        this.unit_quantity = unit_quantity;
        this.unit_total = unit_total;
        this.deliverycost = deliverycost;
        this.codsurcharge = codsurcharge;
        this.pic_address = pic_address;
        this.pic_1 = pic_1;
        this.pic_2 = pic_2;
        this.pic_3 = pic_3;
        this.pic_4 = pic_4;
        this.pickup_status = pickup_status;

    }



    public Orders(
            Long merchant_id,
            Long app_id,
            String trx_id,
            String delivery_type,
            String buyerdeliveryzone,
            String buyerdeliverycity,
            String buyer_name,
            String recipient_name,
            String weight,
            Long actual_weight,
            Long unit_price,
            Long deliverycost,
            Long codsurcharge,
            String pic_address,
            String pic_1,
            String pic_2,
            String pic_3
    ){
        super();

        String now = String.valueOf(System.currentTimeMillis() / 1000 );

        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Log.i("orders time", now);

        Log.i("order date", today);

        this.merchant_id = merchant_id;
        this.app_id = app_id;
        this.trx_id = trx_id;
        this.create_time = now;
        this.create_datetime = today;
        this.delivery_type = delivery_type;
        this.buyerdeliveryzone = buyerdeliveryzone;
        this.buyerdeliverycity = buyerdeliverycity;
        this.buyer_name = buyer_name;
        this.recipient_name = recipient_name;
        this.weight = weight;
        this.actual_weight = actual_weight;
        this.unit_price = unit_price;
        this.deliverycost = deliverycost;
        this.codsurcharge = codsurcharge;
        this.pic_address = pic_address;
        this.pic_1 = pic_1;
        this.pic_2 = pic_2;
        this.pic_3 = pic_3;

        this.sync_status = "unsync";
        this.unit_description = "Paket PickUp";
        this.unit_quantity = 1;
        this.unit_total = unit_price;

    }

    public String toString(){
        if( "".equalsIgnoreCase(unit_description) ){
            return "Paket untuk " + buyer_name;
        }else{
            return unit_description + " - " + buyer_name;
        }
    }

    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Long getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Long merchant_id) {
        this.merchant_id = merchant_id;
    }

    public Long getApp_id() {
        return app_id;
    }

    public void setApp_id(Long app_id) {
        this.app_id = app_id;
    }

    public String getTrx_id() {
        return trx_id;
    }

    public void setTrx_id(String trx_id) {
        this.trx_id = trx_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCreate_datetime() {
        return create_datetime;
    }

    public void setCreate_datetime(String create_datetime) {
        this.create_datetime = create_datetime;
    }

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }

    public String getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public String getBuyerdeliveryzone() {
        return buyerdeliveryzone;
    }

    public void setBuyerdeliveryzone(String buyerdeliveryzone) {
        this.buyerdeliveryzone = buyerdeliveryzone;
    }

    public String getBuyerdeliverycity() {
        return buyerdeliverycity;
    }

    public void setBuyerdeliverycity(String buyerdeliverycity) {
        this.buyerdeliverycity = buyerdeliverycity;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Long getActual_weight() {
        return actual_weight;
    }

    public void setActual_weight(Long actual_weight) {
        this.actual_weight = actual_weight;
    }

    public String getUnit_description() {
        return unit_description;
    }

    public void setUnit_description(String unit_description) {
        this.unit_description = unit_description;
    }

    public Long getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Long unit_price) {
        this.unit_price = unit_price;
    }

    public Long getDeliverycost() {
        return deliverycost;
    }

    public void setDeliverycost(Long deliverycost) {
        this.deliverycost = deliverycost;
    }

    public Long getCodsurcharge() {
        return codsurcharge;
    }

    public void setCodsurcharge(Long codsurcharge) {
        this.codsurcharge = codsurcharge;
    }

    public Integer getUnit_quantity() {
        return unit_quantity;
    }

    public void setUnit_quantity(Integer unit_quantity) {
        this.unit_quantity = unit_quantity;
    }

    public Long getUnit_total() {
        return unit_total;
    }

    public void setUnit_total(Long unit_total) {
        this.unit_total = unit_total;
    }

    public String getPic_address() {
        return pic_address;
    }

    public void setPic_address(String pic_address) {
        this.pic_address = pic_address;
    }

    public String getPic_1() {
        return pic_1;
    }

    public void setPic_1(String pic_1) {
        this.pic_1 = pic_1;
    }

    public String getPic_2() {
        return pic_2;
    }

    public void setPic_2(String pic_2) {
        this.pic_2 = pic_2;
    }

    public String getPic_3() {
        return pic_3;
    }

    public void setPic_3(String pic_3) {
        this.pic_3 = pic_3;
    }

    public String getPic_4() {
        return pic_4;
    }

    public void setPic_4(String pic_4) {
        this.pic_4 = pic_4;
    }

    public String getPickup_status() {
        return pickup_status;
    }

    public void setPickup_status(String pickup_status) {
        this.pickup_status = pickup_status;
    }

    public String getAssignment_datetime() {
        return assignment_datetime;
    }

    public void setAssignment_datetime(String assignment_datetime) {
        this.assignment_datetime = assignment_datetime;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }
}
