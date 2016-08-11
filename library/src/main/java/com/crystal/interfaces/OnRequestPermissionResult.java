package com.crystal.interfaces;

/**
 * Created by owais.ali on 5/4/2016.
 */
public interface OnRequestPermissionResult {
    void permissionResult(int requestCode, String[] permissions, int[] grantResults);
}
