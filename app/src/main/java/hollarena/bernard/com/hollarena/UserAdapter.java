package hollarena.bernard.com.hollarena;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserModel> list;

    public UserAdapter(List<UserModel> result) {
        this.list = result;

    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserModel userModel = list.get(position);
        holder.textname.setText(userModel.userName);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textname;
        public UserViewHolder(View itemView) {
            super(itemView);
            textname = (TextView) itemView.findViewById(R.id.text_name);
        }
    }


}
