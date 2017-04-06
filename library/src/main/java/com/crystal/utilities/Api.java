package com.crystal.utilities;

/**
 * Created by owais.ali on 5/4/2016.
 */
public class Api {

    // tag for logging info
    public static final String TAG = "crystal=> ";

    // web service data key
    public static final String DATA = "data";

    // web service response status
    public static final class Status{
        public static final String STATUS  = "status";
        public static final String STATUS_CODE  = "statusCode";
        public static final String MESSAGE = "message";
        public static final String SUCCESS = "1";
        public static final String LOCALE = "locale";
    }

    // web service method type get - post
    public enum MethodType{
        GET, POST
    }

}
