package com.crystal.interfaces;

import org.json.JSONObject;

/**
 * Created by owais.ali on 5/4/2016.
 */
public interface OnWSResponse {
    void onData(JSONObject data, int requestCode);
    void noData(String message, int requestCode);
    void onError(String error, int requestCode);
    void onCancel(int requestCode);
}
