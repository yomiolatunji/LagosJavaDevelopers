package com.yomiolatunji.andela.lagosjavadev.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.text.MessageFormat;


public class UserDetailFragment extends Fragment {

    public static final String ARG_USERNAME = "username";
    UserDetailBinding binding;
    User user;

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
        showLoading(false);
        binding.setUser(user);

        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_person_outline, null);
        if (user.avatarUrl != null)
            PicassoSingleton.getPicasso()
                    .load(user.avatarUrl)
                    .placeholder(vectorDrawableCompat)
                    .error(vectorDrawableCompat)
                    .into(binding.avatar);
//        .fit()
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
            binding.load.setVisibility(View.VISIBLE);
            //binding.contentWrapper.setVisibility(View.GONE);
        }else{
            binding.load.setVisibility(View.GONE);
            //binding.contentWrapper.setVisibility(View.VISIBLE);
        }
    }
}
