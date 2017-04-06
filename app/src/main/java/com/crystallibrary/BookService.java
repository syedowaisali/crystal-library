package com.crystallibrary;

import android.content.Context;

import com.crystal.base.VolleyService;
import com.crystal.models.ServiceInfo;
import com.crystal.utilities.Api;

import org.json.JSONObject;

/**
 * Created by owais.ali on 9/5/2016.
 */
public class BookService extends VolleyService<BookService, Book> {

    public BookService(Context context) {
        super(context);
    }

    @Override
    protected boolean isCancelable() {
        return true;
    }

    @Override
    protected boolean cacheEnable() {
        return true;
    }

    @Override
    public Book getDataModel(JSONObject jsonData, ServiceInfo serviceInfo) {
        Book book = new Book();
        book.setId(0);
        return book;
    }

    @Override
    public String getApiUrl() {
        return "http://maf3.trafficdemos.net/app_dev.php/api/app/v1/maf_user/user_login";
    }

    @Override
    public Api.MethodType getMethodType() {
        return Api.MethodType.POST;
    }

    @Override
    public BookService getThis() {
        return this;
    }
}
