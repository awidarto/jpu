package com.kickstartlab.android.jayonpickup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by awidarto on 3/3/14.
 */
public class KeyDialog extends DialogPreference {

    RequestQueue drq;

    ProgressBar progressBar;

    public KeyDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.dialog_key_request);



    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setTitle("Device Key Request");
        builder.setPositiveButton(R.string.request_key, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

    }

    @Override
    protected void onBindDialogView(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.keyProgressBar);
        super.onBindDialogView(view);
    }
}
