package com.crystal.base;

import com.crystal.models.ServiceInfo;

import java.io.Serializable;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseModel<T extends BaseModel<T>> implements Serializable {

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    private int id;
    private ServiceInfo serviceInfo;

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public BaseModel(){}

    //////////////////////////////////////////////
    // SETTER'S
    //////////////////////////////////////////////

    public T setId(final int id){
        this.id = id;
        return getThis();
    }

    public T setServiceInfo(final ServiceInfo serviceInfo){
        this.serviceInfo = serviceInfo;
        return getThis();
    }

    //////////////////////////////////////////////
    // GETTER'S
    //////////////////////////////////////////////

    public int getId(){
        return this.id;
    }

    public ServiceInfo getServiceInfo(){
        return this.serviceInfo;
    }

    //////////////////////////////////////////////
    // ABSTRACT METHOD
    //////////////////////////////////////////////

    public abstract T getThis();
}
