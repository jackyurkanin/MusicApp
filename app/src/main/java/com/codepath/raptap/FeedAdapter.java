package com.codepath.raptap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.raptap.models.Sound;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

import static com.codepath.raptap.activities.SoundActivity.DEBUG;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private Context context;
    private List<Sound> feed;
    private static final String POST = "post";

    public FeedAdapter(Context context, List<Sound> feed) {
        this.context = context;
        this.feed = feed;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create the view and return it as a viewHolder
        View v = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvUsername;
        private TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(Sound sound) {
            // Bind the post data to the view elements
            tvDescription.setText(sound.getDescription());
            tvUsername.setText("@" + sound.getUser().getUsername());
            ParseFile audio = sound.getSound();
        }
    }
}
