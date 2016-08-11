package com.crystallibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.base.BaseActivity;
import com.crystal.widgets.CTLEditText;

/**
 * Created by owais.ali on 7/22/2016.
 */
public class MainActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        final TextView info = getView(R.id.tvInfo);

        final CTLEditText editText = getView(R.id.etField);
        editText.setTextValidateListener(new CTLEditText.TextValidateListener() {
            @Override
            public void onValidate(boolean isValid) {
                info.setText(isValid ? "Valid Email." : "Not Valid Email.");
            }
        });

        findViewById(R.id.btnValidate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.isValidate()){
                    Toast.makeText(MainActivity.this, "valid", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
