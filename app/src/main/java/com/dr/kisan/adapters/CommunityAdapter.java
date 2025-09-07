package com.dr.kisan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.dr.kisan.R;
import com.dr.kisan.models.CommunityPost;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private Context context;
    private List<CommunityPost> posts;
    private OnPostClickListener onPostClickListener;

    public interface OnPostClickListener {
        void onPostClick(CommunityPost post);
        void onLikeClick(CommunityPost post, int position);
        void onCommentClick(CommunityPost post);
    }

    public CommunityAdapter(Context context, List<CommunityPost> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.onPostClickListener = listener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_post, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        CommunityPost post = posts.get(position);

        holder.textAuthorName.setText(post.getAuthorName());
        holder.textTitle.setText(post.getTitle());
        holder.textContent.setText(post.getContent());
        holder.textTime.setText(post.getTimeAgo());
        holder.textLikes.setText(String.valueOf(post.getLikes()));
        holder.textComments.setText(String.valueOf(post.getComments()));
        holder.textCategory.setText(post.getCategory());

        // Set author avatar (you can use Glide or Picasso for actual image loading)
        holder.imageAuthor.setImageResource(R.drawable.ic_farmer_avatar);

        // Click listeners
        holder.cardPost.setOnClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onPostClick(post);
            }
        });

        holder.layoutLike.setOnClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onLikeClick(post, position);
            }
        });

        holder.layoutComment.setOnClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onCommentClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePost(int position, CommunityPost post) {
        posts.set(position, post);
        notifyItemChanged(position);
    }

    static class CommunityViewHolder extends RecyclerView.ViewHolder {
        CardView cardPost;
        ImageView imageAuthor;
        TextView textAuthorName;
        TextView textTitle;
        TextView textContent;
        TextView textTime;
        TextView textCategory;
        View layoutLike;
        View layoutComment;
        ImageView imageLike;
        ImageView imageComment;
        TextView textLikes;
        TextView textComments;

        CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPost = itemView.findViewById(R.id.cardPost);
            imageAuthor = itemView.findViewById(R.id.imageAuthor);
            textAuthorName = itemView.findViewById(R.id.textAuthorName);
            textTitle = itemView.findViewById(R.id.textTitle);
            textContent = itemView.findViewById(R.id.textContent);
            textTime = itemView.findViewById(R.id.textTime);
            textCategory = itemView.findViewById(R.id.textCategory);
            layoutLike = itemView.findViewById(R.id.layoutLike);
            layoutComment = itemView.findViewById(R.id.layoutComment);
            imageLike = itemView.findViewById(R.id.imageLike);
            imageComment = itemView.findViewById(R.id.imageComment);
            textLikes = itemView.findViewById(R.id.textLikes);
            textComments = itemView.findViewById(R.id.textComments);
        }
    }
}
