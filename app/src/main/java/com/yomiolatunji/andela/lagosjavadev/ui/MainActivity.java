package com.yomiolatunji.andela.lagosjavadev.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yomiolatunji.andela.lagosjavadev.InfiniteScrollListener;
import com.yomiolatunji.andela.lagosjavadev.data.adapter.LagosJavaDevsAdapter;
import com.yomiolatunji.andela.lagosjavadev.data.LagosJavaDevsDataManager;
import com.yomiolatunji.andela.lagosjavadev.R;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private LagosJavaDevsDataManager dataManager;
    private LinearLayoutManager layoutManager;
    private LagosJavaDevsAdapter adapter;
    private RecyclerView usersRecyclerView;
    private ProgressBar loading;
    private boolean connected;
    private ImageView noConnection;
    private boolean monitoringConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        usersRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        loading = (ProgressBar) findViewById(R.id.loading);

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        dataManager = new LagosJavaDevsDataManager(this) {
            @Override
            public void onDataLoaded(List<User> data) {
                if (data != null && data.size() > 0) {
                    if (adapter.getDataItemCount() == 0) {
                        loading.setVisibility(View.GONE);
                    }
                    adapter.addAndResort(data);
                }
            }
        };
        adapter = new LagosJavaDevsAdapter(this, dataManager,mTwoPane);
        usersRecyclerView.setAdapter(adapter);
        usersRecyclerView.setVisibility(View.VISIBLE);
        layoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(layoutManager);
        usersRecyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager, dataManager) {
            @Override
            public void onLoadMore() {
                dataManager.loadData();
            }
        });
        usersRecyclerView.setHasFixedSize(true);
        dataManager.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected) {
            loading.setVisibility(View.GONE);
            if (noConnection == null) {
                noConnection = (ImageView) findViewById(R.id.no_connection);
                noConnection.setVisibility(View.VISIBLE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.registerNetworkCallback(
                        new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                        new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(Network network) {
                                connected = true;
                                if (adapter.getDataItemCount() != 0) return;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        noConnection.setVisibility(View.GONE);
                                        loading.setVisibility(View.VISIBLE);
                                        dataManager.loadData();
                                    }
                                });
                            }

                            @Override
                            public void onLost(Network network) {
                                connected = false;
                            }
                        });
            }
            monitoringConnectivity = true;
        }
    }




}
