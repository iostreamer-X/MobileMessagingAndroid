package io.streamer.oose;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by iostreamer on 14/11/15.
 */
public class Util {
    static ArrayList<String> getContacts(Context context){
        ArrayList<String> numbers = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    // Query phone here. Covered next
                    Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(!phoneNumber.contains("-") && phoneNumber.length()==10){
                            numbers.add(phoneNumber.replace("+91","").replace(" ",""));
                        }
                    }
                    phones.close();
                }

            }
        }
        return numbers;
    }

    public static void saveDetails(String name,String pass,String phone,Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("pass", pass);
        editor.putString("phone", phone);
        editor.apply();
    }

    public static String loadPhone(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString("phone",null);
    }

    public static String loadName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString("name",null);
    }

    public static String loadPass(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString("pass",null);
    }

    public static void getContactsFromServer(Context context){
        RequestParams contacts = new RequestParams();
        contacts.put("contacts",mkString(getContacts(context), "[", ",", "]"));

        HttpReq.get("/getcontacts", contacts, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String response= new String(responseBody);
                System.out.println(response);

                try {
                    JSONArray array = new JSONArray(response);
                    Main2Activity.contacts = array;
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        Main2Activity.addContact(object.getString("name"),object.getString("phone"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }


    public static void signUp(String name, String pass, String phone, final View view){
        RequestParams details = new RequestParams();
        details.put("name",name);
        details.put("pass",pass);
        details.put("phone",phone);
        HttpReq.get("/signup", details, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                System.out.println(new String(responseBody));
                Snackbar.make(view, "Signed up successfully", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view, new String(responseBody), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public static void login(String name, String pass, String phone, final View view, final Context context){
        RequestParams details = new RequestParams();
        details.put("name",name);
        details.put("pass",pass);
        details.put("phone",phone);
        HttpReq.get("/login", details, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                System.out.println(new String(responseBody));
                Intent i = new Intent(context, Main2Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                ((Activity)context).finish();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view, new String(responseBody), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public static void loadChatHistory(String inb, String chathead){
        RequestParams details = new RequestParams();
        details.put("inb",inb);
        details.put("chathead",chathead);
        HttpReq.get("/getchat", details, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                System.out.println(response);
                try {
                    JSONArray array = new JSONArray(response);
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);

                        ChatMessage message = new ChatMessage();
                        if(object.getString("sender").equals(Main2Activity.phone)){
                            message.setMe(true);
                        }else{
                            message.setMe(false);
                        }
                        message.setMessage(object.getString("data"));

                        Chat.displayMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public static void updateChat(final String inb, final String chathead){
        RequestParams details = new RequestParams();
        details.put("inb",inb);
        details.put("chathead",chathead);
        HttpReq.sget("/buffergetchat", details, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                System.out.println(response);
                try {
                    JSONArray array = new JSONArray(response);
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);

                        ChatMessage message = new ChatMessage();
                        if(object.getString("sender").equals(Main2Activity.phone)){
                            message.setMe(true);
                        }else{
                            message.setMe(false);
                        }
                        message.setMessage(object.getString("data"));

                        Chat.displayMessage(message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });


    }


    public static void sendMessage(String sender, String receiver, final String data){
        RequestParams details = new RequestParams();
        details.put("sender",sender);
        details.put("receiver",receiver);
        details.put("data",data);
        HttpReq.get("/message", details, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                System.out.println(response);
                ChatMessage message = new ChatMessage();
                message.setMe(true);
                message.setMessage(data);
                Chat.displayMessage(message);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public static String mkString(Iterable<?> values, String start, String sep, String end){
        // if the array is null or empty return an empty string
        if(values == null || !values.iterator().hasNext())
            return "";

        // move all non-empty values from the original array to a new list (empty is a null, empty or all-whitespace string)
        List<String> nonEmptyVals = new LinkedList<String>();
        for (Object val : values) {
            if(val != null && val.toString().trim().length() > 0){
                nonEmptyVals.add(val.toString());
            }
        }

        // if there are no "non-empty" values return an empty string
        if(nonEmptyVals.size() == 0)
            return "";

        // iterate the non-empty values and concatenate them with the separator, the entire string is surrounded with "start" and "end" parameters
        StringBuilder result = new StringBuilder();
        result.append(start);
        int i = 0;
        for (String val : nonEmptyVals) {
            if(i > 0)
                result.append(sep);
            result.append(val);
            i++;
        }
        result.append(end);

        return result.toString();
    }
    public static String mkString(Object[] values, String start, String sep, String end){
        return mkString(Arrays.asList(values), start, sep, end);
    }

}
