package com.crystal.helpers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by owais.ali on 5/4/2016.
 */
public class AppHelper {

    private static MediaPlayer mp;

    // prevent object creating
    private AppHelper(){}

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isValidEmail(String email){
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    public static String getDebugKeyHash(Context ctx, String package_name){
        String result = "";
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    package_name,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                result = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            result = e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            result = e.getMessage();
        }

        return result;
    }

    public static Bitmap removeRotate(Bitmap bmp, String filepath) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bmp = rotateImage(bmp, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bmp = rotateImage(bmp, 180);
                break;
            // etc.
        }

        return bmp;
    }

    public static Bitmap rotateImage(Bitmap bitmap, int angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap bmp = bitmap;
        try {
            bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError var5) {
            var5.printStackTrace();
        }

        return bmp;
    }

    public static void hideKeyboard(EditText input, Context ctx){
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static String timeConversion(int totalSeconds) {

        return timeConversion(totalSeconds, true);
    }

    public static void vibrate(Context ctx, int millis){
        ((Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(millis);
    }

    public static void vibrate(Context ctx){
        vibrate(ctx, 1000);
    }

    public static String timeConversion(int totalSeconds, boolean hour) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return ((hour) ? String.format("%02d", hours) + ":" : "") + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    public static void playSound(String sound_path){
        if(new File(sound_path).exists()){
            if(mp != null){
                mp.stop();
                mp.release();
                mp = null;
            }

            try {
                mp = new MediaPlayer();
                mp.setDataSource(sound_path);
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("AppHelper", sound_path + " File not exists");
        }
    }

    public static void playSoundFromAsset(Context ctx, String filename){

        if(mp != null){
            mp.stop();
            mp.release();
            mp = null;
        }

        boolean play = true;

        if(play){
            try {
                AssetFileDescriptor descriptor = ctx.getAssets().openFd(filename);

                mp = new MediaPlayer();
                mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                });
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d("AppHelper", "sound " + filename + " not found.");
            }
        }
    }

    public static void stopSound(){
        if(mp != null){
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist * 0.000621371;
    }

    // M -> Miles, K -> Kilometers, N -. Nautical Miles
    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    // get address by coords
    public static String getAddress(Context ctx, double lat, double lon){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(ctx, Locale.getDefault());
        String address = "";

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            address += " " + addresses.get(0).getLocality();
            address += " " + addresses.get(0).getAdminArea();
            address += " " + addresses.get(0).getCountryName();
            //String postalCode = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static boolean isValidJSON(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static Bitmap takeScreenshot(View view, int resource){
        View rootView = view.findViewById(resource).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public static String saveBitmap(Bitmap bitmap, String path){
        File imagePath = new File(path);
        FileOutputStream fos;
        try{
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fos.close();
        }
        catch (FileNotFoundException e){
            Log.e("GREC", e.getMessage(), e);
        }
        catch (IOException e){
            Log.e("GREC", e.getMessage(), e);
        }

        return imagePath.getAbsolutePath();
    }

    public static void turnGPSOn(Context ctx){
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    public static void turnGPSOff(Context ctx){
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    public static void fireNotification(Context ctx, CharSequence title, CharSequence text){

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // this is it, we'll build the notification!
        NotificationCompat.Builder notif = new NotificationCompat.Builder(ctx)
                //.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setSound(soundUri);

        // get an instance of the notification service
        NotificationManager notifManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // generate random id
        Random rand = new Random();
        int id = rand.nextInt(99999);

        // build the notification and issue it
        notifManager.notify(id, notif.build());
    }

    public static String getFormatedDate(String date){
        String new_date = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d H:mm:s");
        try {
            Date d = sdf.parse(date);
            sdf.applyPattern("MMM d, yyyy");
            new_date = sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new_date;
    }
}
