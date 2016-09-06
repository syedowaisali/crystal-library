package com.crystal.base;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseModel<T extends BaseModel<T>> {

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    private String id;

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public BaseModel(){}

    //////////////////////////////////////////////
    // SETTER'S
    //////////////////////////////////////////////

    public T setId(final String id){
        this.id = id;
        return getThis();
    }

    //////////////////////////////////////////////
    // GETTER'S
    //////////////////////////////////////////////

    public String getId(){
        return this.id;
    }

    //////////////////////////////////////////////
    // ABSTRACT METHOD
    //////////////////////////////////////////////

    public abstract T getThis();
}