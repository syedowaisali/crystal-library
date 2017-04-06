package com.crystallibrary;

import android.os.Bundle;
import android.view.View;

import com.crystal.base.BaseActivity;
import com.crystal.base.BaseModel;
import com.crystal.helpers.CrystalParams;
import com.crystal.helpers.SharedPrefs;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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

        /*final TextView info = getView(R.id.tvInfo);

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
        });*/

        bindClickListener(R.id.btnNetworkRequest, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkRequest();
            }
        });
        bindClickListener(R.id.btnUpload, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private File getFileFromAsset(final String path){
        File file = new File(getCacheDir() + "/" + path);
        try {

            InputStream is = getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    private void networkRequest(){

        CrystalParams params = new CrystalParams();
        //params.add("userid", "g@g.g");
        params.add("password", "gggggg");
        params.add("deviceType", "android");
        params.add("deviceToken", "");
        params.add("account_type", "1");

        BookService bookService = new BookService(this);
        bookService.setParameter(params).execute(this);
    }

    @Override
    public void onData(JSONObject jsonData, BaseModel dataModel, int requestCode) {
        toast(dataModel.toString());
    }

}
