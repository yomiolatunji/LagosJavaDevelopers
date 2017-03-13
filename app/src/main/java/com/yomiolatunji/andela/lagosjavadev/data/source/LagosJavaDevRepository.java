/*
 * Copyright 2016, The Android Open Source Project
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

package com.yomiolatunji.andela.lagosjavadev.data.source;

import android.support.annotation.NonNull;

import com.yomiolatunji.andela.lagosjavadev.data.DataLoadingCallback;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;
import com.yomiolatunji.andela.lagosjavadev.data.source.local.LocalService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class LagosJavaDevRepository implements UsersDataSource {

    private static LagosJavaDevRepository INSTANCE = null;

    private final UsersDataSource mUsersRemoteDataSource;

    private final UsersDataSource mUsersLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, User> mCachedUsers;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private LagosJavaDevRepository(@NonNull UsersDataSource usersRemoteDataSource,
                                   @NonNull UsersDataSource usersLocalDataSource, boolean isNetworkAvailable) {
        mUsersRemoteDataSource = usersRemoteDataSource;
        mUsersLocalDataSource = usersLocalDataSource;
        if (isNetworkAvailable) mCacheIsDirty = true;
    }

    public static LagosJavaDevRepository getInstance(UsersDataSource usersRemoteDataSource,
                                                     UsersDataSource usersLocalDataSource, boolean isNetworkAvailable) {
        if (INSTANCE == null) {
            INSTANCE = new LagosJavaDevRepository(usersRemoteDataSource, usersLocalDataSource, isNetworkAvailable);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private void getLagosDevsFromRemoteDataSource(int page, int pageCount, @NonNull final DataLoadingCallback<List<User>> callback) {
        mUsersRemoteDataSource.getLagosJavaDevs(page, pageCount, new DataLoadingCallback<List<User>>() {
            @Override
            public void onResponse(List<User> data) {
                refreshCache(data);
                refreshLocalDataSource(data);
                callback.onResponse(data);
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }

        });
    }

    private void getUserFromRemoteDataSource(String username, final DataLoadingCallback<User> callback) {
        mUsersRemoteDataSource.getUser(username, new DataLoadingCallback<User>() {
            @Override
            public void onResponse(User data) {
                updateLocalDataSource(data);
                callback.onResponse(data);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void refreshCache(List<User> users) {
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.clear();
        for (User user : users) {
            mCachedUsers.put(user.login, user);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<User> users) {
        ((LocalService) mUsersLocalDataSource).copyOrUpdateUsers(users);
    }

    private void updateLocalDataSource(User user) {
        ((LocalService) mUsersLocalDataSource).copyOrUpdateUser(user);
    }

    @Override
    public void getLagosJavaDevs(final int page, final int pageCount, final DataLoadingCallback<List<User>> callback) {
//        if (mCachedUsers != null && !mCacheIsDirty) {
//            callback.onResponse(new ArrayList<>(mCachedUsers.values()));
//            return;
//        }

        if (mCacheIsDirty ||page>1) {
            getLagosDevsFromRemoteDataSource(page, pageCount, callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mUsersLocalDataSource.getLagosJavaDevs(page, pageCount, new DataLoadingCallback<List<User>>() {
                @Override
                public void onResponse(List<User> data) {
                    if (data != null && data.size() > 0) {
                        refreshCache(data);
                        callback.onResponse(new ArrayList<>(mCachedUsers.values()));
                    } else
                        getLagosDevsFromRemoteDataSource(page, pageCount, callback);
                }

                @Override
                public void onFailure(String message) {

                }
            });
        }
    }

    @Override
    public void getUser(final String username, final DataLoadingCallback<User> callback) {

        //if (mCacheIsDirty) {
        getUserFromRemoteDataSource(username, callback);
//        } else {
//            // Query the local storage if available. If not, query the network.
//            mUsersLocalDataSource.getUser(username, new DataLoadingCallback<User>() {
//                @Override
//                public void onResponse(User data) {
//                    if (data != null) {
//                        //refreshCache(data);
//                        callback.onResponse(data);
//                    } else
//                        getUserFromRemoteDataSource(username, callback);
//                }
//
//                @Override
//                public void onFailure(String message) {
//
//                }
//            });
//        }
    }

    @Override
    public void getUserFollowing(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback) {
        mUsersRemoteDataSource.getUserFollowing(username, page, pageCount, callback);
    }

    @Override
    public void getUserFollowers(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback) {
        mUsersLocalDataSource.getUserFollowers(username, page, pageCount, callback);
    }
}
