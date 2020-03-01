package com.flores.easycurrencyconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flores.easycurrencyconverter.data.model.Rate;
import com.flores.easycurrencyconverter.data.model.Symbol;

import java.util.ArrayList;
import java.util.List;

public class DialogRecyclerViewAdapter
        extends RecyclerView.Adapter<DialogRecyclerViewAdapter.ViewHolder> {

    private List<Symbol> mValues;
    private List<String> mCodes;
    private View.OnClickListener mOnClickListener = view -> {
        String c = (String) view.getTag();
        CheckBox checkBox = view.findViewById(R.id.cb_currency);
        checkBox.setChecked(!checkBox.isChecked());
        boolean found = false;
        for (String code : mCodes) {
            if (code.equals(c)) {
                found = true;
                if (!checkBox.isChecked()) {
                    mCodes.remove(code);
                }
            }
        }
        if (!found) {
            mCodes.add(c);
        }
    };

    DialogRecyclerViewAdapter() {
    }

    public void updateSymbols(List<Symbol> values) {
        if (values != null) {
            mValues = values;
            if (mCodes == null)
                mCodes = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    public void updateRates(List<Rate> rates) {
        mCodes = new ArrayList<>();
        for (Rate rate : rates) {
            mCodes.add(rate.getCode());
        }
        notifyDataSetChanged();
    }

    public List<String> getCodes() {
        return mCodes;
    }

    @NonNull
    @Override
    public DialogRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_content, parent, false);
        return new DialogRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DialogRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mCheckBox.setText(mValues.get(position).getName());
        holder.mCheckBox.setChecked(mCodes.contains(mValues.get(position).getCode()));

        holder.itemView.setTag(mValues.get(position).getCode());
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        if (mValues == null) return 0;

        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox mCheckBox;

        ViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.cb_currency);
        }
    }
}
