package com.example.homespace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class ShareActivity extends AppCompatActivity {

    //constants
    private static final int VERIFY_PERMISSION_REQUEST = 1;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        if (checkPermissionArray(Permissions.PERMISSIONS))
        {
            setupViewPager();
        }
        else
        {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    /**
     * return the current tab number
     * 0 = GalleryFragment
     * 1= PhotoFragment
     * @return
     */
    public int getCurrentTabNumber()
    {
        return mViewPager.getCurrentItem();
    }


    /**
     * verify all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions)
    {
        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSION_REQUEST
        );
    }

    public boolean checkPermissionArray(String[] permissions)
    {

        for (int i=0; i < permissions.length ; i++)
        {
            String check = permissions[i];
            if (!checkPermissions(check))
            {
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission)
    {
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this,permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * Set up view pager for managing the tabs
     */
    private void setupViewPager()
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new CameraFragment());

        mViewPager = (ViewPager) findViewById(R.id.vp_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("GALLERY");
        tabLayout.getTabAt(1).setText("PHOTO");


    }
}