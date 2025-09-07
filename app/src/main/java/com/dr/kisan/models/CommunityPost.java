package com.dr.kisan.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommunityPost {
    private int id;
    private String authorName;
    private String title;
    private String content;
    private long timestamp;
    private int likes;
    private int comments;
    private String authorImage;
    private String postImage;
    private String category;

    public CommunityPost() {
        // Default constructor
    }

    public CommunityPost(String authorName, String title, String content, long timestamp, int likes) {
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = 0;
        this.category = "General";
    }

    public CommunityPost(int id, String authorName, String title, String content,
                         long timestamp, int likes, int comments, String category) {
        this.id = id;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = comments;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    public String getAuthorImage() { return authorImage; }
    public void setAuthorImage(String authorImage) { this.authorImage = authorImage; }

    public String getPostImage() { return postImage; }
    public void setPostImage(String postImage) { this.postImage = postImage; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getTimeAgo() {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60000) {
            return "Just now";
        } else if (diff < 3600000) {
            return (diff / 60000) + " minutes ago";
        } else if (diff < 86400000) {
            return (diff / 3600000) + " hours ago";
        } else {
            return (diff / 86400000) + " days ago";
        }
    }
}
