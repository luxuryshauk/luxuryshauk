package com.luxuryshauk.Utils;

import com.google.firebase.database.FirebaseDatabase;
import com.luxuryshauk.models.notification;

public class SetNotification {

    public void SetNotification(){}

    public void notify(String by_user,String not_id, String seen, String date, String type,String mUserID,String targetid)
    {
        notification n = new notification();
        n.setBy_user(by_user);
        n.setSeen(seen);
        n.setType(type);
        n.setTime(date);
        n.setNot_id(not_id);
        n.setTarget(targetid);
        FirebaseDatabase.getInstance().getReference().child("notification").child(mUserID).child(not_id).setValue(n);
    }
}
