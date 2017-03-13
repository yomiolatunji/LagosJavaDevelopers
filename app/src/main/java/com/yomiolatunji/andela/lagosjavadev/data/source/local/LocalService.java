package com.yomiolatunji.andela.lagosjavadev.data.source.local;

import android.content.Context;

import com.yomiolatunji.andela.lagosjavadev.data.DataLoadingCallback;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;
import com.yomiolatunji.andela.lagosjavadev.data.source.UsersDataSource;
import com.yomiolatunji.andela.lagosjavadev.data.source.local.entity.RealmUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class LocalService implements UsersDataSource {
    private static Context mContext;
    public int resultCount = -1;
    Realm realm;

    private LocalService() {
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

    }

    public static LocalService newInstance(Context context) {
        mContext = context;
        //mOkHttpClient = okHttpClient;
        return new LocalService();
    }

    public void getLagosJavaDevs(int page, int pageCount, DataLoadingCallback<List<User>> callback) {
        RealmResults<RealmUser> realmUsers = realm.where(RealmUser.class).findAllAsync();
        List<User> users = new ArrayList<>();
        for (RealmUser user :
                realmUsers) {
            users.add(user.toUser());
        }
        callback.onResponse(users);
    }

    public void getUserFollowing(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback) {
        callback.onResponse(null);
    }

    public void getUserFollowers(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback) {
        callback.onResponse(null);
    }

    public void getUser(String username, DataLoadingCallback<User> callback) {
        callback.onResponse(realm.where(RealmUser.class).equalTo("login", username).findFirstAsync().toUser());

    }

    public void copyOrUpdateUsers(final List<User> users) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (User user : users) {
                    realm.copyToRealmOrUpdate(new RealmUser(user));
                }
            }
        });
    }

    public void copyOrUpdateUser(final User user) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(new RealmUser(user));
            }
        });
    }
}
