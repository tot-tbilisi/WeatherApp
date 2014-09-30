package ge.tot.weatherapp;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import ge.tot.weatherapp.ui.MyActivity;

/**
 * BasicUITest
 */
public class BasicUITest extends ActivityInstrumentationTestCase2<MyActivity> {

    public BasicUITest() {
        super(MyActivity.class);
    }

    public void testListItemClick() {
        Assert.assertEquals(true, false);
    }

}
