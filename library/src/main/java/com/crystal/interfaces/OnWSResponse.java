package com.crystal.interfaces;

import com.crystal.base.BaseModel;

import org.json.JSONObject;

/**
 * Created by owais.ali on 5/4/2016.
 */
public interface OnWSResponse<M extends BaseModel> {
    void onData(JSONObject jsonData, M dataModel, int requestCode);
    void noData(String message, int requestCode);
    void onError(String error, int requestCode);
    void onCancel(int requestCode);
}
