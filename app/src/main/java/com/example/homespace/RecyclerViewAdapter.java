package com.example.homespace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends FirebaseRecyclerAdapter<AgentInfoAdapter, RecyclerViewAdapter.myViewHolder> {

    private OnItemClickListener mListener;
    private Context context;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<AgentInfoAdapter> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull AgentInfoAdapter model)
    {
        holder.title.setText(model.getTitle());

        Glide
                .with(holder.agentProfilePic.getContext())
                .load(model.getProfilePic())
                .into(holder.agentProfilePic);

        Glide
                .with(holder.PropertyImage.getContext())
                .load(model.getThumbnail())
                .into(holder.PropertyImage);


        holder.agentProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //start intent agentViewProfilePage
                //pass agent....

                Intent intent = new Intent(context, agentViewProfilePage.class);
                intent.putExtra("agentData", model);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        CircleImageView agentProfilePic;
        ImageView PropertyImage;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.Card_title);
            agentProfilePic = (CircleImageView) itemView.findViewById(R.id.agent_imgbtn);
            PropertyImage = (ImageView) itemView.findViewById(R.id.card_thumbnail);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                    {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION)
                        {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
