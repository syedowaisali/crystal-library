package com.crystal.models;

import com.crystal.base.BaseModel;

/**
 * Created by owais.ali on 9/19/2016.
 */
public class ServiceInfo extends BaseModel<ServiceInfo> {

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    private boolean status;
    private int statusCode;
    private String message;
    private String locale;

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public ServiceInfo(){}

    //////////////////////////////////////////////
    // SETTER'S
    //////////////////////////////////////////////

    public ServiceInfo setStatus(boolean status){
        this.status = status;
        return this;
    }

    public ServiceInfo setStatusCode(int statusCode){
        this.statusCode = statusCode;
        return this;
    }

    public ServiceInfo setMessage(String message){
        this.message = message;
        return this;
    }

    public ServiceInfo setLocale(final String locale){
        this.locale = locale;
        return this;
    }

    //////////////////////////////////////////////
    // GETTER'S
    //////////////////////////////////////////////

    public boolean getStatus(){
        return this.status;
    }

    public int getStatusCode(){
        return this.statusCode;
    }

    public String getMessage(){
        return this.message;
    }

    public String getLocale() {
        return locale;
    }

    //////////////////////////////////////////////
    // IMPLEMENTATION'S
    //////////////////////////////////////////////

    @Override
    public ServiceInfo getThis() {
        return this;
    }
}
