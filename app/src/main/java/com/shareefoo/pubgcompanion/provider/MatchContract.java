package com.shareefoo.pubgcompanion.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class MatchContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.shareefoo.pubgcompanion";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "matches" directory
    public static final String PATH_MATCHES = "matches";

    public static final class MatchEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MATCHES).build();

        public static final String TABLE_NAME = "matches";
        public static final String COLUMN_PLACEMENT = "placement";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_KILLS = "kills";
        public static final String COLUMN_DAMAGE = "damage";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_MODE = "mode";
    }

}
