package com.omdd.gdyb.main;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.omdd.gdyb.utils.DateUtil;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public static void testGetPath() throws Exception
    {
        Log.e("exception", DateUtil.getFilePath());
        System.out.println(DateUtil.getFilePath());
    }
}