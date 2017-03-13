package com.yomiolatunji.andela.lagosjavadev.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yomiolatunji.andela.lagosjavadev.R;
import com.yomiolatunji.andela.lagosjavadev.data.DataLoadingCallback;
import com.yomiolatunji.andela.lagosjavadev.data.PicassoSingleton;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;
import com.yomiolatunji.andela.lagosjavadev.data.source.api.GithubApiNetworkService;
import com.yomiolatunji.andela.lagosjavadev.ui.customtabs.CustomTabActivityHelper;

import java.text.MessageFormat;
import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserDetailFragment extends Fragment {

    public static final String ARG_USERNAME = "username";
    //UserDetailBinding binding;
    User user;
    View.OnClickListener shareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, MessageFormat.format("Check out this awesome developer @{0}, {1}", user.login, user.html_url));
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        }
    };
    private View rootView;
    private CircleImageView avatar;
    private ProgressBar loading;
    private TextView name;
    private TextView username;
    private ImageButton share;
    private TextView bio;
    private TextView url;
    private TextView followerCount;
    private TextView followingCount;
    private TextView repoCount;

    public UserDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_USERNAME)) {
            loadUser(getArguments().getString(ARG_USERNAME));
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getArguments().getString(ARG_USERNAME));
        } else {
            loadUser(getActivity().getIntent().getStringExtra(UserDetailFragment.ARG_USERNAME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_detail, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View parent) {
        loading = (ProgressBar) parent.findViewById(R.id.loading);
        avatar = (CircleImageView) parent.findViewById(R.id.avatar);
        username = (TextView) parent.findViewById(R.id.username);
        name = (TextView) parent.findViewById(R.id.user_name);
        url = (TextView) parent.findViewById(R.id.user_url);
        bio = (TextView) parent.findViewById(R.id.user_bio);
        share = (ImageButton) parent.findViewById(R.id.share);
        followerCount = (TextView) parent.findViewById(R.id.follower_count);
        followingCount = (TextView) parent.findViewById(R.id.following_count);
        repoCount = (TextView) parent.findViewById(R.id.repo_count);

        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_person_outline, null);
        avatar.setImageDrawable(vectorDrawableCompat);
    }

    private void loadUser(String username) {
        GithubApiNetworkService githubApi = GithubApiNetworkService.newInstance(getActivity());
        githubApi.getUser(username, new DataLoadingCallback<User>() {
            @Override
            public void onResponse(User data) {
                showLoading(false);
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
//        binding.setUser(user);
        name.setText(user.name);
        username.setText(user.login);
        url.setText(user.html_url);
        if (user.bio != null && user.bio != "null") bio.setText(user.bio);

        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_person_outline, null);
        if (user.avatarUrl != null)
            PicassoSingleton.getPicasso()
                    .load(user.avatarUrl)
                    .placeholder(vectorDrawableCompat)
                    .error(vectorDrawableCompat).fit()
                    .into(avatar);

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(user.html_url);
            }
        });
        share.setOnClickListener(shareClickListener);
        setFollowerCount();
        setFollowingCount();
        setRepoCount();
    }

    private void setFollowerCount() {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_follower, null);
        followerCount.setCompoundDrawablesWithIntrinsicBounds(null, vectorDrawableCompat, null, null);
        followerCount.setText(getResources().getQuantityString(R.plurals.follower_count,
                user.followers, NumberFormat.getInstance().format(user.followers)));

    }

    private void setFollowingCount() {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_following, null);
        followingCount.setCompoundDrawablesWithIntrinsicBounds(null, vectorDrawableCompat, null, null);
        followingCount.setText(getResources().getQuantityString(R.plurals.following_count,
                user.following, NumberFormat.getInstance().format(user.following)));

    }

    private void setRepoCount() {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_repo, null);
        repoCount.setCompoundDrawablesWithIntrinsicBounds(null, vectorDrawableCompat, null, null);
        repoCount.setText(getResources().getQuantityString(R.plurals.repo_count,
                user.publicRepos, NumberFormat.getInstance().format(user.publicRepos)));

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

    private void showLoading(boolean b) {
        if (b) {
            loading.setVisibility(View.VISIBLE);
            //binding.contentWrapper.setVisibility(View.GONE);
        } else {
            loading.setVisibility(View.GONE);
            //binding.contentWrapper.setVisibility(View.VISIBLE);
        }
    }
}
