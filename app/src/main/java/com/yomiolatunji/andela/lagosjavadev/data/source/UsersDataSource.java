package com.yomiolatunji.andela.lagosjavadev.data.source;

import com.yomiolatunji.andela.lagosjavadev.data.DataLoadingCallback;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;

import java.util.List;

/**
 * Created by Oluwayomi on 3/12/2017.
 */

public interface UsersDataSource {
    void getLagosJavaDevs(int page, int pageCount, DataLoadingCallback<List<User>> callback);
    void getUser(String username, DataLoadingCallback<User> callback);
    void getUserFollowing(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback);
    void getUserFollowers(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback);
    }
