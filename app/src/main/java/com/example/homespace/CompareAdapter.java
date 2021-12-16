package com.example.homespace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.ViewHolder> {

    Context context;
    ArrayList<AgentInfoAdapter> compareList;

    public CompareAdapter(Context context, ArrayList<AgentInfoAdapter> compareList) {
        this.context = context;
        this.compareList = compareList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.compare_text_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompareAdapter.ViewHolder holder, int position) {

        ArrayList<String> sliderItems = new ArrayList<>();

        if (compareList!=null && compareList.size() >0)
        {
            AgentInfoAdapter ag = compareList.get(position);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ag.itemType + "/" + ag.getId());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    AuctionHelperClass aucData = new AuctionHelperClass();

                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        aucData = ds.getValue(AuctionHelperClass.class);

                        if (ag.getPushID().equals(aucData.getPushID()))
                        {
                            break;
                        }
                    }

                    holder.category.setText(aucData.getCategory());
                    holder.builtup.setText(aucData.getBuiltup()+" ft");
                    holder.furnishing.setText(aucData.getFurnishing());
                    holder.landarea.setText(aucData.getLandArea()+" ft");
                    holder.price.setText("RM "+aucData.getPrice());
                    holder.bedroom.setText(aucData.getBedrooms()+"");
                    holder.washroom.setText(aucData.getToilets()+"");
                    holder.tenure.setText(aucData.getTenure());
                    holder.type.setText(aucData.getItemType());
                    holder.title.setText(aucData.getTitle());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }});

            //firebase reference path
            reference = FirebaseDatabase.getInstance().getReference(ag.itemType + "/" + ag.id + "/" + ag.pushID + "/PropertyImage");

            //get images
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        sliderItems.add(ds.getValue().toString());
                    }
                    ImageSliderAdapter adapter = new ImageSliderAdapter(context, sliderItems);
                    holder.viewPager.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }});

        }

    }

    @Override
    public int getItemCount() {
        return compareList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView category,tenure,furnishing,type,builtup,landarea,bedroom,washroom,price,title;
        ViewPager viewPager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category = (TextView) itemView.findViewById(R.id.compare_category);
            tenure = (TextView) itemView.findViewById(R.id.compare_tenure);
            furnishing = (TextView) itemView.findViewById(R.id.compare_furnishing);
            type = (TextView) itemView.findViewById(R.id.compare_type);
            builtup = (TextView) itemView.findViewById(R.id.compare_builtup);
            landarea = (TextView) itemView.findViewById(R.id.compare_landarea);
            bedroom = (TextView) itemView.findViewById(R.id.compare_bedroom);
            washroom = (TextView) itemView.findViewById(R.id.compare_washroom);
            price = (TextView) itemView.findViewById(R.id.compare_price);
            title = (TextView) itemView.findViewById(R.id.compare_title);
            viewPager = (ViewPager) itemView.findViewById(R.id.compare_viewPager);
        }
    }
}
