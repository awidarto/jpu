package com.kickstartlab.android.jayonpickup;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by awidarto on 9/26/13.
 */
public class OrderListFragment extends ListFragment {

    private static final int ADD_ORDER_ACTIVITY_REQUEST_CODE = 1337;
    private static final int VIEW_ORDER_ACTIVITY_REQUEST_CODE = 6336;
    private static final int CAPTURE_SIGN_ACTIVITY_REQUEST_CODE = 8662;
    private static final int SCAN_CODE_ACTIVITY_REQUEST_CODE = 0;

    private static final String PICKED_UP_STATUS = "pu";
    private static final String WILL_UP_STATUS = "wp";
    private static final String CANCEL_UP_STATUS = "cp";

    Long current_app_id, current_merchant_id;
    ImageButton btAddOrder, btScanCode;

    TextView shop_name, totalCOD, totalDeliveryCost, totalWeight;

    //ArrayAdapter<Orders> adapter;
    OrderAdapter adapter;
    Button btSignature,btUploadSignature;

    ImageView signaturePic;

    String currentdate, today, pustatus, signpath;

    SharedPreferences jexPrefs;

    private OrderListInteraction oAction;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //List<Orders> orderList = (List<Orders>) Select.from(Orders.class).where(Condition.prop("appid").eq(1)).list();

        today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        today = today + "%";

        Log.i("current date", today);

        List<Orders> orderList = (List<Orders>) Select.from(Orders.class).where(Condition.prop("appid").eq(1))
                .where( Condition.prop("CREATEDATETIME").like(today) ).orderBy("appid").list();

        //adapter = new ArrayAdapter<Orders>(getActivity(),android.R.layout.simple_list_item_1,orderList);
        adapter = new OrderAdapter(getActivity());
        adapter.setData(orderList);

        // TODO: Change Adapter to display your content
        setListAdapter(adapter);

        jexPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_list,container, false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        currentdate = sdf.format(new Date());

        btAddOrder = (ImageButton) view.findViewById(R.id.order_add_new);

        shop_name = (TextView) view.findViewById(R.id.shop_name);

        totalCOD = (TextView) view.findViewById(R.id.totalCOD);
        totalDeliveryCost = (TextView) view.findViewById(R.id.totalDeliveryCost);
        totalWeight = (TextView) view.findViewById(R.id.totalKg);
        btSignature = (Button) view.findViewById(R.id.btSignature);
        btScanCode = (ImageButton) view.findViewById(R.id.btScanCode);
        btUploadSignature = (Button) view.findViewById(R.id.btUploadSignature);
        signaturePic = (ImageView) view.findViewById(R.id.signaturePic);

