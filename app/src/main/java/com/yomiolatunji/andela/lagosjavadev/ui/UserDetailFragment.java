package com.yomiolatunji.andela.lagosjavadev.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yomiolatunji.andela.lagosjavadev.R;
import com.yomiolatunji.andela.lagosjavadev.data.DataLoadingCallback;
import com.yomiolatunji.andela.lagosjavadev.data.PicassoSingleton;
import com.yomiolatunji.andela.lagosjavadev.data.api.GithubApiNetworkService;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;
import com.yomiolatunji.andela.lagosjavadev.databinding.UserDetailBinding;
import com.yomiolatunji.andela.lagosjavadev.ui.customtabs.CustomTabActivityHelper;

import java.lang.reflect.Field;
import java.text.MessageFormat;


public class UserDetailFragment extends Fragment {

    public static final String ARG_USERNAME = "username";
    UserDetailBinding binding;
    User user;
    private FragmentManager fragmentManager;
    private UserPagerAdapter mUserPagerAdapter;

    public UserDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_USERNAME)) {
            loadUser(getArguments().getString(ARG_USERNAME));

            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                //appBarLayout.setTitle(mItem.content);
//            }
        } else {
            loadUser(getActivity().getIntent().getStringExtra(UserDetailFragment.ARG_USERNAME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_detail, container, false);
        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.user_detail)).setText(mItem.details);
//        }
        //showLoading(true);

        return binding.getRoot();
    }

    private void loadUser(String username) {
        GithubApiNetworkService githubApi = GithubApiNetworkService.newInstance(getActivity());
        githubApi.getUser(username, new DataLoadingCallback<User>() {

            @Override
            public void onResponse(User data) {
                user = data;
                if (isAdded())
                    bindUser();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void bindUser() {
        //showLoading(false);
        binding.setUser(user);

        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_person_outline, null);
        if (user.avatarUrl != null)
            PicassoSingleton.getPicasso()
                    .load(user.avatarUrl)
                    .placeholder(vectorDrawableCompat)
                    .error(vectorDrawableCompat).fit()
                    .into(binding.avatar);
//
        binding.userUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(user.html_url);
            }
        });
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, MessageFormat.format("Check out this awesome developer @{0}, {1}", user.login, user.html_url));
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            }
        });
        fragmentManager = this.getChildFragmentManager();
        if (mUserPagerAdapter == null)
            mUserPagerAdapter = new UserPagerAdapter(fragmentManager,user.followers,user.following,user.publicRepos);
        mUserPagerAdapter.notifyDataSetChanged();
        // Set up the ViewPager with the sections adapter.
        binding.viewPager.setAdapter(mUserPagerAdapter);

        binding.tab.setupWithViewPager(binding.viewPager);
    }

    private void openLink(String url) {
        CustomTabActivityHelper.openCustomTab(
                getActivity(),
                new CustomTabsIntent.Builder()
                        .setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                        .addDefaultShareMenuItem()
                        .build(),
                Uri.parse(url));
    }

    private void showLoading(boolean b){
        if(b){
           // binding.load.setVisibility(View.VISIBLE);
            //binding.contentWrapper.setVisibility(View.GONE);
        }else{
            //binding.load.setVisibility(View.GONE);
            //binding.contentWrapper.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public class UserPagerAdapter extends FragmentPagerAdapter {
        private int followerCount;
        private int followingCount;
        private int repoCount;

        public UserPagerAdapter(FragmentManager fm, int followerCount, int followingCount, int repoCount) {
            super(fm);
            this.followerCount = followerCount;
            this.followingCount = followingCount;
            this.repoCount = repoCount;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle arguments = new Bundle();
            arguments.putString(UserDetailFragment.ARG_USERNAME, user.login);
            FollowersFragment followersFragment = new FollowersFragment();
            followersFragment.setArguments(arguments);
            switch (position) {
                case 0:
                    return followersFragment;
                case 1:
                    return followersFragment;
                case 2:
                    return followersFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return followerCount+" Followers";
                case 1:
                    return followingCount+" Following";
                case 2:
                    return repoCount+" Repos";
            }
            return "";
        }
    }
}
