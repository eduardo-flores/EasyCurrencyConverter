package com.flores.easycurrencyconverter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.flores.easycurrencyconverter.data.Repository;
import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.Rate;
import com.flores.easycurrencyconverter.data.model.Symbol;
import com.flores.easycurrencyconverter.util.ResourcesUtil;
import com.flores.easycurrencyconverter.util.SimpleIdlingResource;
import com.flores.easycurrencyconverter.viewmodel.MainActivityViewModel;
import com.flores.easycurrencyconverter.viewmodel.MainViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SimpleItemRecyclerViewAdapter mAdapter;
    private Converter mConverter;
    private List<Symbol> mSymbols;
    private List<Rate> mRates;

    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private ImageView mRefresh;
    private EditText mBaseValue;

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

        mRefresh = findViewById(R.id.iv_refresh);

        mBaseValue = findViewById(R.id.currency_value);

        mRecyclerView = findViewById(R.id.rv_list_item);
        mAdapter = new SimpleItemRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        MainViewModelFactory factory = new MainViewModelFactory(Repository.getInstance(getApplication()));
        mViewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);

        getIdlingResource().setIdleState(false);

        bindUI();

    }

    private void bindUI() {
        mViewModel.getRates().observe(this, rates -> {
            mRates = rates;
            showData();
            getIdlingResource().setIdleState(true);
        });

        mViewModel.getCurrency().observe(this, converter -> mConverter = converter);

        mViewModel.getSymbols().observe(this, (List<Symbol> symbolsList) -> {
            mSymbols = symbolsList;
            if (symbolsList.size() == 0 && !callSymbolsAPI) {
                mViewModel.fetchSymbolsAPI();
                callSymbolsAPI = true;
            } else {
                mAdapter.updateSymbols(symbolsList);
            }
        });

        mViewModel.getSymbolsAPI().observe(this, symbolsAPI -> {
            for (String key : symbolsAPI.getSymbols().keySet()) {
                mViewModel.insertSymbol(new Symbol(key, symbolsAPI.getSymbols().get(key)));
            }
        });

        mBaseValue.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mBaseValue.clearFocus();
                calculateNewConversion("EUR", mBaseValue.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }

    private void calculateNewConversion(String newBase, String newBaseValue) {
        Double baseValue = Double.valueOf(newBaseValue);
        // conversion will be (baseValue * eurvalue / oldBaseValue);
        Double conversion = baseValue * mConverter.getRates().get(MainActivityViewModel.EUR_CODE) / mConverter.getRates().get(newBase);

        mViewModel.deleteAllRates();

        for (String key : mConverter.getRates().keySet()) {
            mViewModel.insertRate(new Rate(key, mConverter.getRates().get(key) * conversion, key.equals(newBase)));
        }
    }

    private void showData() {
        mAdapter.updateValues(mRates);
        for (Rate rate :
                mRates) {
            if (rate.isBase()) {
                mBaseValue.setText(String.format("%.2f", rate.getValue()));
            }
        }
        if (mRates != null) showDataView();
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

        private final View.OnClickListener mShareOnClickListener = view -> {
            Context context = view.getContext();
            String shareBody = (String) view.getTag();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_subject));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_using)));
        };
        private final View.OnClickListener mOnClickListener = view -> {
            Context context = view.getContext();

            Rate rate = (Rate) view.getTag();

            Toast.makeText(context, rate.getCode(), Toast.LENGTH_SHORT).show();

        };
        private String base;
        private Double baseValue;
        private List<Rate> mValues;
        private List<Symbol> mSymbols;

        SimpleItemRecyclerViewAdapter() {
        }

        void updateValues(List<Rate> values) {
            if (values != null) {
                mValues = new ArrayList<>();
                for (Rate rate : values) {
                    if (rate.isBase()) {
                        base = rate.getCode();
                        baseValue = rate.getValue();
                    } else {
                        mValues.add(rate);
                    }

                }
                notifyDataSetChanged();
            }
        }

        void updateSymbols(List<Symbol> symbols) {
            if (symbols != null && symbols.size() > 0) {
                mSymbols = symbols;
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
            holder.mCurrencyName.setText(getSymbol(mValues.get(position).getCode()));
            holder.mCurrencyValue.setText(String.format("%.4f", mValues.get(position).getValue()));
            holder.mImageView.setImageResource(ResourcesUtil.getResourceByName(R.drawable.class, mValues.get(position).getCode().toLowerCase()));
            Context context = holder.mShare.getContext();
            holder.itemView.setTag(new Rate(mValues.get(position).getCode(), mValues.get(position).getValue(), true));
            holder.itemView.setOnClickListener(mOnClickListener);

            holder.mShare.setTag(String.format(
                    context.getResources().getString(R.string.share_message)
                    , getSymbol(base)
                    , baseValue
                    , getSymbol(mValues.get(position).getCode())
                    , mValues.get(position).getValue())
            );
            holder.mShare.setOnClickListener(mShareOnClickListener);
        }

        private String getSymbol(String code) {
            String result = code;
            if (mSymbols != null) {
                for (Symbol symbol : mSymbols) {
                    if (code.equals(symbol.getCode())) {
                        result = symbol.getName();
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public int getItemCount() {
            if (mValues == null) return 0;

            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mCurrencyName;
            final TextView mCurrencyValue;
            final ImageView mImageView;
            final ImageView mShare;

            ViewHolder(View view) {
                super(view);
                mCurrencyName = view.findViewById(R.id.tv_currency_name);
                mCurrencyValue = view.findViewById(R.id.tv_currency_value);
                mImageView = view.findViewById(R.id.iv_place_holder);
                mShare = view.findViewById(R.id.iv_share);
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
