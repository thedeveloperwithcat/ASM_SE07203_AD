package com.example.se07203_b5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.se07203_b5.Models.RecurringExpense;
import com.example.se07203_b5.R;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecurringAdapter extends RecyclerView.Adapter<RecurringAdapter.ViewHolder> {
    private Context context;
    private ArrayList<RecurringExpense> list;

    public RecurringAdapter(Context context, ArrayList<RecurringExpense> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recurring_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecurringExpense item = list.get(position);
        holder.tvName.setText(item.getName());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvAmount.setText(currencyFormat.format(item.getAmount()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String dateString = sdf.format(new Date(item.getNextDueDate()));

        holder.tvFrequencyDate.setText(item.getFrequency() + " • Due " + dateString);
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFrequencyDate, tvAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvFrequencyDate = itemView.findViewById(R.id.tvFrequencyDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}