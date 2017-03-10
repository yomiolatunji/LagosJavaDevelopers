package com.yomiolatunji.andela.lagosjavadev.data.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yomiolatunji.andela.lagosjavadev.R;
import com.yomiolatunji.andela.lagosjavadev.data.DataLoadingSubject;
import com.yomiolatunji.andela.lagosjavadev.data.GithubItem;
import com.yomiolatunji.andela.lagosjavadev.data.PicassoSingleton;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;
import com.yomiolatunji.andela.lagosjavadev.ui.UserDetailActivity;
import com.yomiolatunji.andela.lagosjavadev.ui.UserDetailFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements DataLoadingSubject.DataLoadingCallbacks {

    private static final int TYPE_FOLLOWERS = 0;
    private static final int TYPE_LOADING_MORE = -1;

    private final Activity activity;
    private final LayoutInflater layoutInflater;
    private final
    @Nullable
    DataLoadingSubject dataLoading;
    private List<GithubItem> items;
    private boolean showLoadingMore = false;

    public FollowersAdapter(Activity hostActivity,
                            DataLoadingSubject dataLoading) {
        this.activity = hostActivity;
        this.dataLoading = dataLoading;
        dataLoading.registerCallback(this);
        layoutInflater = LayoutInflater.from(activity);
        items = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOLLOWERS:
                return new FollowersHolder(
                        layoutInflater.inflate(R.layout.user_item, parent, false));
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(
                        layoutInflater.inflate(R.layout.infinite_loading, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_FOLLOWERS:
                bindUserHolder((User) getItem(position), (FollowersHolder) holder);
                break;
            case TYPE_LOADING_MORE:
                bindLoadingViewHolder((LoadingMoreHolder) holder);
                break;
        }
    }

    private void bindUserHolder(final User user,
                                final FollowersHolder holder) {
        holder.user = user;
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_person_outline, null);

        if (user.avatarUrl != null)
            PicassoSingleton.getPicasso()
                    .load(user.avatarUrl)
                    .placeholder(vectorDrawableCompat)
                    .error(vectorDrawableCompat)
                    .fit()
                    .into(holder.image);
        else
            holder.image.setImageResource(R.drawable.ic_person_outline);
        holder.username.setText(user.login);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra(UserDetailFragment.ARG_USERNAME, holder.user.id);
                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                            holder.image, context.getString(R.string.transition_user_avatar));

                    context.startActivity(intent, options.toBundle());
                }

                context.startActivity(intent);

            }
        });
    }


    private void bindLoadingViewHolder(LoadingMoreHolder holder) {
        // only show the infinite load progress spinner if there are already items in the
        // grid i.e. it's not the first item & data is being loaded
        holder.progress.setVisibility((holder.getAdapterPosition() > 0
                && dataLoading.isDataLoading()) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            GithubItem item = getItem(position);
            return TYPE_FOLLOWERS;

        }
        return TYPE_LOADING_MORE;
    }

    private GithubItem getItem(int position) {
        return items.get(position);
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAndResort(List<? extends GithubItem> newItems) {
        deduplicateAndAdd(newItems);
        notifyDataSetChanged();
    }


    /**
     * De-dupe as the same item can be returned by multiple feeds
     */
    private void deduplicateAndAdd(List<? extends GithubItem> newItems) {
        final int count = getDataItemCount();
        for (GithubItem newItem : newItems) {
            boolean add = true;
            for (int i = 0; i < count; i++) {
                GithubItem existingItem = getItem(i);
                if (existingItem.equals(newItem)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                add(newItem);
            }
        }
    }

    private void add(GithubItem item) {
        items.add(item);
    }


    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == TYPE_LOADING_MORE) {
            return -1L;
        }
        return getItem(position).id;
    }

    @Override
    public int getItemCount() {
        return getDataItemCount() + (showLoadingMore ? 1 : 0);
    }


    public int getDataItemCount() {
        return items.size();
    }

    private int getLoadingMoreItemPosition() {
        return showLoadingMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }


    @Override
    public void dataStartedLoading() {
        if (showLoadingMore) return;
        showLoadingMore = true;
        notifyItemInserted(getLoadingMoreItemPosition());
    }

    @Override
    public void dataFinishedLoading() {
        if (!showLoadingMore) return;
        final int loadingPos = getLoadingMoreItemPosition();
        showLoadingMore = false;
        notifyItemRemoved(loadingPos);
    }

    /* package */ static class FollowersHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView username;
        View mView;
        User user;

        public FollowersHolder(View itemView) {
            super(itemView);
            mView = itemView;
            image = (CircleImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
        }

    }


    /* package */ static class LoadingMoreHolder extends RecyclerView.ViewHolder {

        ProgressBar progress;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView;
        }

    }

}
