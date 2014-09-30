package com.kickstartlab.android.jayonpickup;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.kickstartlab.android.jayonpickup.dummy.DummyContent;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements MerchantListFragment.OnMerchantSelectedListener,
        ShopListFragment.OnShopSelectedListener,
        OrderListFragment.OrderListInteraction,
        OrdersListActivity.OnActionCallback{

    RequestQueue rq;

    Context context;

    MerchantListFragment merchantListFragment;

    private String zoneurl;

    private String merchanturl;

    private String dataurl;

    private String upurl;

    private Boolean login = false;

    SharedPreferences jexPrefs;

    String passkey;

    PassKeyDialog passdialog;

    public Long shownmerchant = 0L;
    public Long shownapp = 0L;

    private static final int SCAN_CODE_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);
        rq = Volley.newRequestQueue(this);
        setProgressBarIndeterminateVisibility(false);

        upurl = getResources().getString(R.string.upload_url);
        zoneurl = getResources().getString(R.string.zone_url);
        merchanturl = getResources().getString(R.string.merchant_url);
        dataurl = getResources().getString(R.string.data_url);

        jexPrefs = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings :
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_upload :
                uploadData();
                break;
            case R.id.action_download:
                downloadData();
                break;
            case R.id.action_sync_merchant:
                downloadMerchant();
                break;
            case R.id.action_sync_zone :

                setProgressBarIndeterminateVisibility(true);

                JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET,zoneurl,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            JSONArray ct = response.getJSONObject("data").getJSONArray("city");

                            for (int i = 0; i < ct.length(); i++) {
                                //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));

                                String cityname = ct.getJSONObject(i).getString("name");

                                List<City> ctname = City.find(City.class, "name = ?", cityname );

                                City city;


                                if(ctname.size() == 0){
                                    city = new City(MainActivity.this);
                                    Log.i("do","new");
                                }else{
                                    city = ctname.get(0);
                                    Long id = city.getId();
                                }

                                city.setName(cityname);
                                city.save();

                            }

                            JSONArray dt = response.getJSONObject("data").getJSONArray("district");

                            for (int i = 0; i < dt.length(); i++) {
                                //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));

                                String districtname = dt.getJSONObject(i).getString("district");
                                String cityname = dt.getJSONObject(i).getString("city");
                                String province = dt.getJSONObject(i).getString("province");
                                String country = dt.getJSONObject(i).getString("country");
                                Integer is_on = dt.getJSONObject(i).getInt("is_on");

                                List<District> dtlist = (List<District>) Select.from(District.class).where(Condition.prop("district").eq(districtname)).where(Condition.prop("city").eq(cityname)).list();

                                District district;

                                if(dtlist.size() == 0){
                                    district = new District(MainActivity.this);
                                    Log.i("do","new");
                                }else{
                                    district = dtlist.get(0);
                                    Long id = district.getId();
                                }

                                district.setDistrict(districtname);
                                district.setCity(cityname);
                                district.setProvince(province);
                                district.setCountry(country);
                                district.setIs_on(is_on);

                                district.save();

                            }

                        } catch (JSONException e) {
                            //mProgress.setVisibility(View.INVISIBLE);

                            setProgressBarIndeterminateVisibility(false);

                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }

                        setProgressBarIndeterminateVisibility(false);

                        Toast.makeText(MainActivity.this, "Zone Data Updated", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

                rq.add(jreq);

                Toast.makeText(this, "Synchronizing Zone Data", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onMerchantSelected(Long merchant_id, String merchant_name, String merchant_address){

        shownmerchant = merchant_id;

        ShopListFragment shopListFragment = (ShopListFragment) getFragmentManager().findFragmentById(R.id.shop_list_fragment);

        if (shopListFragment != null && shopListFragment.isInLayout()) {
            shopListFragment.updateShopList(merchant_id, merchant_name, merchant_address );
            Log.i("merchant selected",String.valueOf(merchant_id));

            OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

            if (orderListFragment != null) {
                orderListFragment.clearList();

                try{
                    Apps al = (Apps) Select.from(Apps.class).where(Condition.prop("merchantid").eq(merchant_id)).orderBy("appid").first();
                    orderListFragment.updateOrderList(al.getApp_id(), merchant_id, al.getApplication_name());
                }catch(NullPointerException e){

                    e.printStackTrace();

                }

            }else{

            }


        }else{
            Intent shopintent = new Intent(getApplicationContext(), ShopsListActivity.class);
            shopintent.putExtra("merchant_id",merchant_id);
            shopintent.putExtra("merchant_name",merchant_name);
            shopintent.putExtra("merchant_address",merchant_address);

            startActivity(shopintent);
        }


    }

    public void onShopSelected(Long app_id, Long merchant_id, String shopname){

        shownmerchant = merchant_id;
        shownapp = app_id;

        OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

        if (orderListFragment != null) {
            orderListFragment.updateOrderList(app_id, merchant_id, shopname );
            Log.i("app selected", String.valueOf(app_id));
        }else{

        }
    }

    public void sendDataCallback(){
        uploadData();
    }

    public void sendStatusCallback( String trx, String status){
        updateStatus(trx,status);
    }

    public void updateStatus(String trx, String status){


        String dev_id = jexPrefs.getString("device_identifier","JY-PICKUPDEV");
        String courier_name = jexPrefs.getString("courier_name","John Smith");

        Toast.makeText(context, trx + " " + status, Toast.LENGTH_SHORT).show();

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

        String orderdataurl = new StringBuilder(dataurl).append("/did/").append(dev_id).append("/date/").append(today).toString();

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

                        o.save();
                    }


                } catch (JSONException e) {
                    setProgressBarIndeterminateVisibility(false);

                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }

                /* Refresh merchant list fragment if visible */
                OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

                if(orderListFragment != null){
                    orderListFragment.refreshList(shownapp, shownmerchant);
                }


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

    public void downloadMerchant(){

        setProgressBarIndeterminateVisibility(true);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, merchanturl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                //txtDisplay.setText("Response => "+response.toString());
                //findViewById(R.id.progressBar1).setVisibility(View.GONE);

                Log.d("volleyresponse", response.toString());

                try {


                    JSONArray merchants = response.getJSONObject("data").getJSONArray("merchants");

                    for (int i = 0; i < merchants.length(); i++) {
                        //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));

                        Long merchant_id = merchants.getJSONObject(i).getLong("id");

                        String district    = merchants.getJSONObject(i).getString("district");
                        String province    = merchants.getJSONObject(i).getString("province");
                        String city        = merchants.getJSONObject(i).getString("city");
                        String country     = merchants.getJSONObject(i).getString("country");
                        String zip         = merchants.getJSONObject(i).getString("zip");
                        String phone       = merchants.getJSONObject(i).getString("phone");
                        String mobile      = merchants.getJSONObject(i).getString("mobile");
                        String mobile1     = merchants.getJSONObject(i).getString("mobile1");
                        String mobile2     = merchants.getJSONObject(i).getString("mobile2");
                        String merchantname= merchants.getJSONObject(i).getString("merchantname");
                        String mc_email    = merchants.getJSONObject(i).getString("mc_email");
                        String mc_street   = merchants.getJSONObject(i).getString("mc_street");
                        String mc_district = merchants.getJSONObject(i).getString("mc_district");
                        String mc_city     = merchants.getJSONObject(i).getString("mc_city");
                        String mc_province = merchants.getJSONObject(i).getString("mc_province");
                        String mc_country  = merchants.getJSONObject(i).getString("mc_country");
                        String mc_zip      = merchants.getJSONObject(i).getString("mc_zip");
                        String mc_phone    = merchants.getJSONObject(i).getString("mc_phone");
                        String mc_mobile   = merchants.getJSONObject(i).getString("mc_mobile");
                        String street      = merchants.getJSONObject(i).getString("street");

                        Log.i("merchant_id",merchant_id.toString());

                        Merchant m;

                        List<Merchant> merchant = Merchant.find(Merchant.class, "MERCHANTID = ?", merchant_id.toString());

                        if(merchant.size() == 0){
                            m = new Merchant(context);
                            //m.setId(merchant_id);
                            Log.i("merchant","new");
                        }else{
                            m = merchant.get(0);
                            Long id = m.getId();
                            Log.i("merchant exist",id.toString());
                            //m.setId(merchant_id);
                        }

                        m.setMerchant_id(merchant_id);
                        m.setDistrict(district);
                        m.setProvince(province);
                        m.setCity(city);
                        m.setCountry(country);
                        m.setZip(zip);
                        m.setPhone(phone);
                        m.setMobile(mobile);
                        m.setMobile1(mobile1);
                        m.setMobile2(mobile2);
                        m.setMerchantname(merchantname);
                        m.setMc_email(mc_email);
                        m.setMc_street(mc_street);
                        m.setMc_district(mc_district);
                        m.setMc_city(mc_city);
                        m.setMc_province(mc_province);
                        m.setMc_country(mc_country);
                        m.setMc_zip(mc_zip);
                        m.setMc_phone(mc_phone);
                        m.setMc_mobile(mc_mobile);
                        m.setStreet(street);


                        m.save();

                    }

                    //update apps

                    JSONArray apps = response.getJSONObject("data").getJSONArray("apps");

                    for (int i = 0; i < apps.length(); i++) {
                        //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));

                        Long app_id = apps.getJSONObject(i).getLong("id");
                        Long merchant_id     = apps.getJSONObject(i).getLong("merchant_id");
                        String application_name = apps.getJSONObject(i).getString("application_name");
                        String key              = apps.getJSONObject(i).getString("key");

                        Log.i("app_id",app_id.toString());

                        Apps a;

                        List<Apps> app = Apps.find(Apps.class, "APPID = ?", app_id.toString());

                        if(app.size() == 0){
                            a = new Apps(context);
                            Log.i("apps","new");
                        }else{
                            a = app.get(0);
                            Long id = a.getId();
                            Log.i("apps exist",id.toString());
                            //m.setId(merchant_id);
                        }

                        a.setMerchant_id(merchant_id);
                        a.setApp_id(app_id);
                        a.setApplication_name(application_name);
                        a.setKey(key);
                        a.save();

                    }

                    //update cod

                    JSONArray cods = response.getJSONObject("data").getJSONArray("cod");

                    for (int i = 0; i < cods.length(); i++) {
                        //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));

                        Long cod_id = cods.getJSONObject(i).getLong("id");
                        Long app_id = cods.getJSONObject(i).getLong("app_id");
                        Long from_price = cods.getJSONObject(i).getLong("from_price");
                        Long to_price = cods.getJSONObject(i).getLong("to_price");
                        Long surcharge = cods.getJSONObject(i).getLong("surcharge");

                        CODTariff c;

                        List<CODTariff> cod = CODTariff.find(CODTariff.class, "CODID = ?", cod_id.toString());

                        if(cod.size() == 0){
                            c = new CODTariff(context);
                            Log.i("cod","new");
                        }else{
                            c = cod.get(0);
                            Long id = c.getId();
                        }
                        c.setCod_id(cod_id);
                        c.setApp_id(app_id);
                        c.setFrom_price(from_price);
                        c.setTo_price(to_price);
                        c.setSurcharge(surcharge);
                        c.save();

                    }

                    //update do

                    JSONArray dos = response.getJSONObject("data").getJSONArray("do");

                    for (int i = 0; i < dos.length(); i++) {
                        //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));

                        Long do_id = dos.getJSONObject(i).getLong("id");
                        Long kg_from =  dos.getJSONObject(i).getLong("kg_from");
                        Long kg_to =  dos.getJSONObject(i).getLong("kg_to");
                        Long calculated_kg =  dos.getJSONObject(i).getLong("calculated_kg");
                        Long tariff_kg =  dos.getJSONObject(i).getLong("tariff_kg");
                        Long total =  dos.getJSONObject(i).getLong("total");
                        Long app_id =  dos.getJSONObject(i).getLong("app_id");

                        DOTariff d;

                        List<DOTariff> dot = DOTariff.find(DOTariff.class, "DOID = ?", do_id.toString());

                        if(dot.size() == 0){
                            d = new DOTariff(context);
                            Log.i("do","new");
                        }else{
                            d = dot.get(0);
                            Long id = d.getId();
                        }

                        d.setApp_id(app_id);
                        d.setDo_id(do_id);
                        d.setKg_from(kg_from);
                        d.setKg_to(kg_to);
                        d.setCalculated_kg(calculated_kg);
                        d.setTariff_kg(tariff_kg);
                        d.setTotal(total);

                        d.save();

                    }

                    //update ps

                    JSONArray pot = response.getJSONObject("data").getJSONArray("ps");

                    for (int i = 0; i < pot.length(); i++) {
                        //merchantArray.add(merchants.getJSONObject(i).getString("merchantname"));

                        Long ps_id = pot.getJSONObject(i).getLong("id");
                        Long kg_from =  pot.getJSONObject(i).getLong("kg_from");
                        Long kg_to =  pot.getJSONObject(i).getLong("kg_to");
                        Long calculated_kg =  pot.getJSONObject(i).getLong("calculated_kg");
                        Long tariff_kg =  pot.getJSONObject(i).getLong("tariff_kg");
                        Long total =  pot.getJSONObject(i).getLong("total");
                        Long app_id =  pot.getJSONObject(i).getLong("app_id");

                        PSTariff p;

                        List<PSTariff> ps = PSTariff.find(PSTariff.class, "PSID = ?", ps_id.toString());

                        if(ps.size() == 0){
                            p = new PSTariff(context);
                            Log.i("do","new");
                        }else{
                            p = ps.get(0);
                            Long id = p.getId();
                        }

                        p.setApp_id(app_id);
                        p.setPs_id(ps_id);
                        p.setKg_from(kg_from);
                        p.setKg_to(kg_to);
                        p.setCalculated_kg(calculated_kg);
                        p.setTariff_kg(tariff_kg);
                        p.setTotal(total);

                        p.save();

                    }

                } catch (JSONException e) {
                    setProgressBarIndeterminateVisibility(false);

                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }

                /* Refresh merchant list fragment if visible */
                MerchantListFragment merchantListFragment = (MerchantListFragment) getFragmentManager().findFragmentById(R.id.merchant_list_fragment);

                if(merchantListFragment != null){
                    merchantListFragment.refreshView();
                }


                setProgressBarIndeterminateVisibility(false);

                Toast.makeText(context, "Merchant Data Updated", Toast.LENGTH_LONG).show();


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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == SCAN_CODE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                String scresult = data.getStringExtra("SCAN_RESULT");

                Log.i("scan result", scresult);

                List<Orders> ord = Orders.find(Orders.class, "TRXID = ?", scresult);


                if(ord.size() > 0){
                    Orders o = ord.get(0);
                    o.setPickup_status("sudah diambil");
                    o.save();
                    Toast.makeText(this,"Order ditemukan",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Tidak ada order yang sama",Toast.LENGTH_SHORT).show();
                }

            }
        }


    }


    public String[] collectSignatures(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentdate = sdf.format(new Date());
        String currentsuffix = currentdate + "_sign.jpg";

        String root_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String saved_file = root_dir + "/jayonpu/";

        File signPath = new File(saved_file);

        ArrayList<String> resultFiles = new ArrayList<String>();

        if(signPath.exists() && signPath.isDirectory()){
            File[] fileList = signPath.listFiles();
            for(File infile : fileList){
                if(infile.getName().contains(currentsuffix) ){
                    resultFiles.add(infile.getName());
                }
            }
        }

        return (String[]) resultFiles.toArray();
    }


}
