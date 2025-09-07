package com.dr.kisan.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.dr.kisan.R;
import com.dr.kisan.adapters.CommunityAdapter;
import com.dr.kisan.models.CommunityPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommunityAdapter adapter;
    private List<CommunityPost> posts;
    private FloatingActionButton fabNewPost;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tabLayout;
    private android.widget.LinearLayout layoutEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        setupToolbar();
        initializeViews();
        setupTabLayout();
        setupRecyclerView();
        loadCommunityPosts();
        setupRefreshLayout();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Farmer Community");
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewCommunity);
        fabNewPost = findViewById(R.id.fabNewPost);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tabLayout = findViewById(R.id.tabLayout);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        fabNewPost.setOnClickListener(v -> openCreatePostDialog());
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Questions"));
        tabLayout.addTab(tabLayout.newTab().setText("Tips"));
        tabLayout.addTab(tabLayout.newTab().setText("Diseases"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterPostsByCategory(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        posts = new ArrayList<>();
        adapter = new CommunityAdapter(this, posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnPostClickListener(new CommunityAdapter.OnPostClickListener() {
            @Override
            public void onPostClick(CommunityPost post) {
                // Handle post click
                android.widget.Toast.makeText(CommunityActivity.this, "Post clicked: " + post.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLikeClick(CommunityPost post, int position) {
                // Handle like click
                post.setLikes(post.getLikes() + 1);
                adapter.updatePost(position, post);
                android.widget.Toast.makeText(CommunityActivity.this, "Post liked!", android.widget.Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCommentClick(CommunityPost post) {
                // Handle comment click
                android.widget.Toast.makeText(CommunityActivity.this, "Comments for: " + post.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_green);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadCommunityPosts();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void loadCommunityPosts() {
        posts.clear();

        // Add sample community posts
        posts.add(new CommunityPost(1, "Rajesh Kumar", "Tomato blight issue",
                "My tomato plants are showing brown spots on leaves. Started 3 days ago after heavy rain. Any suggestions for treatment?",
                System.currentTimeMillis() - 3600000, 15, 8, "Diseases"));

        posts.add(new CommunityPost(2, "Priya Singh", "Organic pesticide recipe",
                "Sharing my grandmother's organic pesticide recipe that works great against aphids. Mix neem oil with soap water and spray in evening.",
                System.currentTimeMillis() - 7200000, 32, 12, "Tips"));

        posts.add(new CommunityPost(3, "Amit Patel", "Best time for wheat sowing?",
                "Planning to sow wheat next week. Is it the right time considering the weather conditions? Located in Punjab region.",
                System.currentTimeMillis() - 10800000, 8, 15, "Questions"));

        posts.add(new CommunityPost(4, "Sunita Devi", "Successful potato harvest",
                "Just completed potato harvesting with 20% increased yield compared to last year. Used organic fertilizers and proper irrigation scheduling.",
                System.currentTimeMillis() - 14400000, 45, 6, "Tips"));

        posts.add(new CommunityPost(5, "Manish Sharma", "Corn leaf rust treatment",
                "Found effective treatment for corn leaf rust. Applied copper-based fungicide twice with 10 days interval. Sharing before and after photos.",
                System.currentTimeMillis() - 18000000, 28, 9, "Diseases"));

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void filterPostsByCategory(int tabPosition) {
        // This would normally filter the posts based on category
        // For now, just showing all posts
        updateEmptyState();
        android.widget.Toast.makeText(this, "Filtered by category: " + tabPosition, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void updateEmptyState() {
        if (posts.isEmpty()) {
            layoutEmptyState.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            layoutEmptyState.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void openCreatePostDialog() {
        // Implement create post dialog
        android.widget.Toast.makeText(this, "Create post feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
