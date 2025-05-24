package com.duc.chatting.utilities;



import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AppHelper {
    public static List<Integer> getAvatarDrawables(Context context) {
        List<Integer> avatars = new ArrayList<>();
        String packageName = context.getPackageName();

        for (int i = 1; i <= 10; i++) { // Có thể tăng giới hạn nếu có nhiều hơn
            String name = "ava" + i;
            int resId = context.getResources().getIdentifier(name, "drawable", packageName);
            if (resId != 0) {
                avatars.add(resId);
            }
        }
        return avatars;
    }
}

