package com.vinod.sqlitedatabaseapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Vector;

class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.MyHolder> {
    private   Context context;
    Vector<EmployeeDO> vec;
    public RecordsAdapter(Context context,Vector<EmployeeDO> vec) {
        this.vec =vec;
        this.context =context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((MainActivity) context).getLayoutInflater().inflate(R.layout.item_view,null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        EmployeeDO employeeDO = vec.get(position);
        holder.tvId.setText(employeeDO.id+"");
        holder.tvName.setText(employeeDO.name+"");
        holder.tvSalary.setText(employeeDO.salary+"");
        holder.tvSalary.setText(employeeDO.column1 +"");
        if(employeeDO.isEnable)
            holder.tvId.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        else
            holder.tvId.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);


    }

    @Override
    public int getItemCount() {
        return vec!=null ?vec.size():0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvName;
        private TextView tvSalary;
        private TextView tvColumn1;

        public MyHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvName= itemView.findViewById(R.id.tvName);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvColumn1= itemView.findViewById(R.id.tvColumn1);
        }
    }
    public void refresh(Vector<EmployeeDO>vec)
    {
        this.vec =vec;
        notifyDataSetChanged();
    }
}
