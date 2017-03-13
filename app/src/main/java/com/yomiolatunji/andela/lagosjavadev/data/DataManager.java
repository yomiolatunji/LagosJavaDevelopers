/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yomiolatunji.andela.lagosjavadev.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yomiolatunji.andela.lagosjavadev.data.source.LagosJavaDevRepository;
import com.yomiolatunji.andela.lagosjavadev.data.source.UsersDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class DataManager<T> implements DataLoadingSubject {

    private final AtomicInteger loadingCount;
    protected boolean moreDataAvailable = true;
    private LagosJavaDevRepository repository;
    private int page = 0;
    private List<DataLoadingCallbacks> loadingCallbacks;
    private Context context;

    public DataManager(@NonNull Context context) {
        this.context = context;
        loadingCount = new AtomicInteger(0);
    }

    protected static void setPage(List<? extends GithubItem> items, int page) {
        for (GithubItem item : items) {
            item.page = page;
        }
    }

    public void loadData() {
        if (!moreDataAvailable) return;
        page++;
        loadStarted();
        loadData(page);
    }

    protected abstract void loadData(int page);

    public abstract void onDataLoaded(T data);

    public abstract void cancelLoading();

    @Override
    public boolean isDataLoading() {
        return loadingCount.get() > 0;
    }

    public LagosJavaDevRepository getRepository(UsersDataSource userRemoteDataSource,UsersDataSource userLocalDataSource,boolean isNetworkAvailable) {
        if (repository == null) createRepository(userRemoteDataSource,userLocalDataSource,isNetworkAvailable);
        return repository;
    }

    @Override
    public void registerCallback(DataLoadingSubject.DataLoadingCallbacks callback) {
        if (loadingCallbacks == null) {
            loadingCallbacks = new ArrayList<>(1);
        }
        loadingCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(DataLoadingSubject.DataLoadingCallbacks callback) {
        if (loadingCallbacks != null && loadingCallbacks.contains(callback)) {
            loadingCallbacks.remove(callback);
        }
    }

    protected void loadStarted() {
        if (0 == loadingCount.getAndIncrement()) {
            dispatchLoadingStartedCallbacks();
        }
    }

    protected void loadFinished() {
        if (0 == loadingCount.decrementAndGet()) {
            dispatchLoadingFinishedCallbacks();
        }
    }

    protected void resetLoadingCount() {
        loadingCount.set(0);
    }


    protected void dispatchLoadingStartedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (DataLoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.dataStartedLoading();
        }
    }

    protected void dispatchLoadingFinishedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (DataLoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.dataFinishedLoading();
        }
    }

    private void createRepository(UsersDataSource userRemoteDataSource,UsersDataSource userLocalDataSource,boolean isNetworkAvailable) {
        repository = LagosJavaDevRepository.getInstance(userRemoteDataSource,userLocalDataSource,isNetworkAvailable);
    }

}
