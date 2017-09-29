package com.bernard.hollarena.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bernard.hollarena.fragment.ChatsFragment;
import com.bernard.hollarena.fragment.FriendsFragment;
import com.bernard.hollarena.fragment.RequestFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private String[] titleList = {"Request","Chat","Friend"};
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return  requestFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList[position];
    }
}
