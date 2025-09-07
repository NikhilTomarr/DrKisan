package com.dr.kisan.models;

public class RecentActivity {
    private String title;
    private String description;
    private long timestamp;
    private int iconResource;

    public RecentActivity(String title, String description, long timestamp, int iconResource) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.iconResource = iconResource;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public int getIconResource() { return iconResource; }

    public String getTimeAgo() {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 3600000) {
            return (diff / 60000) + " minutes ago";
        } else if (diff < 86400000) {
            return (diff / 3600000) + " hours ago";
        } else {
            return (diff / 86400000) + " days ago";
        }
    }
}
