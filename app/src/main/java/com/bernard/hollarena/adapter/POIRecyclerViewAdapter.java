package com.bernard.hollarena.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bernard.hollarena.model.Interests;
import com.bernard.hollarena.activity.PointOfInterestActivity;

import java.util.ArrayList;
import java.util.List;


public class POIRecyclerViewAdapter extends RecyclerView.Adapter<POIRecyclerViewAdapter.InterestHolder> implements View.OnClickListener {
    private final Context context;
    private List<Interests> pointOfInterest;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private ArrayList<Integer> selectedInterest = new ArrayList<>();
    private int minHeight;
    private int height;

    public POIRecyclerViewAdapter(Context context, List<Interests> interests) {
        this.pointOfInterest = interests;
        this.context = context;

        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimension = new DisplayMetrics();
        windowmanager.getDefaultDisplay().getMetrics(dimension);
        height = dimension.heightPixels;

    }

    @Override
    public InterestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(com.bernard.hollarena.R.layout.layout_cardview, parent, false);
        return new InterestHolder(v);
    }

    @Override
    public void onBindViewHolder(InterestHolder holder, int position) {
        holder.interestName.setText(pointOfInterest.get(position).interestName);
        holder.interestPic.setImageResource(pointOfInterest.get(position).interestImage);

        holder.cardView.setSelected(selectedItems.get(position, false));


    }

    @Override
    public int getItemCount() {
        return pointOfInterest.size();
    }

    @Override
    public void onClick(View v) {
        v.animate().setDuration(1000).rotationYBy(15);


    }

    public ArrayList getSelectedInterest() {

        return selectedInterest;
    }

    class InterestHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView interestName;
        ImageView interestPic;

        void setDialogue(){
            //set up dialog
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(com.bernard.hollarena.R.layout.dialogue);
            dialog.setTitle("This is my custom dialog box");
            dialog.setCancelable(true);
            //there are a lot of settings, for dialog, check them all out!

            //set up text
            TextView text = (TextView) dialog.findViewById(com.bernard.hollarena.R.id.article_text);
            text.setText(com.bernard.hollarena.R.string.lots_of_text);

            //set up button
            ImageView imageButton = (ImageView) dialog.findViewById(com.bernard.hollarena.R.id.image_button);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            //now that the dialog is set up, it's time to show it
            dialog.show();

        }
        InterestHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(com.bernard.hollarena.R.id.cv);
            interestName = (TextView) itemView.findViewById(com.bernard.hollarena.R.id.interest_name);
            interestPic = (ImageView) itemView.findViewById(com.bernard.hollarena.R.id.interest_photo);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (selectedItems.get(getAdapterPosition(), false)) {
                            selectedItems.delete(getAdapterPosition());
                            selectedInterest.remove(selectedInterest.indexOf(getAdapterPosition()));
                            cardView.setSelected(false);
                        } else {

//                            setDialogue();

                            selectedItems.put(getAdapterPosition(), true);
                            selectedInterest.add(getAdapterPosition());
                            cardView.setSelected(true);
                        }
                        notifyDataSetChanged();

                        if (selectedItems.size() > 0) {
                            PointOfInterestActivity.submitBt.setVisibility(View.VISIBLE);
                            PointOfInterestActivity.submitBt.setSelected(true);
                        } else {
                            PointOfInterestActivity.submitBt.setVisibility(View.GONE);
                            PointOfInterestActivity.submitBt.setSelected(false);

                    }
                }
            });
        }
    }
}
