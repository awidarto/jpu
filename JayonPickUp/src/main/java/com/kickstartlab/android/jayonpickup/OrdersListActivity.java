package com.kickstartlab.android.jayonpickup;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Downloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrdersListActivity extends Activity implements OrderListFragment.OrderListInteraction {

    Long app_id, merchant_id;
    String shopname;

    private String upurl;

    private String dataurl;

    private String statusurl;

    SharedPreferences jexPrefs;

    Context context;

    RequestQueue rq;

    OnActionCallback onActionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        rq = Volley.newRequestQueue(this);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_orders_list);


        setProgressBarIndeterminateVisibility(false);

        context = this;

        upurl = getResources().getString(R.string.upload_url);
        dataurl = getResources().getString(R.string.data_url);
        statusurl = getResources().getString(R.string.status_url);

        jexPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            merchant_id = extras.getLong("merchant_id");
            app_id = extras.getLong("app_id");
            shopname = extras.getString("shopname");
        }


        OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

        if (orderListFragment != null && orderListFragment.isInLayout()) {
            orderListFragment.updateOrderList(app_id, merchant_id, shopname);
        }


        if (savedInstanceState == null) {
        }
    }

    public void updateStatus(String trx, String status){


        String dev_id = jexPrefs.getString("device_identifier","JY-PICKUPDEV");
        String courier_name = jexPrefs.getString("courier_name","John Smith");

        try {

            Toast.makeText(context, trx + " " + status, Toast.LENGTH_SHORT).show();

        }catch(Exception e){

        }


    }

    public void uploadData(){


        String dev_id = jexPrefs.getString("device_identifier","JY-PICKUPDEV");
        String courier_name = jexPrefs.getString("courier_name","John Smith");

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        today = today + "%";

        Log.i("current date", today);


        List<Orders> orderList = (List<Orders>) Select.from(Orders.class)
                .where(Condition.prop("CREATEDATETIME").like(today))
                .where(Condition.prop("SYNCSTATUS").notEq("sync"))
                .list();

        Integer total = orderList.size();

        Log.i("Total", total.toString());


        if( total > 0){

            setProgressBarIndeterminateVisibility(true);

            JSONArray queuedJSON = new JSONArray();

            for(int j = 0; j < total ;j = j + 5 ){

                String limit = new StringBuilder().append(j * 5).append(",").append(5).toString();

                List<Orders> orderCap = (List<Orders>) Select.from(Orders.class)
                        .where( Condition.prop("CREATEDATETIME").like(today) )
                        .where(Condition.prop("SYNCSTATUS").notEq("sync"))
                        .limit(limit)
                        .list();

                try{

                    GsonBuilder gsonBuilder = new GsonBuilder();

                    gsonBuilder.registerTypeAdapter(Orders.class, new OrderSerializer());

                    //gsonBuilder.setPrettyPrinting();

                    Gson gson = gsonBuilder.create();

                    String orderBody = gson.toJson(orderCap);

                    JSONObject jsonBody = new JSONObject();

                    jsonBody.put("dev_id", dev_id);
                    jsonBody.put("courier_name", courier_name);
                    jsonBody.put("orders", orderBody);

                    Log.i("jsonBody", jsonBody.toString());

                    queuedJSON.put(jsonBody);

                }catch(JSONException e){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

                }

            }

            Toast.makeText(this, "Uploading Data", Toast.LENGTH_SHORT).show();

            if( queuedJSON.length() > 0){

                for( int q = 0; q < queuedJSON.length(); q++){


                    try{

                        JSONObject jsonBody = new JSONObject();

                        jsonBody = queuedJSON.getJSONObject(q);

                        JsonObjectRequest upObjRequest = new JsonObjectRequest(Request.Method.POST, upurl, jsonBody, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {


                                JSONArray ids = null;

                                try {
                                    ids = response.getJSONArray("orders");
                                    Log.i("responseobj", ids.toString());

                                    for(int ix = 0;ix < ids.length();ix++){
                                        String idx = ids.getString(ix);

                                        List<Orders> ordlist = (List<Orders>) Select.from(Orders.class).where(Condition.prop("TRXID").eq(idx)).list();

                                        Orders order = null;

                                        if(ordlist.size() == 0){

                                        }else{
                                            order = ordlist.get(0);
                                            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                                            order.setSync_status("sync");
                                            order.setSync_time(now);

                                            order.save();
                                        }


                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                setProgressBarIndeterminateVisibility(false);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                setProgressBarIndeterminateVisibility(false);

                                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                        );

                        rq.add(upObjRequest);


                    }catch(Exception e){

                    }


                }


            }

        }else{
            Toast.makeText(this,"Tidak ada data untuk upload",Toast.LENGTH_SHORT).show();
        }



    }

    public void downloadData(){

        setProgressBarIndeterminateVisibility(true);

        String dev_id = jexPrefs.getString("device_identifier","JY-DEV2");

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        //today = "2014-03-04";//testing only

        Log.i("current date", today);

        String orderdataurl = new StringBuilder(dataurl).append("/did/").append(dev_id).append("/mid/").append(merchant_id).append("/date/").append(today).toString();

        Log.i("data_url", orderdataurl);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, orderdataurl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                //txtDisplay.setText("Response => "+response.toString());
                //findViewById(R.id.progressBar1).setVisibility(View.GONE);

                Log.d("volleyresponse", response.toString());

                try {

                    //update cod

                    JSONArray orders = response.getJSONArray("orders");

                    for (int i = 0; i < orders.length(); i++) {

                        //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));
                        Long merchant_id = orders.getJSONObject(i).getLong("merchant_id");
                        Long app_id = orders.getJSONObject(i).getLong("application_id");
                        String trx_id = orders.getJSONObject(i).getString("merchant_trans_id");
                        String dtype = orders.getJSONObject(i).getString("delivery_type");
                        String dzone = orders.getJSONObject(i).getString("buyerdeliveryzone");
                        String dcity = orders.getJSONObject(i).getString("buyerdeliverycity");
                        String buyer = orders.getJSONObject(i).getString("buyer_name");
                        String recipient = orders.getJSONObject(i).getString("recipient_name");
                        String dweight = orders.getJSONObject(i).getString("weight");
                        Long actual_weight = orders.getJSONObject(i).getLong("actual_weight");
                        Long tprice = orders.getJSONObject(i).getLong("total_price");
                        Long tdcost = orders.getJSONObject(i).getLong("delivery_cost");
                        Long tcod = orders.getJSONObject(i).getLong("cod_cost");
                        String pstatus = orders.getJSONObject(i).getString("pickup_status");
                        String delivery_id = orders.getJSONObject(i).getString("delivery_id");

                        Orders o;

                        List<Orders> ord = Orders.find(Orders.class, "TRXID = ?", trx_id.toString());

                        if(ord.size() == 0){
                            o = new Orders(context);
                            Log.i("cod","new");
                        }else{
                            o = ord.get(0);
                            Long id = o.getId();
                        }

                        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                        o.setCreate_datetime(createdAt);
                        o.setMerchant_id(merchant_id);
                        o.setApp_id(app_id);
                        o.setTrx_id(trx_id);
                        o.setBuyerdeliveryzone(dzone);
                        o.setBuyerdeliverycity(dcity);
                        o.setDelivery_type(dtype);
                        o.setBuyer_name(buyer);
                        o.setRecipient_name(recipient);
                        o.setWeight(dweight);
                        o.setActual_weight(actual_weight);
                        o.setUnit_price(tprice);
                        o.setDeliverycost(tdcost);
                        o.setCodsurcharge(tcod);
                        o.setPickup_status(pstatus);
                        o.setDelivery_id(delivery_id);

                        o.save();
                    }


                } catch (JSONException e) {
                    setProgressBarIndeterminateVisibility(false);

                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }

                OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

                if (orderListFragment != null && orderListFragment.isInLayout()) {
                    orderListFragment.updateOrderList(app_id, merchant_id, shopname);
                }

                /*
                OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

                if(orderListFragment != null){
                    orderListFragment.refreshList(shownapp, shownmerchant);
                }
                */

                setProgressBarIndeterminateVisibility(false);

                Toast.makeText(context, "Order Data Downloaded", Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                setProgressBarIndeterminateVisibility(false);

                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        );

        rq.add(jsObjRequest);


    }

    @Override
    protected void onResume() {
        super.onResume();
        OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

        if (orderListFragment != null && orderListFragment.isInLayout()) {
            orderListFragment.updateOrderList(app_id, merchant_id, shopname);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.orders_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_upload:
                uploadData();
                break;
            case R.id.action_download:
                downloadData();
                break;
        }

            return super.onOptionsItemSelected(item);
    }

    public void sendStatusCallback(String trx, String status){
        Log.i("trx", trx);
        Log.i("status",status);
        //String upstatusurl = new StringBuilder(statusurl).append("/trx/").append(trx).append("/status/").append(status);

        String upstatusurl = statusurl + "/trx/" + trx + "/status/" + status;

        Log.i("up url", upstatusurl);

        try{
            JsonObjectRequest jsonReq = new JsonObjectRequest( Request.Method.GET, upstatusurl, null, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("stat response", response.toString());
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,error.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("stat error", error.toString());
                }
            });

            rq.add(jsonReq);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public interface OnActionCallback{
        public void sendDataCallback();
        public void sendStatusCallback(String trx, String status);
    }

}
