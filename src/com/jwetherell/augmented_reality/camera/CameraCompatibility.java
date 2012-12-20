package com.jwetherell.augmented_reality.camera;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;

/**
 * Ensures compatibility with older and newer versions of the API. See the SDK
 * docs for comments
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class CameraCompatibility {

    private static Method getSupportedPreviewSizes = null;
    private static Method mDefaultDisplay_getRotation = null;

    static {
        initCompatibility();
    };

    /**
     * This will fail on older phones (Android version < 2.0)
     */
    private static void initCompatibility() {
        try {
            getSupportedPreviewSizes = Camera.Parameters.class.getMethod("getSupportedPreviewSizes", new Class[] {});
            mDefaultDisplay_getRotation = Display.class.getMethod("getRotation", new Class[] {});
            /* success, this is a newer device */
        } catch (NoSuchMethodException nsme) {
            /* failure, must be older device */
        }
    }

    /**
     * Get the current roation of the device
     * 
     * @param activity
     *            Current running activity
     * @return The rotation of the screen from its "natural" orientation.
     */
    public static int getRotation(Activity activity) {
        int result = 1;
        try {
            Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Object retObj = mDefaultDisplay_getRotation.invoke(display);
            if (retObj != null) result = (Integer) retObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * If it's running on a new phone, let's get the supported preview sizes,
     * before it was fixed to 480 x 320
     * 
     * @param params
     *            Camera's parameters.
     * @return List of supported Camera viewer sizes.
     * @throws RuntimeException
     *             if supported size fails.
     */
    @SuppressWarnings("unchecked")
    public static List<Camera.Size> getSupportedPreviewSizes(Camera.Parameters params) {
        List<Camera.Size> retList = null;

        try {
            Object retObj = getSupportedPreviewSizes.invoke(params);
            if (retObj != null) {
                retList = (List<Camera.Size>) retObj;
            }
        } catch (InvocationTargetException ite) {
            /* unpack original exception when possible */
            Throwable cause = ite.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                /* unexpected checked exception; wrap and re-throw */
                throw new RuntimeException(ite);
            }
        } catch (IllegalAccessException ie) {
            ie.printStackTrace();
        }
        return retList;
    }
}
