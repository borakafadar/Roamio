package com.borakafadar.roamio.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borakafadar.roamio.App.Save.TripEntity;
import com.borakafadar.roamio.R;

import java.util.ArrayList;

public class TripsRecyclerViewAdapter extends RecyclerView.Adapter<TripsRecyclerViewAdapter.TripsViewHolder> {

    private TripsRecyclerViewInterface tripsRecyclerViewInterface;
    private Context context;
    private ArrayList<TripEntity> trips;

    public TripsRecyclerViewAdapter(Context context, ArrayList<TripEntity> trips, TripsRecyclerViewInterface tripsRecyclerViewInterface){
        this.context = context;
        this.trips = trips;
        this.tripsRecyclerViewInterface=tripsRecyclerViewInterface;
    }



    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This is where you inflate the layout (giving a look to our rows)
        //you create cards here

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_trips, parent, false);
        return new TripsViewHolder(view,tripsRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        //assigning values to the views we created in the card_view_trips layout file
        //based on the position of the recycler view

        holder.tripImage.setImageResource(R.drawable.default_trip_image); //TODO: add image
        holder.tripTitle.setText(trips.get(position).getTitle());
        holder.tripComment.setText(trips.get(position).getComments());
        holder.tripDate.setText(trips.get(position).getDate());
        holder.tripDuration.setText(trips.get(position).getDuration());
        holder.tripDistance.setText(Double.toString(trips.get(position).getDistance())); //TODO change to km

    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want displayed
        return trips.size();
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from our layout (card_view_trips) file

        ImageView tripImage;
        TextView tripTitle, tripComment, tripDate, tripDuration, tripDistance;

        public TripsViewHolder(@NonNull View itemView, TripsRecyclerViewInterface tripsRecyclerViewInterface) {
            super(itemView);

            tripImage = itemView.findViewById(R.id.tripCardImageView);
            tripTitle = itemView.findViewById(R.id.tripCardTitleTextView);
            tripComment = itemView.findViewById(R.id.tripCardCommentsTextView);
            tripDate = itemView.findViewById(R.id.tripCardDateTextView);
            tripDuration = itemView.findViewById(R.id.tripCardDurationTextView);
            tripDistance = itemView.findViewById(R.id.tripCardDistanceTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tripsRecyclerViewInterface != null){
                        if(getAdapterPosition() != RecyclerView.NO_POSITION){
                         tripsRecyclerViewInterface.onItemClicked(getAdapterPosition());
                        }
                    }
                }
            });
        }
    }
}
