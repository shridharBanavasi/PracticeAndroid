package com.example.databaseexample.databas;

import android.provider.BaseColumns;

public class ToolsHistory {
    public ToolsHistory() {
    }

    public static final class ToolsItemEntery implements BaseColumns {
        public static final String TABLE_NAME = "toolsHistory";
        public static final String COLUMN_INFO1 = "time";
        public static final String COLUMN_INFO2 = "tool";
        public static final String COLUMN_INFO3 = "status";
    }
}
