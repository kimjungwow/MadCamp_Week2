package com.example.cs496_week2;
import android.app.Activity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactsJsonParsing extends Activity {
    static JSONObject jsonObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsonObj = new JSONObject();
    }

    public static String toJSon(ContactModel contact){
        try {
            jsonObj.put("name", contact.getName());
            jsonObj.put("number", contact.getNumber());
            return jsonObj.toString();
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
        return null;
    }
}