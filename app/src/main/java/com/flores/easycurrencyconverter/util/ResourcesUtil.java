package com.flores.easycurrencyconverter.util;

import android.util.Log;

import java.lang.reflect.Field;

public class ResourcesUtil {

    /**
     * Finds the resource ID for the current application's resources.
     *
     * @param resourceClass Resource class to find resource in.
     *                      Example: R.string.class, R.layout.class, R.drawable.class
     * @param name          Name of the resource to search for.
     * @return The id of the resource or -1 if not found.
     */
    public static int getResourceByName(Class<?> resourceClass, String name) {
        int id = -1;
        if (name == null) return id;

        //changed resource name with reserved java word
        if (name.equals("try")) name += "a";

        try {
            if (resourceClass != null) {
                final Field field = resourceClass.getField(name);
                id = field.getInt(null);
            }
        } catch (final Exception e) {
            Log.e("GET_RESOURCE_BY_NAME: ", e.toString());
            e.printStackTrace();
        }
        return id;
    }
}
