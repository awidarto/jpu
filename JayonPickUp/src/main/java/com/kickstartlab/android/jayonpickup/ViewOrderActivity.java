package com.kickstartlab.android.jayonpickup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ViewOrderActivity extends Activity {

    private static final int EDIT_ORDER_ACTIVITY_REQUEST_CODE = 7557;

    TextView buyer, recipient ,deliverytype, deliverycity, deliveryzone, weight, price, deliverycost, codsurcharge;

    Button edit,delete,cancel;

    ImageView pic_address, pic_1, pic_2, pic_3;

    TextView lbl_trx_id;

    String imagefile, trx_id, pic_address_name, pic_1_name, pic_2_name, pic_3_name, weighttariff;
    Bitmap bitmap;

    Long order_id, id;

    Orders order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_view);

        Intent intent = getIntent();

        order_id = intent.getLongExtra("order_id", 0);
        id = intent.getLongExtra("id", 0);

        buyer = (TextView) findViewById(R.id.buyer_name);
        recipient = (TextView) findViewById(R.id.recipient_name);

        edit = (Button) findViewById(R.id.bt_edit);
        delete = (Button) findViewById(R.id.bt_del);
        cancel = (Button) findViewById(R.id.bt_cancel);

        deliverytype = (TextView) findViewById(R.id.deliverytype);
        deliveryzone = (TextView) findViewById(R.id.deliveryzone);
        deliverycity = (TextView) findViewById(R.id.deliverycity);
        weight = (TextView) findViewById(R.id.weight);
        price = (TextView) findViewById(R.id.unit_price);

        lbl_trx_id = (TextView) findViewById(R.id.lblTrxId);
        deliverycost = (TextView) findViewById(R.id.deliverycost);
        codsurcharge = (TextView) findViewById(R.id.codsurcharge);

        order = Orders.findById(Orders.class,id);

        refreshData();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewOrderActivity.this, OrderEdit.class);

                intent.putExtra("order_id", order.getOrder_id());
                intent.putExtra("id", order.getId());

                startActivityForResult(intent, EDIT_ORDER_ACTIVITY_REQUEST_CODE);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder delConfirm = new AlertDialog.Builder(ViewOrderActivity.this);

                delConfirm.setTitle("Delete Order");
                delConfirm.setMessage("Anda yakin untuk menghapus order ini ?");

                delConfirm.setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order.delete();
                        Toast.makeText(ViewOrderActivity.this, "Order telah dihapus", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

                delConfirm.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                delConfirm.show();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder delConfirm = new AlertDialog.Builder(ViewOrderActivity.this);

                delConfirm.setTitle(getString(R.string.pickup_cancel));
                delConfirm.setMessage(getString(R.string.pickup_cancel_message));

                delConfirm.setPositiveButton(getString(R.string.act_yes),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order.setPickup_status(getString(R.string.is_canceled));
                        order.save();
                        Toast.makeText(ViewOrderActivity.this, "Order telah dibatalkan", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

                delConfirm.setNegativeButton(getString(R.string.act_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                delConfirm.show();


            }
        });


    }

    public void refreshData(){
        buyer.setText(order.getBuyer_name());
        recipient.setText(order.getRecipient_name());
        deliverycity.setText(order.getBuyerdeliverycity());
        deliveryzone.setText(order.getBuyerdeliveryzone());
        deliverytype.setText(order.getDelivery_type());
        price.setText(order.getUnit_price().toString());

        try{

            Log.i("app_id", order.getApp_id().toString());
            Log.i("weight", order.getWeight());
            Log.i("actual weight",order.getActual_weight().toString());

            if("PS".equalsIgnoreCase(order.getDelivery_type()) ){

                PSTariff wt = (PSTariff) Select.from(PSTariff.class).where(Condition.prop("appid").eq(order.getApp_id())).where(Condition.prop("total").eq(order.getWeight())).first();

                weighttariff = wt.getKg_from().toString() + "kg - " + wt.getKg_to().toString() + "kg";

            }else{
                DOTariff wt= (DOTariff) Select.from(DOTariff.class).where(Condition.prop("appid").eq(order.getApp_id())).where(Condition.prop("total").eq(order.getWeight())).first();

                weighttariff = wt.getKg_from().toString() + "kg - " + wt.getKg_to().toString() + "kg";

            }

        }catch(NullPointerException e){

            e.printStackTrace();

            weighttariff = "";
        }


        weight.setText(weighttariff);

        deliverycost.setText(order.getDeliverycost().toString());
        codsurcharge.setText(order.getCodsurcharge().toString());

        pic_address = (ImageView) findViewById(R.id.pic_address);
        pic_1 = (ImageView) findViewById(R.id.pic_1);
        pic_2 = (ImageView) findViewById(R.id.pic_2);
        pic_3 = (ImageView) findViewById(R.id.pic_3);

        pic_address_name = order.getPic_address();
        pic_1_name = order.getPic_1();
        pic_2_name = order.getPic_2();
        pic_3_name = order.getPic_3();


        //String root_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        //imagefile = root_dir + "/jayonpu/";
        imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonpu/";

        displayPhoto(imagefile + pic_address_name, pic_address);
        displayPhoto(imagefile + pic_1_name, pic_1);
        displayPhoto(imagefile + pic_2_name, pic_2);
        displayPhoto(imagefile + pic_3_name, pic_3);

        lbl_trx_id.setText(order.getTrx_id());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_order, menu);
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

    private void displayPhoto(String imagepath, ImageView image_view){
        File imagefile = new File(imagepath);
        if(imagefile.exists()){
            Picasso.with(ViewOrderActivity.this).load(imagefile).skipMemoryCache().noFade().into(image_view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_ORDER_ACTIVITY_REQUEST_CODE){

            Long app_id = data.getLongExtra("app_id",0);
            Long merchant_id = data.getLongExtra("merchant_id",0);

            order = Orders.findById(Orders.class,id);
            refreshData();
        }

    }
}
