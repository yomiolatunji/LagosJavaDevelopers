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

import com.yomiolatunji.andela.lagosjavadev.data.api.GithubApiNetworkService;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;

import java.util.List;

public abstract class LagosJavaDevsDataManager extends DataManager<List<User>> {


    public LagosJavaDevsDataManager(Context context) {
        super(context);
    }

    @Override
    protected void loadData(int page) {
        loadUsers(page);

    }

    @Override
    public void cancelLoading() {

        //if (loadUsersCall != null) loadUsersCall.cancel();
    }

    private void loadUsers(final int page) {
        getGithubApi()
                .getLagosJavaDevs(page, GithubApiNetworkService.PER_PAGE_DEFAULT, new DataLoadingCallback<List<User>>() {
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
