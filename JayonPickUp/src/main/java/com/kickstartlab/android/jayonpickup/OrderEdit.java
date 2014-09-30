package com.kickstartlab.android.jayonpickup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class OrderEdit extends Activity implements OnClickListener {

    Spinner deliverytype, deliverycity, deliveryzone, weight;
    EditText price,buyer_name, recipient_name;
    Button save_next;
    ImageButton scan_code;

    RequestQueue rq;

    Context context;

    private String statusurl, trxchangeurl;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ADDR = 13370;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ONE = 13371;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_TWO = 13372;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_THREE = 13373;
    private static final int SCAN_CODE_ACTIVITY_REQUEST_CODE = 0;

    private static final String PICKED_UP_STATUS = "pu";
    private static final String WILL_UP_STATUS = "wp";
    private static final String CANCEL_UP_STATUS = "cp";

    ImageView pic_address, pic_1, pic_2, pic_3;

    TextView lbl_trx_id, deliverycost, codsurcharge;

    String imagefile, trx_id, pic_address_name, pic_1_name, pic_2_name, pic_3_name;

    String delivery_id = "";

    Bitmap bitmap;

    ArrayAdapter<DOTariff> doAdapter;
    ArrayAdapter<PSTariff> psAdapter;
    ArrayAdapter<CODTariff> codAdapter;
    ArrayAdapter<City> cityAdapter;
    ArrayAdapter<District> zoneAdapter;

    Long app_id, merchant_id, actual_weight;
    Long order_id, id;

    Orders order;
    SharedPreferences jexPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_form);

        Intent intent = getIntent();

        context = this;

        rq = Volley.newRequestQueue(this);

        jexPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        statusurl = getResources().getString(R.string.status_url);
        trxchangeurl = getResources().getString(R.string.trxchange_url);

        order_id = intent.getLongExtra("order_id", 0);
        id = intent.getLongExtra("id", 0);

        deliverytype = (Spinner) findViewById(R.id.deliverytype);
        deliveryzone = (Spinner) findViewById(R.id.deliveryzone);
        deliverycity = (Spinner) findViewById(R.id.deliverycity);
        weight = (Spinner) findViewById(R.id.weight);

        lbl_trx_id = (TextView) findViewById(R.id.lblTrxId);

        price = (EditText) findViewById(R.id.unit_price);
        buyer_name = (EditText) findViewById(R.id.buyer_name);
        recipient_name = (EditText) findViewById(R.id.recipient_name);

        scan_code = (ImageButton) findViewById(R.id.btScanCode);
        save_next = (Button) findViewById(R.id.bt_save_next);

        pic_address = (ImageView) findViewById(R.id.pic_address);
        pic_1 = (ImageView) findViewById(R.id.pic_1);
        pic_2 = (ImageView) findViewById(R.id.pic_2);
        pic_3 = (ImageView) findViewById(R.id.pic_3);

        deliverycost = (TextView) findViewById(R.id.deliverycost);
        codsurcharge = (TextView) findViewById(R.id.codsurcharge);

        pic_address.setImageResource(R.drawable.ic_action_camera);
        pic_1.setImageResource(R.drawable.ic_action_camera);
        pic_2.setImageResource(R.drawable.ic_action_camera);
        pic_3.setImageResource(R.drawable.ic_action_camera);

        save_next.setOnClickListener(this);
        scan_code.setOnClickListener(this);

        pic_address.setOnClickListener(this);
        pic_1.setOnClickListener(this);
        pic_2.setOnClickListener(this);
        pic_3.setOnClickListener(this);



        /* Populate form */
        order = Orders.findById(Orders.class,id);
        app_id = order.getApp_id();
        merchant_id = order.getMerchant_id();

        trx_id = order.getTrx_id();
        lbl_trx_id.setText(trx_id);

        buyer_name.setText(order.getBuyer_name());
        recipient_name.setText(order.getRecipient_name());
        price.setText(order.getUnit_price().toString());

        pic_address_name = order.getPic_address();
        pic_1_name = order.getPic_1();
        pic_2_name = order.getPic_2();
        pic_3_name = order.getPic_3();

        deliverycity.setAdapter(getCityAdapter());
        psAdapter = getPSTariff(app_id);
        doAdapter = getDOTariff(app_id);

        delivery_id = order.getDelivery_id();

        if("PS".equalsIgnoreCase(order.getDelivery_type()) ){
            weight.setAdapter(psAdapter);
        }else{
            weight.setAdapter(doAdapter);
        }

        getZoneAdapter(order.getBuyerdeliveryzone());
        deliveryzone.setAdapter(zoneAdapter);

        ArrayAdapter<String> typeadapter = (ArrayAdapter<String>) deliverytype.getAdapter();

        for(int i = 0; i < typeadapter.getCount(); i++){
            String seltype = order.getDelivery_type();
            if(seltype.equalsIgnoreCase(typeadapter.getItem(i))){
                deliverytype.setSelection(i);
            }
        }

        for(int i = 0; i < cityAdapter.getCount(); i++){
            String seltype = order.getBuyerdeliverycity();
            if(seltype.equalsIgnoreCase( cityAdapter.getItem(i).getName() )){
                deliverycity.setSelection(i);
            }
        }

        for(int i = 0; i < zoneAdapter.getCount(); i++){
            String seltype = order.getBuyerdeliveryzone();
            if(seltype.equalsIgnoreCase( zoneAdapter.getItem(i).getDistrict() )){
                deliveryzone.setSelection(i);
            }
        }

        Log.i("tipe delivery", order.getDelivery_type());


        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if("".equalsIgnoreCase(price.getText().toString())){
                    Long p = Long.parseLong("0");
                    setCODSurcharge(p, app_id);
                }else{
                    Long p = Long.parseLong(price.getText().toString());
                    setCODSurcharge(p, app_id);
                }
            }
        });

        weight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if( "PS".equalsIgnoreCase(deliverytype.getSelectedItem().toString() ) ){
                    try{
                        deliverycost.setText(psAdapter.getItem(position).getTotal().toString()) ;
                        actual_weight = psAdapter.getItem(position).getCalculated_kg();
                    }catch(IndexOutOfBoundsException e){
                        deliverycost.setText(psAdapter.getItem(0).getTotal().toString()) ;
                        actual_weight = psAdapter.getItem(0).getCalculated_kg();
                    }
                }else{
                    try{
                        deliverycost.setText(doAdapter.getItem(position).getTotal().toString()) ;
                        actual_weight = doAdapter.getItem(position).getCalculated_kg();
                    }catch(IndexOutOfBoundsException e){
                        deliverycost.setText(doAdapter.getItem(0).getTotal().toString()) ;
                        actual_weight = doAdapter.getItem(0).getCalculated_kg();
                    }
                }

                Log.i("actual_weight", actual_weight.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        deliverycity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String ct = cityAdapter.getItem(position).getName().toString();
                Log.i("city",ct);

                getZoneAdapter(ct);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        deliverytype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String dtype = deliverytype.getItemAtPosition(position).toString();

                Long prc;

                if("".equalsIgnoreCase(price.getText().toString())){
                    prc = 0L;
                }else{
                    prc = Long.parseLong(price.getText().toString());
                }

                if("PS".equalsIgnoreCase(order.getDelivery_type()) ){
                    weight.setAdapter(psAdapter);
                    weight.setSelection(0);

                    for(int i = 0; i < psAdapter.getCount(); i++){
                        String seltype = order.getDeliverycost().toString();
                        if(seltype.equalsIgnoreCase( psAdapter.getItem(i).getTotal().toString() )){
                            weight.setSelection(i);
                        }
                    }
                }else{
                    weight.setAdapter(doAdapter);
                    weight.setSelection(0);

                    for(int i = 0; i < doAdapter.getCount(); i++){
                        String seltype = order.getDeliverycost().toString();
                        if(seltype.equalsIgnoreCase( doAdapter.getItem(i).getTotal().toString() )){
                            weight.setSelection(i);
                        }
                    }
                }

                deliverycost.setText(order.getDeliverycost().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.i("delivery cost", order.getDeliverycost().toString());

        if("PS".equalsIgnoreCase(order.getDelivery_type()) ){

            for(int i = 0; i < psAdapter.getCount(); i++){
                String seltype = order.getDeliverycost().toString();
                if(seltype.equalsIgnoreCase( psAdapter.getItem(i).getTotal().toString() )){
                    weight.setSelection(i);
                }
            }
        }else{

            for(int i = 0; i < doAdapter.getCount(); i++){
                String seltype = order.getDeliverycost().toString();
                if(seltype.equalsIgnoreCase( doAdapter.getItem(i).getTotal().toString() )){
                    weight.setSelection(i);
                }
            }
        }

        deliverycost.setText(order.getDeliverycost().toString());
        codsurcharge.setText(order.getCodsurcharge().toString());

        imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonpu/";

        displayPhoto(imagefile + pic_address_name, pic_address);
        displayPhoto(imagefile + pic_1_name, pic_1);
        displayPhoto(imagefile + pic_2_name, pic_2);
        displayPhoto(imagefile + pic_3_name, pic_3);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        Intent intent = new Intent();
        intent.putExtra("app_id", app_id);
        intent.putExtra("merchant_id", merchant_id);
        setResult(RESULT_OK, intent);
        finish();
    }


    public boolean saveOrder(){

        String dtype = deliverytype.getSelectedItem().toString();
        String dzone = deliveryzone.getSelectedItem().toString();
        String dcity = deliverycity.getSelectedItem().toString();
        String dprice = price.getText().toString();
        //String dweight = weight.getSelectedItem().toString();
        String dweight = deliverycost.getText().toString();

        String ddcost = deliverycost.getText().toString();
        String dcodsurcharge = codsurcharge.getText().toString();

        String buyer = buyer_name.getText().toString();
        String recipient = recipient_name.getText().toString();


        Long tprice, tdcost, tcod;

        if("".equals(dprice)){
            tprice = Long.parseLong("0");
        }else {
            tprice = Long.parseLong(dprice);
        }

        if("".equals(ddcost)){
            tdcost = Long.parseLong("0");
        }else {
            tdcost = Long.parseLong(ddcost);
        }

        if("".equals(dcodsurcharge)){
            tcod = Long.parseLong("0");
        }else {
            tcod = Long.parseLong(dcodsurcharge);
        }
/*
        merchant_id,
                app_id,
                trx_id,
                dtype,
                dzone,
                dcity,
                buyer,
                recipient,
                dweight,
                actual_weight,
                tprice,
                tdcost,
                tcod,
                pic_address_name,
                pic_1_name,
                pic_2_name,
                pic_3_name
*/


        order.setBuyer_name(buyer);
        order.setRecipient_name(recipient);
        order.setDelivery_type(dtype);
        order.setBuyerdeliveryzone(dzone);
        order.setBuyerdeliverycity(dcity);
        order.setUnit_price(tprice);
        order.setWeight(dweight);
        order.setDeliverycost(tdcost);
        order.setCodsurcharge(tcod);
        order.setActual_weight(actual_weight);

        order.setPic_address(pic_address_name);
        order.setPic_1(pic_1_name);
        order.setPic_2(pic_2_name);
        order.setPic_3(pic_3_name);

        order.setTrx_id(trx_id);


        order.save();

        return true;
    }

    @Override
    public void onClick(View v) {
        Log.i("view id", String.valueOf(v.getId()));

        switch(v.getId()){
            case R.id.bt_save_next:
                if(saveOrder()){
                    Toast.makeText(this, "Order Saved",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("app_id", app_id);
                    intent.putExtra("merchant_id", merchant_id);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(this, "Order Not Saved",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pic_address:
                takePhoto(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ADDR);
                break;

            case R.id.pic_1:
                takePhoto(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ONE);
                break;

            case R.id.pic_2:
                takePhoto(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_TWO);
                break;

            case R.id.pic_3:
                takePhoto(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_THREE);
                break;

            case R.id.btScanCode:

                /*
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "SCAN_MODE");
                startActivityForResult(intent, 0);
                */
                Intent intent;

                if(jexPrefs.getBoolean("use_ext_scanner", false)){
                    intent = new Intent("com.google.zxing.client.android.SCAN");
                }else{
                    intent = new Intent(this,ScannerActivity.class);
                }

                intent.putExtra("SCAN_MODE", "SCAN_MODE");
                startActivityForResult(intent, SCAN_CODE_ACTIVITY_REQUEST_CODE);

                Log.i("scan action", "do scan");

                break;

            default:
                Toast.makeText(this, "Nothing to do", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("activity Result", String.valueOf(requestCode));

        if( requestCode == SCAN_CODE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                String scresult = data.getStringExtra("SCAN_RESULT");

                trx_id = scresult;
                lbl_trx_id.setText(scresult);


                sendTrxid(delivery_id,scresult);
            }
        }else if(
                requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ADDR ||
                        requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ONE ||
                        requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_TWO ||
                        requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_THREE
                ) {
            if (resultCode == RESULT_OK) {
                try {
                    // We need to recyle unused bitmaps
                    if (bitmap != null) {
                        bitmap.recycle();
                    }

                    imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonpu/";
                    //imagefile = Environment.DIRECTORY_PICTURES + "/jayonpu/";
                    //imagefile = root_dir + "/jayonpu/";

                    switch(requestCode){
                        case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ADDR :
                            pic_address_name = trx_id + "_address.jpg";
                            displayPhoto(imagefile + pic_address_name, pic_address);

                            if("".equalsIgnoreCase(delivery_id) || delivery_id == null){

                                String strx =  Base64.encodeToString(trx_id.getBytes(), Base64.NO_WRAP);

                                Log.i("base64 encoded", strx);

                                sendStatus("trx",strx,PICKED_UP_STATUS);
                            }else{
                                String strx =  Base64.encodeToString(delivery_id.getBytes(), Base64.NO_WRAP);
                                sendStatus("did",strx,PICKED_UP_STATUS);
                            }
                            break;
                        case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ONE :
                            pic_1_name = trx_id + "_1.jpg";
                            displayPhoto(imagefile + pic_1_name, pic_1);
                            break;
                        case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_TWO :
                            pic_2_name = trx_id + "_2.jpg";
                            displayPhoto(imagefile + pic_2_name, pic_2);
                            break;
                        case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_THREE :
                            pic_3_name = trx_id + "_3.jpg";
                            displayPhoto(imagefile + pic_3_name, pic_3);
                            break;
                        default:
                            break;
                    }



                }catch(NullPointerException e){
                    e.printStackTrace();
                }


                //Bitmap thumbnail = (Bitmap) data.getExtras().get();
                //imagecam.setImageBitmap(thumbnail);
                //use imageUri here to access the image
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePhoto(Integer requestCode){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //String saved_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/";
        //String saved_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonpu/";

        String root_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String saved_file = root_dir + "/jayonpu/";

        File folder = new File(saved_file);
        if(!folder.exists()){
            folder.mkdirs();
        }

        File file = new File(saved_file + "dummy.jpg");

        switch(requestCode){
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ADDR :
                file = new File(saved_file + trx_id + "_address.jpg");
                if(file.exists()){
                    file.delete();
                    file = new File(saved_file + trx_id + "_address.jpg");
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_ONE :
                file = new File(saved_file + trx_id + "_1.jpg");
                if(file.exists()){
                    file.delete();
                    file = new File(saved_file + trx_id + "_1.jpg");
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_TWO :
                file = new File(saved_file + trx_id + "_2.jpg");
                if(file.exists()){
                    file.delete();
                    file = new File(saved_file + trx_id + "_2.jpg");
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_THREE :
                file = new File(saved_file + trx_id + "_3.jpg");
                if(file.exists()){
                    file.delete();
                    file = new File(saved_file + trx_id + "_3.jpg");
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                break;
            default:
                break;
        }

        startActivityForResult(cameraIntent, requestCode);

    }

    public void sendStatus(String idtype, String trx, String status){
        Log.i("trx", trx);
        Log.i("status",status);
        //String upstatusurl = new StringBuilder(statusurl).append("/trx/").append(trx).append("/status/").append(status);

        String upstatusurl = statusurl + "/"+ idtype +"/" + trx + "/status/" + status;

        Log.i("up url", upstatusurl);

        try{
            JsonObjectRequest jsonReq = new JsonObjectRequest( Request.Method.GET, upstatusurl, null, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("stat response", response.toString());
                    Orders o;
                    o = Orders.findById(Orders.class,id);
                    o.setPickup_status("sudah diambil");

                    Log.i("order buyer",o.getBuyer_name());
                    o.save();

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


    public void sendTrxid(String did, final String newtrx){

        Log.i("newtrx", newtrx);
        Log.i("did",did);

        String strx =  Base64.encodeToString(newtrx.getBytes(), Base64.NO_WRAP);
        String sdid =  Base64.encodeToString(did.getBytes(), Base64.NO_WRAP);

        String chgtrxurl = trxchangeurl + "/trx/" + strx + "/did/" + sdid;

        Log.i("chg trx url", chgtrxurl);

        try{
            JsonObjectRequest jsonReq = new JsonObjectRequest( Request.Method.GET, chgtrxurl, null, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("stat response", response.toString());

                    Orders o;
                    o = Orders.findById(Orders.class,id);

                    Log.i("order object", o.toString());

                    o.setTrx_id(newtrx);
                    o.save();

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



    private void displayPhoto(String imagepath, ImageView image_view){
        File imagefile = new File(imagepath);
        if(imagefile.exists()){
            Picasso.with(OrderEdit.this).load(imagefile).skipMemoryCache().noFade().into(image_view);
        }
    }

    protected ArrayAdapter<CODTariff> getCODTariff( Long app_id){
        List<CODTariff> codList = (List<CODTariff>) Select.from(CODTariff.class).where(Condition.prop("appid").eq(app_id)).orderBy("fromprice").list();
        return new ArrayAdapter<CODTariff>(this,android.R.layout.simple_spinner_dropdown_item,codList);
    }

    protected ArrayAdapter<DOTariff> getDOTariff( Long app_id){
        List<DOTariff> doList = (List<DOTariff>) Select.from(DOTariff.class).where(Condition.prop("appid").eq(app_id)).orderBy("kgfrom").list();
        doAdapter = new ArrayAdapter<DOTariff>(this,android.R.layout.simple_spinner_dropdown_item,doList);
        return doAdapter;
    }

    protected ArrayAdapter<PSTariff> getPSTariff( Long app_id){
        List<PSTariff> doList = (List<PSTariff>) Select.from(PSTariff.class).where(Condition.prop("appid").eq(app_id)).orderBy("kgfrom").list();
        psAdapter = new ArrayAdapter<PSTariff>(this,android.R.layout.simple_spinner_dropdown_item,doList);
        return psAdapter;
    }

    protected ArrayAdapter<City> getCityAdapter(){
        List<City> ctList = (List<City>) Select.from(City.class).orderBy("name").list();
        cityAdapter = new ArrayAdapter<City>(this,android.R.layout.simple_spinner_dropdown_item,ctList);
        return cityAdapter;
    }

    protected void getZoneAdapter(String city){
        List<District> zList = (List<District>) Select.from(District.class).where(Condition.prop("city").eq(city)).orderBy("district").list();
        zoneAdapter = new ArrayAdapter<District>(this,android.R.layout.simple_spinner_dropdown_item,zList);
        deliveryzone.setAdapter(zoneAdapter);
    }

    protected void setCODSurcharge(Long price, Long app_id){

        //String pricestring = String.valueOf(price);

        List<CODTariff> codTariffList = CODTariff.find(CODTariff.class, " fromprice <= ? and toprice >= ? and appid = ?", price.toString(), price.toString(), app_id.toString() );

        if(codTariffList.size() > 0){
            codsurcharge.setText(codTariffList.get(0).getSurcharge().toString());
        }else{

            //List<CODTariff> codTariffListMax = CODTariff.find(CODTariff.class, "appid = ?", new String[]{ app_id.toString() }, "appid", "toprice desc", "1");
            try{

                CODTariff codTariffListMax = (CODTariff) Select.from(CODTariff.class).where("appid = ?", new String[]{app_id.toString()}).orderBy("toprice desc").first();

                //Log.i("codmax", codTariffListMax.getSurcharge().toString() );


                if(codTariffListMax.getTo_price() < price){

                    codsurcharge.setText(codTariffListMax.getSurcharge().toString());
                }else{
                    codsurcharge.setText("0");
                }

            }catch(NullPointerException e){

                codsurcharge.setText("0");

            }



        }

    }

}
