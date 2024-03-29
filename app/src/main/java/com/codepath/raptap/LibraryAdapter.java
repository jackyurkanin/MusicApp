package com.codepath.raptap;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
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
import com.codepath.raptap.activities.EditActivity;
import com.codepath.raptap.models.Sound;
import com.parse.ParseFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private Context context;
    private List<Sound> feed;
    private static final String TAG = "libraryAdapter";
    private static final String DEBUG = "Debug process";
    private static int ROUNDED_CORNERS = 30;
    private static final int TRACK_SAMPLE_RATE = 44100;
    private static final int TRACK_CHANNELS = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int TRACK_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int STREAM = 0;
    private int minBufferSize;

    public LibraryAdapter(Context context, List<Sound> feed) {
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

        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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
            tvCreatedAt.setText(getRelativeTimeAgo(sound.getCreatedAt().toString()));
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
                    try {
                        File musicFile = audio.getFile();
                        setUpPlayerAndPlay(musicFile);
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void setUpPlayerAndPlay(File musicFile) {
            minBufferSize = AudioTrack.getMinBufferSize(TRACK_SAMPLE_RATE, TRACK_CHANNELS, TRACK_AUDIO_ENCODING);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            AudioFormat format = new AudioFormat.Builder()
                    .setChannelMask(TRACK_CHANNELS)
                    .setEncoding(TRACK_AUDIO_ENCODING)
                    .setSampleRate(TRACK_SAMPLE_RATE)
                    .build();
            AudioTrack musicPlayer = new AudioTrack(attributes, format, minBufferSize, AudioTrack.MODE_STREAM, STREAM);
            try {
                EditActivity.PlayAudioFileViaAudioTrack(String.valueOf(musicFile), musicPlayer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + " d";
                }
            } catch (ParseException e) {
                Log.i(TAG, "getRelativeTimeAgo failed");
                e.printStackTrace();
            }
            return "";
        }
    }
}
