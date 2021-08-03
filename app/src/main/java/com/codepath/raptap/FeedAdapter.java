package com.codepath.raptap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.raptap.models.Sound;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private Context context;
    private List<Sound> feed;
    private static final String POST = "feedAdapter";
    private static final String DEBUG = "Debug process";
    private static int ROUNDED_CORNERS = 30;


    public FeedAdapter(Context context, List<Sound> feed) {
        this.context = context;
        this.feed = feed;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create the view and return it as a viewHolder
        View v = LayoutInflater.from(context).inflate(R.layout.sound_post_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sound sound = feed.get(position);
        holder.bind(sound);
    }

    @Override
    public int getItemCount() {
        return feed.size();
    }

    public void clear() {
        feed.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Sound> list) {
        feed.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvSongName;
        private TextView tvDescription;
        private TextView tvCreatedAt;
        private ImageView ivCoverImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCoverImage = itemView.findViewById(R.id.ivCoverImage);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvUsername = itemView.findViewById(R.id.tvHandle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(Sound sound) {
            // Bind the post data to the view elements
            tvDescription.setText(sound.getDescription());
            tvUsername.setText("by " + sound.getUser().getUsername());
            tvSongName.setText(sound.getSongName());
//            tvCreatedAt.setText(dateTime(sound.getCreatedAt()));
            ParseFile audio = sound.getSound();
            ParseFile image = sound.getCoverPic();
            if (image != null) {
                Glide.with(context).load(image.getUrl())
                        .centerCrop()
                        .transform(new RoundedCorners(ROUNDED_CORNERS))
                        .into(ivCoverImage);
            }
            ivCoverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ToDo:
                    //   Make onClick play music and advance a progress bar
                }
            });
        }

//        private int dateTime(Date createdAt) {
//        }

    }
}
