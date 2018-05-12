package com.gmaxgps.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmaxgps.R;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.Consts;

public class ConfirmInfoActivity extends AppCompatActivity {

    ImageView back = null;
    EditText edt_first_name = null;
    EditText edt_last_name = null;
    EditText edt_email = null;
    EditText edt_phone = null;
    TextView submit = null;
    String first_name = "", last_name = "", email = "", phone = "";
    Context ct = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info);
        back = (ImageView) findViewById(R.id.back);
        edt_first_name = (EditText) findViewById(R.id.edt_first_name);
        edt_last_name = (EditText) findViewById(R.id.edt_last_name);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        submit = (TextView) findViewById(R.id.submit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setValue();
    }

    private void setValue() {
        first_name = AppSession.getValue(ct, Consts.FIRST_NAME);
        last_name = AppSession.getValue(ct, Consts.LAST_NAME);
        email = AppSession.getValue(ct, Consts.EMAIL);

        if (first_name != null && !first_name.equalsIgnoreCase("")) {
            edt_first_name.setText(first_name);
        }
        if (last_name != null && !last_name.equalsIgnoreCase("")) {
            edt_last_name.setText(last_name);
        }
        if (email != null && !email.equalsIgnoreCase("")) {
            edt_email.setText(email);
        }

    }

}
