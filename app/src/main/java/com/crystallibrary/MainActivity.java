package com.crystallibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.base.BaseActivity;
import com.crystal.base.BaseModel;
import com.crystal.helpers.CrystalParams;
import com.crystal.helpers.SharedPrefs;
import com.crystal.widgets.CTLEditText;

import org.json.JSONObject;

/**
 * Created by owais.ali on 7/22/2016.
 */
public class MainActivity extends BaseActivity {

    SharedPrefs sharedPrefs;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        sharedPrefs = new SharedPrefs(this);

        final TextView info = getView(R.id.tvInfo);

        final CTLEditText editText = getView(R.id.etField);
        editText.setValidateListener(new CTLEditText.ValidateListener() {
            @Override
            public void onValidate(boolean isValid) {
                info.setText(isValid ? "Valid Email." : "Not Valid Email.");

            }
        });

        bindClickListener(R.id.btnValidate, new View.OnClickListener() {
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

    @Override
    public void onData(JSONObject jsonData, BaseModel dataModel, int requestCode) {
        toast(dataModel.toString());
    }

    @Override
    public void noData(String message, int requestCode) {
        toast(message);
    }

    @Override
    public void onError(String error, int requestCode) {
        toast(error);
    }

    @Override
    public void onCancel(int requestCode) {
        toast("Cancel");
    }
}
