package com.crystal.helpers;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by owais.ali on 9/5/2016.
 */
public class CMParams extends RequestParams{

    private Map<String, String> mapParams;
    private Map<String, File> fileParams;

    public CMParams() {
        super();
        mapParams = new HashMap<>();
        fileParams = new HashMap<>();
    }

    public CMParams(Map<String, String> source) {
        super(source);
        mapParams = new HashMap<>();
        fileParams = new HashMap<>();
    }

    public CMParams(String key, String value) {
        super(key, value);
        mapParams = new HashMap<>();
        fileParams = new HashMap<>();
    }

    public CMParams(Object... keysAndValues) {
        super(keysAndValues);
        mapParams = new HashMap<>();
        fileParams = new HashMap<>();
    }

    public void add(String key, double value){
        add(key, String.valueOf(value));
    }

    public void add(String key, long value){
        add(key, String.valueOf(value));
    }

    public void add(String key, float value){
        add(key, String.valueOf(value));
    }

    public void add(String key, int value){
        add(key, String.valueOf(value));
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

    @Override
    public void put(String key, File file) throws FileNotFoundException {
        super.put(key, file);
        fileParams.put(key, file);
    }

    public Map<String, String> getMapParams(){
        return mapParams;
    }

    public Map<String, File> getFileParams(){
        return fileParams;
    }
}
