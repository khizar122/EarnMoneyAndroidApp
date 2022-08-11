package com.OCK.earn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.OCK.earn.model.HistoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private List<HistoryModel> list;

    public HistoryAdapter(List<HistoryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {

        holder.phoneEt.setText(list.get(position).getPhone());
        holder.statusEt.setText(list.get(position).getStatus());
        holder.amountEt.setText(String.valueOf(list.get(position).getAmount()));
        holder.usernameEt.setText(list.get(position).getName());
        holder.rDateUser.setText(list.get(position).getDate());

//        Glide.with(holder.itemView.getContext().getApplicationContext())
//                .load(list.get(position).getImage())
//                .into(holder.imageview);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HistoryHolder extends RecyclerView.ViewHolder{
        private ImageView imageview;
        private TextView phoneEt,statusEt,amountEt,usernameEt,rDateUser;

            public HistoryHolder(@NonNull View itemView) {
                super(itemView);
                imageview=itemView.findViewById(R.id.image);
                phoneEt=itemView.findViewById(R.id.phone);
                statusEt=itemView.findViewById(R.id.status);
                amountEt=itemView.findViewById(R.id.amount);
                usernameEt=itemView.findViewById(R.id.trans_name);
                rDateUser=itemView.findViewById(R.id.rDateUser);



            }
        }
}
