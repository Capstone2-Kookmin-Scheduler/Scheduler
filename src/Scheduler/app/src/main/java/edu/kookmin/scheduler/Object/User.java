package edu.kookmin.scheduler.Object;

import java.util.HashMap;
import java.util.Map;

/**
 * User 객체
 * @author - 구윤모, 이주형
 * @start - 2020.10.11
 * @finish - 2020.10.11
 */
public class User {
    private String email;
    private int lateCount;

    public User(){}
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

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("email",email);
        result.put("lateCount",lateCount);

        return result;
    }

}
