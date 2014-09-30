package com.kickstartlab.android.jayonpickup;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kickstartlab.android.jayonpickup.dummy.DummyContent;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ShopListFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MERCHANT_NAME = "merchant_name";
    private static final String MERCHANT_ADDRESS = "merchant_address";
    private static final String MERCHANT_ID = "merchant_id";

    // TODO: Rename and change types of parameters
    private String merchant_name;
    private String merchant_address;
    private Long merchant_id;

    //ArrayAdapter<Apps> adapter;
    ShopAdapter adapter;

    TextView txt_merchant_name, txt_merchant_address;

    private OnShopSelectedListener sListener;

    // TODO: Rename and change types of parameters
    public static ShopListFragment newInstance(Long merchant_id, String merchant_name, String merchant_address ) {
        ShopListFragment fragment = new ShopListFragment();
        Bundle args = new Bundle();
        args.putString(MERCHANT_NAME, merchant_name);
        args.putString(MERCHANT_ADDRESS, merchant_address);
        args.putLong(MERCHANT_ID,merchant_id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShopListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            merchant_name = getArguments().getString(MERCHANT_NAME);
            merchant_address = getArguments().getString(MERCHANT_ADDRESS);
            merchant_id = getArguments().getLong(MERCHANT_ID);
        }

        List<Apps> appList = (List<Apps>) Select.from(Apps.class).where(Condition.prop("merchantid").eq(1)).list();

        //adapter = new ArrayAdapter<Apps>(getActivity(),R.layout.simple_item,appList);

        adapter = new ShopAdapter(getActivity());
        adapter.setData(appList);

        // TODO: Change Adapter to display your content
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shop_list,container, false);

        txt_merchant_name = (TextView) view.findViewById(R.id.merchant_name);
        txt_merchant_address = (TextView) view.findViewById(R.id.merchant_address);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            sListener = (OnShopSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnShopSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Apps shop = (Apps) l.getItemAtPosition(position);

        if (null != sListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            sListener.onShopSelected(shop.getApp_id(), shop.getMerchant_id(), shop.getApplication_name() );
        }
    }

    public void updateShopList(Long merchant_id, String merchant_name, String merchant_address){
        List<Apps> appList = (List<Apps>) Select.from(Apps.class).where(Condition.prop("merchantid").eq(merchant_id)).orderBy("appid").list();

        /*
        adapter.clear();
        adapter = new ArrayAdapter<Apps>(getActivity(),R.layout.simple_item,appList);
        */

        adapter = null;
        adapter = new ShopAdapter(getActivity());
        adapter.setData(appList);

        txt_merchant_address.setText(merchant_address);
        txt_merchant_name.setText(merchant_name);

        // TODO: Change Adapter to display your content
        setListAdapter(adapter);


        if(appList.size() == 0){
            Log.i("merchant","new");
        }else{
            Apps a = appList.get(0);

            Long app_id = a.getApp_id();

            OrderListFragment orderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);

            if (orderListFragment != null) {
                orderListFragment.updateOrderList(app_id, merchant_id, a.getApplication_name() );
                Log.i("app selected", String.valueOf(app_id));
            }else{

            }

        }




    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnShopSelectedListener {
        // TODO: Update argument type and name
        public void onShopSelected(Long app_id, Long merchant_id, String shopname);
    }

}