        btUploadSignature.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),"sign upload",Toast.LENGTH_SHORT).show();

            }
        });

        btScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                if(jexPrefs.getBoolean("use_ext_scanner", false)){
                    intent = new Intent("com.google.zxing.client.android.SCAN");
                }else{
                    intent = new Intent(getActivity(),ScannerActivity.class);
                }

                intent.putExtra("SCAN_MODE", "SCAN_MODE");
                startActivityForResult(intent, SCAN_CODE_ACTIVITY_REQUEST_CODE);

                Log.i("scan action", "do scan");

            }
        });

        btAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),OrderActivity.class);

                intent.putExtra("app_id", current_app_id);
                intent.putExtra("merchant_id",current_merchant_id);

                startActivityForResult(intent,ADD_ORDER_ACTIVITY_REQUEST_CODE );

                Log.i("current app", String.valueOf(current_app_id));
                Log.i("current merchant", String.valueOf(current_merchant_id));

            }
        });

        btSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signatureIntent = new Intent(getActivity(), SignatureActivity.class);
                String root_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                String saved_file = root_dir + "/jayonpu/";

                File sfolder = new File(saved_file);
                if(!sfolder.exists()){
                    sfolder.mkdirs();
                }

                String sign_file = current_merchant_id + "_" + current_app_id + "_" + currentdate + "_sign.jpg";
                /*
                File sfile = new File(saved_file + sign_file);

                if(sfile.exists()){
                    sfile.delete();
                    sfile = new File(saved_file + delivery_id + "_sign.jpg");
                }
                */
                signatureIntent.putExtra("signature_file", saved_file + sign_file);
                startActivityForResult(signatureIntent, CAPTURE_SIGN_ACTIVITY_REQUEST_CODE);

            }
        });


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            oAction = (OrderListInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OrderListInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        oAction = null;
    }

    @Override
    public void onResume() {

        currentdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonpu/";
        String sign_file = current_merchant_id + "_" + current_app_id + "_" + currentdate + "_sign.jpg";

        Log.i("sign image file",imagefile + sign_file);

        displaySign(imagefile + sign_file,signaturePic);

        refreshList(current_app_id,current_merchant_id);
        super.onResume();
    }

    public void updateOrderList(Long app_id, Long merchant_id, String shopname){

        current_app_id = app_id;
        current_merchant_id = merchant_id;

        Log.i("current app", String.valueOf(current_app_id));
        Log.i("current merchant", String.valueOf(current_merchant_id));

        today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        today = today + "%";

        List<Orders> orderList = (List<Orders>) Select.from(Orders.class).where(Condition.prop("appid").eq(app_id)).where( Condition.prop("CREATEDATETIME").like(today) ).orderBy("appid").list();

        /*
        adapter.clear();
        adapter = new ArrayAdapter<Orders>(getActivity(),R.layout.simple_item,orderList);
        */

        adapter = null;
        adapter = new OrderAdapter(getActivity());
        adapter.setData(orderList);

        setListAdapter(adapter);

        shop_name.setText(shopname);

        Long total_delivery_cost = 0L;
        Long total_cod_cost = 0L;
        Long total_weight = 0L;

        for(int i = 0; i < orderList.size(); i++ ){
            total_weight += orderList.get(i).actual_weight;
            total_cod_cost += orderList.get(i).codsurcharge;
            total_delivery_cost += orderList.get(i).deliverycost;
        }

        totalCOD.setText(String.valueOf(total_cod_cost));
        totalDeliveryCost.setText(String.valueOf(total_delivery_cost));
        totalWeight.setText(String.valueOf(total_weight) + " kg");


    }

    public void refreshList(Long app_id, Long merchant_id){

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        today = today + "%";

        //today = "2014-03-04";//testing only

        Log.i("current app id", app_id.toString());
        Log.i("current date", today);

        List<Orders> orderlist = (List<Orders>) Select.from(Orders.class).where(Condition.prop("appid").eq(app_id)).where( Condition.prop("CREATEDATETIME").like(today) ).orderBy("appid").list();

        /*
        adapter.clear();
        adapter = new ArrayAdapter<Orders>(getActivity(),R.layout.simple_item,orderlist);
        */

        adapter = null;
        adapter  = new OrderAdapter(getActivity());
        adapter.setData(orderlist);

        setListAdapter(adapter);

        Long total_delivery_cost = 0L;
        Long total_cod_cost = 0L;
        Long total_weight = 0L;

        for(int i = 0; i < orderlist.size(); i++ ){
            total_weight += orderlist.get(i).actual_weight;
            total_cod_cost += orderlist.get(i).codsurcharge;
            total_delivery_cost += orderlist.get(i).deliverycost;
        }

        totalCOD.setText(String.valueOf(total_cod_cost));
        totalDeliveryCost.setText(String.valueOf(total_delivery_cost));
        totalWeight.setText(String.valueOf(total_weight) + " kg");

    }

    public void clearList(){

        //adapter.clear();
        adapter = null;
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Orders order = (Orders) l.getItemAtPosition(position);

        Intent intent = new Intent(getActivity(), ViewOrderActivity.class);

        intent.putExtra("order_id", order.getOrder_id());
        intent.putExtra("id", order.getId());

        startActivityForResult(intent, VIEW_ORDER_ACTIVITY_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == SCAN_CODE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                String scresult = data.getStringExtra("SCAN_RESULT");

                Log.i("scan result", scresult);

                //List<Orders> ord = Orders.find(Orders.class, "TRXID = ?", scresult);

                if(scresult.contains("|") && scresult.indexOf("|") > 0 ){
                    String[] sparts = scresult.split("\\|");
                    scresult = sparts[1];
                }

                // work around for bukukita, should be omitted after uniform labeling
                String lastsix = scresult.substring( scresult.length() - 6 , scresult.length() );

                Log.i("last six", lastsix);

                Log.i("captured trx id", scresult);

                String scresultquery;

                if (scresult.length() != 0) {
                    scresultquery = "%" + scresult + "%";
                }else{
                    scresultquery = scresult;
                }

                String lastsixquery;

                if (lastsix.length() != 0) {
                    lastsixquery = "%" + scresult + "%";
                }else{
                    lastsixquery = scresult;
                }

                List<Orders> ord = Orders.find(Orders.class, "( TRXID = ? OR TRXID LIKE ? OR TRXID = ? OR TRXID LIKE ? ) AND MERCHANTID = ? ", scresult ,scresultquery, lastsix, lastsixquery, String.valueOf(current_merchant_id)  );

                if(ord.size() > 0){
                    Orders o = ord.get(0);
                    o.setPickup_status("sudah diambil");
                    o.save();

                    refreshList(current_app_id,current_merchant_id);

                    //String strx = com.kickstartlab.android.lib.Base64.encodeBytes( scresult.getBytes() );

                    String strx =  Base64.encodeToString(scresult.getBytes(),Base64.NO_WRAP);

                    Log.i("base64 encoded", strx);

                    pustatus = "pu";

                    if(null != oAction){
                        oAction.sendStatusCallback(strx, PICKED_UP_STATUS);
                    }

                    Toast.makeText(getActivity(),"Order ditemukan " + o.getTrx_id(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Tidak ada order yang sama",Toast.LENGTH_SHORT).show();
                }
                /*
                try {
                    playSound(getActivity());
                } catch (Exception e) {}
                */
            }
        }

        if(requestCode == ADD_ORDER_ACTIVITY_REQUEST_CODE){
            Long app_id = data.getLongExtra("app_id",0);
            Long merchant_id = data.getLongExtra("merchant_id",0);

            Log.i("back pressed", String.valueOf(requestCode) );
            refreshList(app_id, merchant_id);

        }

        if(requestCode == VIEW_ORDER_ACTIVITY_REQUEST_CODE){
            Long app_id = current_app_id;
            Long merchant_id = current_merchant_id;

            Log.i("back from view", String.valueOf(requestCode) );
            refreshList(app_id, merchant_id);
        }

        if(requestCode == CAPTURE_SIGN_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){

                Toast.makeText(getActivity(),"Signature saved",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void displaySign(String imagepath, ImageView image_view){
        File imagefile = new File(imagepath);
        if(imagefile.exists()){
            Picasso.with(getActivity()).load(imagefile).skipMemoryCache().noFade().into(image_view);
        }
    }

    public void playSound(Context context) throws IllegalArgumentException,
            SecurityException,
            IllegalStateException,
            IOException {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, soundUri);
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getStreamVolume(AudioManager.STREAM_DTMF) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_DTMF);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
    }

    private void uploadSign(String filepath){
        if(new File(filepath).exists()){

            String sdata = prepPicture(filepath);

        }
    }

    public String prepPicture(String filename){

        Bitmap bm = BitmapFactory.decodeFile(filename);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 25, bos);
        byte[] bdata = bos.toByteArray();
        String sdata = Base64.encodeToString(bdata, Base64.DEFAULT);

        return sdata;
    }

    public interface OrderListInteraction{
        public void sendStatusCallback(String trx, String status);

    }

}