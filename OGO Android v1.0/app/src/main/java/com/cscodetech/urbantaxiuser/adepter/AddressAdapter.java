package com.customerogo.app.adepter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.customerogo.app.R;
import com.customerogo.app.activity.SelectCarActivity;
import com.customerogo.app.model.Address;
import com.customerogo.app.utility.SessionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {




    private List<Address> historyItems;

    Context context;
    SessionManager sessionManager;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_ptitel)
        public TextView txtPtitel;
        @BindView(R.id.txt_paddress)
        public TextView txtPaddress;
        @BindView(R.id.lvl_click)
        public LinearLayout lvlClick;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public AddressAdapter(Context context,List<Address> historyItems) {

        this.historyItems = historyItems;
        this.context=context;
        sessionManager=new SessionManager(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Address item = historyItems.get(position);
        holder.txtPtitel.setText("" + item.getTitle());
        holder.txtPaddress.setText("" + item.getAddres());


        holder.lvlClick.setOnClickListener(v -> {


            sessionManager.setAddressDrop(item);

            context.startActivity(new Intent(context, SelectCarActivity.class));


        });

    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }
}