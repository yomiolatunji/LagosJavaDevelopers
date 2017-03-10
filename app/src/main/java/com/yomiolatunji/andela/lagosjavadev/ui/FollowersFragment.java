package com.yomiolatunji.andela.lagosjavadev.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yomiolatunji.andela.lagosjavadev.R;
import com.yomiolatunji.andela.lagosjavadev.data.FollowersDataManager;
import com.yomiolatunji.andela.lagosjavadev.data.adapter.FollowersAdapter;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;

import java.util.List;


public class FollowersFragment extends Fragment {
    RecyclerView mRecycler;
    int shortAnimTime = 0;
    String username = "";
    private LinearLayoutManager mManager;
    private View mProgressView;
    private TextView noResults;
    private FollowersDataManager dataManager;
    private FollowersAdapter adapter;

    public FollowersFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(UserDetailFragment.ARG_USERNAME)) {
                username = getArguments().getString(UserDetailFragment.ARG_USERNAME);
            }
        } else
            username = getActivity().getIntent().getStringExtra(UserDetailFragment.ARG_USERNAME);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        mRecycler.setNestedScrollingEnabled(false);
        dataManager = new FollowersDataManager(getActivity(), username) {
            @Override
            public void onDataLoaded(List<User> data) {
                if (data != null && data.size() > 0) {
                    if (adapter.getDataItemCount() == 0) {
                        showProgress(false);
                    }
                    adapter.addAndResort(data);
                } else {
                    showProgress(false);
                    setNoResultsVisibility(View.VISIBLE);
                }
            }
        };
        adapter = new FollowersAdapter(getActivity(), dataManager);


        mRecycler.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        mRecycler = (RecyclerView) view.findViewById(R.id.list);
        mProgressView = view.findViewById(R.id.progress);
        mRecycler.setHasFixedSize(true);

        noResults = (TextView)
                view.findViewById(R.id.empty_results);
        return view;
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            mRecycler.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecycler.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecycler.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRecycler.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        setNoResultsVisibility(View.GONE);
    }

    private void setNoResultsVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noResults == null) {
            }
            if (isAdded()) {
                String message = getString(R.string.no_follower);
                SpannableStringBuilder ssb = new SpannableStringBuilder(message);
                ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                        message.indexOf('“') + 1,
                        message.length() - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                noResults.setText(ssb);
            }
        }
        if (noResults != null) {
            noResults.setVisibility(visibility);
        }
    }

}
