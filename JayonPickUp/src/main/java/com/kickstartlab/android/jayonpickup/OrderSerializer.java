package com.kickstartlab.android.jayonpickup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by awidarto on 1/23/14.
 */
public class OrderSerializer implements JsonSerializer<Orders> {

    @Override
    public JsonElement serialize(Orders order, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("create_time", order.getCreate_time());
        jsonObject.addProperty("create_datetime", order.getCreate_datetime());

        jsonObject.addProperty("merchant_id", order.getMerchant_id());
        jsonObject.addProperty("app_id", order.getApp_id());
        jsonObject.addProperty("trx_id", order.getTrx_id());
        jsonObject.addProperty("delivery_type", order.getDelivery_type());
        jsonObject.addProperty("buyerdeliveryzone", order.getBuyerdeliveryzone());
        jsonObject.addProperty("buyerdeliverycity", order.getBuyerdeliverycity());

        jsonObject.addProperty("buyer_name", order.getBuyer_name());
        jsonObject.addProperty("recipient_name", order.getRecipient_name());

        jsonObject.addProperty("weight", order.getWeight());
        jsonObject.addProperty("actual_weight", order.getActual_weight());
        jsonObject.addProperty("unit_price", order.getUnit_price());
        jsonObject.addProperty("deliverycost", order.getDeliverycost());
        jsonObject.addProperty("codsurcharge", order.getCodsurcharge());
        jsonObject.addProperty("pickup_status",order.getPickup_status());

        jsonObject.addProperty("pic_address", order.getPic_address());
        jsonObject.addProperty("pic_1", order.getPic_1());
        jsonObject.addProperty("pic_2", order.getPic_2());
        jsonObject.addProperty("pic_3", order.getPic_3());

        String saved_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonpu/";

        String trx_id = order.getTrx_id();

        if(new File(saved_file + trx_id + "_address.jpg").exists()){

            String sdata = prepPicture(saved_file + trx_id + "_address.jpg");

            jsonObject.addProperty("pic_address_body", sdata);

        }

        if(new File(saved_file + trx_id + "_1.jpg").exists()){

            String sdata = prepPicture(saved_file + trx_id + "_1.jpg");
            jsonObject.addProperty("pic_1_body", sdata);

        }

        if(new File(saved_file + trx_id + "_2.jpg").exists()){

            String sdata = prepPicture(saved_file + trx_id + "_2.jpg");
            jsonObject.addProperty("pic_2_body", sdata);

        }

        if(new File(saved_file + trx_id + "_3.jpg").exists()){

            String sdata = prepPicture(saved_file + trx_id + "_3.jpg");
            jsonObject.addProperty("pic_3_body", sdata);

        }

        return jsonObject;
    }

    public String prepPicture(String filename){

        Bitmap bm = BitmapFactory.decodeFile(filename);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 25, bos);
        byte[] bdata = bos.toByteArray();
        String sdata = Base64.encodeToString(bdata, Base64.DEFAULT);

        return sdata;
    }

    /*
    @Override
    public JsonElement serialize(Orders order, Type type, JsonSerializationContext jsonSerializationContext) {

        final JsonObject jsonObject = new JsonObject();

        jsonObject.add("merchant_id", String.valueOf order.getMerchant_id());
        jsonObject.add("app_id", order.getApp_id());
        jsonObject.add("trx_id", order.getTrx_id());
        jsonObject.add("delivery_type", order.getDelivery_type());
        jsonObject.add("buyerdeliveryzone", order.getBuyerdeliveryzone());

        jsonObject.add("buyerdeliverycity", order.getBuyerdeliverycity());
        jsonObject.add("weight", order.getWeight());
        jsonObject.add("actual_weight", order.getActual_weight());
        jsonObject.add("unit_price", order.getUnit_price());
        jsonObject.add("deliverycost", order.getDeliverycost());

        jsonObject.add("codsurcharge", order.getCodsurcharge());
        jsonObject.add("pic_address", order.getPic_address());
        jsonObject.add("pic_1", order.getPic_1());
        jsonObject.add("pic_2", order.getPic_2());
        jsonObject.add("pic_3", order.getPic_3());

        return jsonObject;
    }
    */


}
