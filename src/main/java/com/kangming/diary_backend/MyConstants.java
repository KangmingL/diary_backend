package com.kangming.diary_backend;

public final class MyConstants {
    public static final long CELEBRITY_THRESHOLD = 10000;

    // Redis feed caching limit
    public static final int FEED_CACHING_LIMIT = 30;

    // Redis keys prefix
    public static final String HOME_FEED = "homefeed:";
    public static final String USER_FEED = "userfeed:";
    public static final String POST = "post:";
    public static final String USER = "user:";
}
