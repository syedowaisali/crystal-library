package com.crystal.helpers;

import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by owais.ali on 9/5/2016.
 */
public class CrystalParams extends RequestParams{

    private Map<String, String> mapParams;

    public CrystalParams() {
        super();
        mapParams = new HashMap<>();
    }

    public CrystalParams(Map<String, String> source) {
        super(source);
        mapParams = new HashMap<>();
    }

    public CrystalParams(String key, String value) {
        super(key, value);
        mapParams = new HashMap<>();
    }

    public CrystalParams(Object... keysAndValues) {
        super(keysAndValues);
        mapParams = new HashMap<>();
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        mapParams.put(key, value);
    }

    @Override
    public void put(String key, String value) {
        super.put(key, value);
        mapParams.put(key, value);
    }

    @Override
    public void put(String key, int value) {
        super.put(key, value);
        mapParams.put(key, String.valueOf(value));
    }

    @Override
    public void put(String key, long value) {
        super.put(key, value);
        mapParams.put(key, String.valueOf(value));
    }

    public Map<String, String> getMapParams(){
        return mapParams;
    }
}
