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

import com.yomiolatunji.andela.lagosjavadev.NetworkUtils;
import com.yomiolatunji.andela.lagosjavadev.data.source.api.GithubApiNetworkService;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;
import com.yomiolatunji.andela.lagosjavadev.data.source.local.LocalService;

import java.util.List;

public abstract class FollowingDataManager extends DataManager<List<User>> {


    private Context context;
    private String username;

    public FollowingDataManager(Context context, String username) {
        super(context);
        this.context = context;
        this.username = username;
    }

    @Override
    protected void loadData(int page) {
        loadFollowing(page,username);

    }

    @Override
    public void cancelLoading() {

        //if (loadUsersCall != null) loadUsersCall.cancel();
    }

    private void loadFollowing(final int page,String username) {
        getRepository(GithubApiNetworkService.newInstance(context), LocalService.newInstance(context), NetworkUtils.isNetworkAvailable(context))
                .getUserFollowing(username,page, GithubApiNetworkService.PER_PAGE_DEFAULT, new DataLoadingCallback<List<User>>() {
                    @Override
                    public void onResponse(List<User> users) {
                        setPage(users, page);
                        onDataLoaded(users);
                        loadFinished();
                        moreDataAvailable = users.size() == GithubApiNetworkService.PER_PAGE_DEFAULT;
                    }

                    @Override
                    public void onFailure(String message) {
                        failure();
                    }
                });
    }

    private void failure() {
        loadFinished();
        moreDataAvailable = false;
    }

}
