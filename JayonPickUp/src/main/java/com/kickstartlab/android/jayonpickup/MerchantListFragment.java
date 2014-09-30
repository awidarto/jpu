package com.kickstartlab.android.jayonpickup;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by awidarto on 9/26/13.
 */
public class MerchantListFragment extends ListFragment {

    RequestQueue requestQueue;

    private String url = "http://www.jayonexpress.com/api/mobile/v2/merchant/key/12345678/format/json";

    Context context;

    ArrayList<String> merchantArray = new ArrayList<String>();

    //ArrayAdapter<Merchant> adapter;
    MerchantAdapter adapter;

    ProgressBar mProgress;

    OnMerchantSelectedListener mCallback;

    EditText searchBox;

    public interface OnMerchantSelectedListener{
        public void onMerchantSelected(Long merchant_id, String merchant_name, String merchant_address);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        List<Merchant> merchantsList = (List<Merchant>) Select.from(Merchant.class).orderBy("merchantname").list();

        //adapter = new ArrayAdapter<Merchant>(getActivity(),R.layout.simple_item,merchantsList);
        adapter = new MerchantAdapter(getActivity());
        adapter.setData(merchantsList);

        requestQueue = Volley.newRequestQueue(getActivity());

        setListAdapter(adapter);
    }

    public void refreshView(){
        String searchTerm = searchBox.getText().toString();

        List<Merchant> merchantsList;

        if("".equalsIgnoreCase(searchTerm)){
            merchantsList = (List<Merchant>) Select.from(Merchant.class).orderBy("merchantname").list();
        }else{
            merchantsList = (List<Merchant>) Select.from(Merchant.class).where(Condition.prop("merchantname").like("%" + searchTerm + "%")).orderBy("merchantname").list();
        }

        //adapter.clear();
        //adapter = new ArrayAdapter<Merchant>(getActivity(),R.layout.simple_item,merchantsList);
        adapter = null;
        adapter = new MerchantAdapter(getActivity());
        adapter.setData(merchantsList);

        requestQueue = Volley.newRequestQueue(getActivity());

        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.merchant_list,container, false);

        //ImageButton btMcRefresh = (ImageButton) view.findViewById(R.id.merchant_list_refresh);

        searchBox = (EditText) view.findViewById(R.id.search_merchant);

        //mProgress = (ProgressBar) view.findViewById(R.id.merchant_progress_bar);

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                refreshView();
                return false;
            }
        });

        /*
        btMcRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                refreshView();
            }
        });
        */

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Merchant merchant = (Merchant) l.getItemAtPosition(position);

        mCallback.onMerchantSelected(merchant.getMerchant_id(), merchant.getMerchantname(), merchant.getMc_street());

        Log.i("item id",String.valueOf(merchant.getMerchant_id()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMerchantSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMerchantSelectedListener");
        }
    }

    public void downloadMerchant(){

        //mProgress.setVisibility(View.VISIBLE);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

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
                    //mProgress.setVisibility(View.INVISIBLE);

                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }

                List<Merchant> merchantsList = (List<Merchant>) Select.from(Merchant.class).orderBy("merchantname").list();

                adapter = null;
                //adapter = new ArrayAdapter<Merchant>(getActivity(),android.R.layout.simple_list_item_1,merchantsList);
                adapter = new MerchantAdapter(getActivity());
                adapter.setData(merchantsList);

                setListAdapter(adapter);

                //mProgress.setVisibility(View.INVISIBLE);

                Toast.makeText(context, "Merchant Data Updated", Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                //mProgress.setVisibility(View.INVISIBLE);

                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        );

        requestQueue.add(jsObjRequest);


    }

}