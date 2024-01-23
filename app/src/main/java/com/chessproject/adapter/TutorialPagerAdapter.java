package com.chessproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chessproject.R;
import com.chessproject.entity.TutorialInfo;

import java.util.ArrayList;

public class TutorialPagerAdapter extends RecyclerView.Adapter<TutorialPagerAdapter.TutorialPagerViewHolder> {

    private Context context;
    private ArrayList<TutorialInfo> tutorialSlide;
    private LayoutInflater inflater;

    public TutorialPagerAdapter(Context context, ArrayList<TutorialInfo> tutorialSlide){
        this.context = context;
        this.tutorialSlide = tutorialSlide;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TutorialPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View imageView = inflater.inflate(R.layout.item_tutorial, parent, false);
        return new TutorialPagerViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialPagerViewHolder holder, int position) {
        TutorialInfo slide = tutorialSlide.get(position);
        int imageId = slide.getImageResourceId();
        String description = slide.getDescription();
        holder.imageView.setImageResource(imageId);
        holder.textView.setText(description);
    }

    @Override
    public int getItemCount() {
        return (tutorialSlide == null) ? 0 : tutorialSlide.size();
    }

    public static class TutorialPagerViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;
        public TutorialPagerViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageTutorial);
            textView = itemView.findViewById(R.id.textTutorial);
        }
    }
}