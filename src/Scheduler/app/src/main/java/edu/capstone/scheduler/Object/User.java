package edu.capstone.scheduler.Object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String email;
    private int lateCount;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLateCount() {
        return lateCount;
    }

    public void setLateCount(int latCount) {
        this.lateCount = lateCount;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("email",email);
        result.put("lateCount",lateCount);

        return result;
    }

}
