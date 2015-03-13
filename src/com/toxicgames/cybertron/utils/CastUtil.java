package com.toxicgames.cybertron.utils;

/**
 * Created by mor on 13.03.2015.
 */
public class CastUtil {

    public static double extractDouble(Object value)
    {
        if (value instanceof Integer)
        {
            return ((Integer) value).doubleValue();
        }

        try {
            return ((Double) value).doubleValue();
        } catch (ClassCastException e) {
            // Какая-то обработка того что пришли кривые данные, может в лог записать или еще как поругаться...
            return 0;
        }


//        return ((Double) value).doubleValue();
    }

}
