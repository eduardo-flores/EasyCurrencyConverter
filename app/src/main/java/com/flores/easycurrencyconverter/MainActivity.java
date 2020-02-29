package com.flores.easycurrencyconverter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.flores.easycurrencyconverter.data.Repository;
import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.Symbols;
import com.flores.easycurrencyconverter.util.SimpleIdlingResource;
import com.flores.easycurrencyconverter.viewmodel.MainActivityViewModel;
import com.flores.easycurrencyconverter.viewmodel.MainViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SimpleItemRecyclerViewAdapter mAdapter;
    private Converter mConverter;
    private List<Symbols> mSymbolsList;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;

    private MainActivityViewModel mViewModel;


    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;
    private boolean callSymbolsAPI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mRecyclerView = findViewById(R.id.rv_list_item);
        mAdapter = new SimpleItemRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        MainViewModelFactory factory = new MainViewModelFactory(Repository.getInstance(getApplication()));
        mViewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);

        getIdlingResource().setIdleState(false);

        bindUI();

    }

    private void bindUI() {
        mViewModel.getCurrency().observe(this, converter -> {
            mConverter = converter;
            showData();
            getIdlingResource().setIdleState(true);
        });

        mViewModel.getSymbols().observe(this, symbolsList -> {
            mSymbolsList = symbolsList;
            if (symbolsList.size() == 0 && !callSymbolsAPI) {
                mViewModel.fetchSymbolsAPI();
                callSymbolsAPI = true;
            } else {
                ((TextView) findViewById(R.id.tv_text)).setText(String.format("%s - %s", mSymbolsList.get(0).getCode(), mSymbolsList.get(0).getName()));
            }
        });

        mViewModel.getSymbolsAPI().observe(this, symbolsAPI -> {
            for (String key : symbolsAPI.getSymbols().keySet()) {
                mViewModel.insertSymbols(new Symbols(key, symbolsAPI.getSymbols().get(key)));
            }
        });
    }

    private void showData() {
        mAdapter.updateValues(mConverter);
//        if (mConverter != null && mConverter.size() != 0) showDataView();
//        else showLoading();
        String text = "";
        for (String key : mConverter.getRates().keySet()) {
            text += key + " - " + mConverter.getRates().get(key);

        }
        ((TextView) findViewById(R.id.tv_text)).setText(text);
//        ((TextView) findViewById(R.id.tv_text)).setText(mConverter.getDate());
        if (mConverter != null) showDataView();
        else showLoading();
    }

    private void showDataView() {
        Log.d(LOG_TAG, "showMovieDataView");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        Log.d(LOG_TAG, "Loading");
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final View.OnClickListener mOnClickListener = view -> {
////            Map item = (Co) view.getTag();
//            Context context = view.getContext();
//            Intent intent = new Intent(context, ItemListActivity.class);
//            intent.putExtra(ARG_RECIPE, item);
//
//            context.startActivity(intent);
        };
        private List<Double> mValues;
        private List<String> mKeys;

        SimpleItemRecyclerViewAdapter() {
        }

        void updateValues(Converter values) {
            if (values != null && values.getRates() != null) {
                mValues = new ArrayList<>(values.getRates().values());
                mKeys = new ArrayList<>(values.getRates().keySet());
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mContentView.setText(String.format("%s %s", mKeys.get(position), mValues.get(position)));

            // Load image if it exists
//            if (!TextUtils.isEmpty(mValues.get(position).getImage())) {
//                Picasso.get()
//                        .load(mValues.get(position).getImage())
//                        .placeholder(R.drawable.ic_book)
//                        .into(holder.mImageView);
//            }
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            if (mValues == null) return 0;

            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;
            final ImageView mImageView;

            ViewHolder(View view) {
                super(view);
                mContentView = view.findViewById(R.id.currency_name);
                mImageView = view.findViewById(R.id.iv_place_holder);
            }
        }
    }

    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
