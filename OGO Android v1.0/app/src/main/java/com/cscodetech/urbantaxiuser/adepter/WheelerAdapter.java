package com.customerogo.app.adepter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.customerogo.app.R;
import com.customerogo.app.model.VehicleDataItem;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.utility.SessionManager;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WheelerAdapter extends RecyclerView.Adapter<WheelerAdapter.MyViewHolder> {


    double dis;
    private Context mContext;
    private List<VehicleDataItem> wheeleritemList;
    private RecyclerTouchListener listener;
    SessionManager sessionManager;
    int prSelct;

    public interface RecyclerTouchListener {

        public void onClickWheelerInfo(VehicleDataItem item, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_name)
        TextView txtName;

        @BindView(R.id.txt_price)
        TextView txtPrice;

        @BindView(R.id.lvl_click)
        LinearLayout lvlClick;
        @BindView(R.id.img_icon)
        ImageView imgIcon;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public WheelerAdapter(Context mContext, List<VehicleDataItem> wheeleritemList, final RecyclerTouchListener listener, double dst) {
        this.mContext = mContext;
        this.wheeleritemList = wheeleritemList;
        this.listener = listener;
        this.dis = dst;
        sessionManager = new SessionManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wheeler, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        VehicleDataItem item = wheeleritemList.get(position);
        holder.txtName.setText(item.getTitle());
        Glide.with(mContext).load(APIClient.baseUrl + "/" + item.getImg()).thumbnail(Glide.with(mContext).load(R.drawable.cars)).into(holder.imgIcon);

        double tempp = 0;
        if (dis <= item.getUkms()) {
            tempp = item.getUprice();
        } else {
            double km = dis - item.getUkms();
            tempp = item.getUprice() + (km * item.getAprice());
        }

        holder.txtPrice.setText("$" + new DecimalFormat("##.##").format(tempp));


        if (item.isSelct()) {
            holder.txtName.setTextColor(mContext.getResources().getColor(R.color.black));

            holder.txtPrice.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.lvlClick.setBackgroundResource(R.drawable.squrebg);

        } else {
            holder.lvlClick.setBackgroundResource(0);
            holder.txtName.setTextColor(mContext.getResources().getColor(R.color.gray1));

            holder.txtPrice.setTextColor(mContext.getResources().getColor(R.color.gray1));


        }

        holder.lvlClick.setOnClickListener(v -> {

            if (item.isSelct()) {
                item.setSelct(false);
            } else {
                VehicleDataItem itema = wheeleritemList.get(prSelct);
                itema.setSelct(false);
                wheeleritemList.set(prSelct, itema);
                item.setSelct(true);
                prSelct = position;
                notifyDataSetChanged();
            }

            listener.onClickWheelerInfo(item, position);


        });

    }

    @Override
    public int getItemCount() {
        return wheeleritemList.size();
    }
}