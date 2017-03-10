package com.yomiolatunji.andela.lagosjavadev.data.model;

import com.yomiolatunji.andela.lagosjavadev.data.GithubItem;

public class User extends GithubItem {

    public int id;
    public String login;
    public String avatarUrl;
    public String url;
    public String html_url;
    public String followers_url;
    public String following_url;
    public String gistsUrl;
    public String starred_url;
    public String subscriptions_url;
    public String organizations_url;
    public String reposUrl;
    public String eventsUrl;
    public String receivedEventsUrl;
    public String type;
    public String siteAdmin;
    public String name;
    public String company;
    public String blog;
    public String location;
    public String email;
    public String hireable;
    public String bio;
    public int publicRepos;
    public int publicGists;
    public int followers;
    public int following;
    public String createdAt;

    public User(String url, int id, String login, String avatarUrl, String html_url, String followers_url, String following_url, String gistsUrl, String starred_url, String subscriptions_url, String organizations_url, String reposUrl, String eventsUrl, String receivedEventsUrl, String type, String siteAdmin, String name, String company, String blog, String location, String email, String hireable, String bio, int publicRepos, int publicGists, int followers, int following, String createdAt) {
        super(id, login, url);
        this.id = id;
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.url = url;
        this.html_url = html_url;
        this.followers_url = followers_url;
        this.following_url = following_url;
        this.gistsUrl = gistsUrl;
        this.starred_url = starred_url;
        this.subscriptions_url = subscriptions_url;
        this.organizations_url = organizations_url;
        this.reposUrl = reposUrl;
        this.eventsUrl = eventsUrl;
        this.receivedEventsUrl = receivedEventsUrl;
        this.type = type;
        this.siteAdmin = siteAdmin;
        this.name = name;
        this.company = company;
        this.blog = blog;
        this.location = location;
        this.email = email;
        this.hireable = hireable;
        this.bio = bio;
        this.publicRepos = publicRepos;
        this.publicGists = publicGists;
        this.followers = followers;
        this.following = following;
        this.createdAt = createdAt;
    }

    public static class Builder {
        private int id;
        private String login;
        private String avatarUrl;
        private String url;
        private String html_url;
        private String followers_url;
        private String following_url;
        private String gistsUrl;
        private String starred_url;
        private String subscriptions_url;
        private String organizations_url;
        private String reposUrl;
        private String eventsUrl;
        private String receivedEventsUrl;
        private String type;
        private String siteAdmin;
        private String name;
        private String company;
        private String blog;
        private String location;
        private String email;
        private String hireable;
        private String bio;
        private int publicRepos;
        private int publicGists;
        private int followers;
        private int following;
        private String createdAt;

        public Builder setGistsUrl(String gistsUrl) {
            this.gistsUrl = gistsUrl;
            return this;
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setHtml_url(String html_url) {
            this.html_url = html_url;
            return this;
        }

        public Builder setFollowers_url(String followers_url) {
            this.followers_url = followers_url;
            return this;
        }

        public Builder setFollowing_url(String following_url) {
            this.following_url = following_url;
            return this;
        }

        public Builder setStarred_url(String starred_url) {
            this.starred_url = starred_url;
            return this;
        }

        public Builder setSubscriptions_url(String subscriptions_url) {
            this.subscriptions_url = subscriptions_url;
            return this;
        }

        public Builder setOrganizations_url(String organizations_url) {
            this.organizations_url = organizations_url;
            return this;
        }

        public Builder setReposUrl(String reposUrl) {
            this.reposUrl = reposUrl;
            return this;
        }

        public Builder setEventsUrl(String eventsUrl) {
            this.eventsUrl = eventsUrl;
            return this;
        }

        public Builder setReceivedEventsUrl(String receivedEventsUrl) {
            this.receivedEventsUrl = receivedEventsUrl;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setSiteAdmin(String siteAdmin) {
            this.siteAdmin = siteAdmin;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCompany(String company) {
            this.company = company;
            return this;
        }

        public Builder setBlog(String blog) {
            this.blog = blog;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setHireable(String hireable) {
            this.hireable = hireable;
            return this;
        }

        public Builder setBio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder setPublicRepos(int publicRepos) {
            this.publicRepos = publicRepos;
            return this;
        }

        public Builder setPublicGists(int publicGists) {
            this.publicGists = publicGists;
            return this;
        }

        public Builder setFollowers(int followers) {
            this.followers = followers;
            return this;
        }

        public Builder setFollowing(int following) {
            this.following = following;
            return this;
        }

        public Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public User build() {
            return new User(url, id, login, avatarUrl, html_url, followers_url, following_url, gistsUrl, starred_url, subscriptions_url, organizations_url, reposUrl, eventsUrl, receivedEventsUrl, type, siteAdmin, name, company, blog, location, email, hireable, bio, publicRepos, publicGists, followers, following, createdAt);
        }
    }
}
