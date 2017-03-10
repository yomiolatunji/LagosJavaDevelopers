package com.yomiolatunji.andela.lagosjavadev.data.api;

import android.content.Context;
import android.os.AsyncTask;

import com.yomiolatunji.andela.lagosjavadev.NetworkUtils;
import com.yomiolatunji.andela.lagosjavadev.data.DataLoadingCallback;
import com.yomiolatunji.andela.lagosjavadev.data.OkHttpSingleton;
import com.yomiolatunji.andela.lagosjavadev.data.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class GithubApiNetworkService {
    public static int PER_PAGE_DEFAULT = 30;
    public static int PER_PAGE_MAX = 100;
    private static Context mContext;
    public int resultCount = -1;
    String baseUrl = "https://api.github.com";

    private GithubApiNetworkService() {
    }

    public static GithubApiNetworkService newInstance(Context context) {
        mContext = context;
        //mOkHttpClient = okHttpClient;
        return new GithubApiNetworkService();
    }

    public void getLagosJavaDevs(int page, int pageCount, DataLoadingCallback<List<User>> callback) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/search/users?q=language:java+location:lagos");
        if (page >= 0) {
            urlBuilder.append(MessageFormat.format("&page={0}", page));
        }
        if (pageCount >= 0) {
            urlBuilder.append(MessageFormat.format("&per_page={0}", pageCount));
        }

        String url = urlBuilder.toString();
        GetUserList getUserList = new GetUserList(url, callback);
        getUserList.execute();
    }

    public void getUserFollowing(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append(MessageFormat.format("/users/{0}/following", username));
        if (page >= 0) {
            urlBuilder.append(MessageFormat.format("&page={0}", page));
        }
        if (pageCount >= 0) {
            urlBuilder.append(MessageFormat.format("&per_page={0}", pageCount));
        }

        String url = urlBuilder.toString();
        GetUserList getUserList = new GetUserList(url, callback);
        getUserList.execute();
    }

    public void getUserFollowers(String username, int page, int pageCount, DataLoadingCallback<List<User>> callback) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append(MessageFormat.format("/users/{0}/followers", username));
        if (page >= 0) {
            urlBuilder.append(MessageFormat.format("&page={0}", page));
        }
        if (pageCount >= 0) {
            urlBuilder.append(MessageFormat.format("&per_page={0}", pageCount));
        }

        String url = urlBuilder.toString();
        GetUserList getUserList = new GetUserList(url, callback);
        getUserList.execute();
    }

    public void getUser(String username, DataLoadingCallback<User> callback) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/users/{0}");

        String url = MessageFormat.format(urlBuilder.toString(), username);
        GetUser getUser = new GetUser(url, callback);
        getUser.execute();
    }

    class GetUserList extends AsyncTask<Void, Void, Void> {
        List<User> users = new ArrayList<>();
        private String url;
        private DataLoadingCallback<List<User>> callback;

        public GetUserList(String url, DataLoadingCallback<List<User>> callback) {
            this.url = url;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String response = NetworkUtils.newInstance(mContext, OkHttpSingleton.getOkHttpClient()).get(url);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt("total_count") == 0)
                    callback.onFailure("Empty list");
                else {
                    resultCount = jsonObject.getInt("total_count");
                    JSONArray userJsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < userJsonArray.length(); i++) {
                        JSONObject userObject = userJsonArray.getJSONObject(i);
                        User.Builder builder = new User.Builder();
                        if (userObject.has("login"))
                            builder.setLogin(userObject.getString("login"));
                        if (userObject.has("id"))
                            builder.setId(userObject.getInt("id"));
                        if (userObject.has("avatar_url"))
                            builder.setAvatarUrl(userObject.getString("avatar_url"));
                        if (userObject.has("url"))
                            builder.setUrl(userObject.getString("url"));
                        if (userObject.has("html_url"))
                            builder.setHtml_url(userObject.getString("html_url"));
                        if (userObject.has("followers_url"))
                            builder.setFollowers_url(userObject.getString("followers_url"));
                        if (userObject.has("following_url"))
                            builder.setFollowing_url(userObject.getString("following_url"));
                        if (userObject.has("gists_url"))
                            builder.setGistsUrl(userObject.getString("gists_url"));
                        if (userObject.has("starred_url"))
                            builder.setStarred_url(userObject.getString("starred_url"));
                        if (userObject.has("subscriptions_url"))
                            builder.setSubscriptions_url(userObject.getString("subscriptions_url"));
                        if (userObject.has("organizations_url"))
                            builder.setOrganizations_url(userObject.getString("organizations_url"));
                        if (userObject.has("repos_url"))
                            builder.setReposUrl(userObject.getString("repos_url"));
                        if (userObject.has("events_url"))
                            builder.setEventsUrl(userObject.getString("events_url"));
                        if (userObject.has("received_events_url"))
                            builder.setReceivedEventsUrl(userObject.getString("received_events_url"));
                        if (userObject.has("type"))
                            builder.setType(userObject.getString("type"));

                        users.add(builder.build());

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            callback.onResponse(users);
        }
    }

    class GetUser extends AsyncTask<Void, Void, Void> {
        private String url;
        private DataLoadingCallback<User> callback;

        public GetUser(String url, DataLoadingCallback<User> callback) {
            this.url = url;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            User.Builder builder = new User.Builder();
            try {
                String response = NetworkUtils.newInstance(mContext, OkHttpSingleton.getOkHttpClient()).get(url);
                JSONObject userObject = new JSONObject(response);
                if (userObject.has("login"))
                    builder.setLogin(userObject.getString("login"));
                if (userObject.has("id"))
                    builder.setId(userObject.getInt("id"));
                if (userObject.has("avatar_url"))
                    builder.setAvatarUrl(userObject.getString("avatar_url"));
                if (userObject.has("url"))
                    builder.setUrl(userObject.getString("url"));
                if (userObject.has("html_url"))
                    builder.setHtml_url(userObject.getString("html_url"));
                if (userObject.has("followers_url"))
                    builder.setFollowers_url(userObject.getString("followers_url"));
                if (userObject.has("following_url"))
                    builder.setFollowing_url(userObject.getString("following_url"));
                if (userObject.has("gists_url"))
                    builder.setGistsUrl(userObject.getString("gists_url"));
                if (userObject.has("starred_url"))
                    builder.setStarred_url(userObject.getString("starred_url"));
                if (userObject.has("subscriptions_url"))
                    builder.setSubscriptions_url(userObject.getString("subscriptions_url"));
                if (userObject.has("organizations_url"))
                    builder.setOrganizations_url(userObject.getString("organizations_url"));
                if (userObject.has("repos_url"))
                    builder.setReposUrl(userObject.getString("repos_url"));
                if (userObject.has("events_url"))
                    builder.setEventsUrl(userObject.getString("events_url"));
                if (userObject.has("received_events_url"))
                    builder.setReceivedEventsUrl(userObject.getString("received_events_url"));
                if (userObject.has("type"))
                    builder.setType(userObject.getString("type"));
                if (userObject.has("name"))
                    builder.setName(userObject.getString("name"));
                if (userObject.has("company"))
                    builder.setCompany(userObject.getString("company"));
                if (userObject.has("blog"))
                    builder.setBlog(userObject.getString("blog"));
                if (userObject.has("location"))
                    builder.setLocation(userObject.getString("location"));
                if (userObject.has("email"))
                    builder.setEmail(userObject.getString("email"));
                if (userObject.has("hireable"))
                    builder.setHireable(userObject.getString("hireable"));
                if (userObject.has("bio"))
                    builder.setBio(userObject.getString("bio"));
                if (userObject.has("public_repos"))
                    builder.setPublicRepos(userObject.getInt("public_repos"));
                if (userObject.has("public_gists"))
                    builder.setPublicGists(userObject.getInt("public_gists"));
                if (userObject.has("followers"))
                    builder.setFollowers(userObject.getInt("followers"));
                if (userObject.has("following"))
                    builder.setFollowing(userObject.getInt("following"));


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callback.onResponse(builder.build());

            return null;
        }

    }

}
