package com.sda5.double2app.helper;

import android.widget.EditText;

public class EditTextHelper {

    public static boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().equals("");
    }

}
