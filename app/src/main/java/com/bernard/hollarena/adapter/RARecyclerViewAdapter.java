package com.bernard.hollarena.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bernard.hollarena.ReadArticlesActivity;

import java.util.List;

class RARecyclerViewAdapter extends RecyclerView.Adapter<RARecyclerViewAdapter.InterestHolder> implements View.OnClickListener {
private final Context context;
    List<String> articles;

private int minHeight;
private int height;

    public RARecyclerViewAdapter(ReadArticlesActivity context, List<String> articles) {
        this.articles = articles;
        this.context = context;
    }

//
//        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics dimension = new DisplayMetrics();
//        windowmanager.getDefaultDisplay().getMetrics(dimension);
//        height = dimension.heightPixels;

@Override
public RARecyclerViewAdapter.InterestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(com.bernard.hollarena.R.layout.layout_cardview, parent, false);
        return new InterestHolder(v);
        }

@Override
public void onBindViewHolder(InterestHolder holder, int position) {
        holder.articleText.setText(articles.get(position));




        }

@Override
public int getItemCount() {
        return articles.size();
        }

@Override
public void onClick(View v) {


}
class InterestHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    TextView articleText;

    void setDialogue() {
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
        articleText = (TextView) itemView.findViewById(com.bernard.hollarena.R.id.interest_name);


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
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
*/

            }
        });
    }
}

}
