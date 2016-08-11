package com.crystal.interfaces;

/**
 * Created by owais.ali on 5/25/2016.
 */
public interface OnPermissionResult {
    void onPermissionSuccess(int requestCode);
    void onPermissionFailure(int requestCode);
}
