package com.flores.easycurrencyconverter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.appcompat.view.menu.ActionMenuItemView;
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

import static com.flores.easycurrencyconverter.DialogActivity.EXTRA_BASE_CODE;
import static com.flores.easycurrencyconverter.viewmodel.MainActivityViewModel.EUR_CODE;


public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_CODES = "MainActivity.EXTRA_CODES";
    private SimpleItemRecyclerViewAdapter mAdapter;
    private Converter mConverter;
    private List<Symbol> mSymbols;
    private List<Rate> mRates;
    private List<String> mCodes;
    private Rate mBaseRate;

    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private ImageView mRefresh;
    private MainActivityViewModel mViewModel;
    private final View.OnClickListener mOnClickListener = view -> {

        Rate r = (Rate) view.getTag();

        List<Rate> newRates = new ArrayList<>();
        mRates.add(mBaseRate);
        for (Rate rate : mRates) {
            rate.setBase(rate.getCode().equals(r.getCode()));
            newRates.add(rate);
        }
        mViewModel.updateRates(newRates);


    };
    private EditText mBaseValue;



    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;
    private boolean callSymbolsAPI = false;
    private ImageView mBaseFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mRefresh = findViewById(R.id.iv_refresh);

        mBaseValue = findViewById(R.id.currency_value);
        mBaseFlag = findViewById(R.id.iv_base_flag);

        mRecyclerView = findViewById(R.id.rv_list_item);
        mAdapter = new SimpleItemRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        MainViewModelFactory factory = new MainViewModelFactory(Repository.getInstance(getApplication()));
        mViewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);

        getIdlingResource().setIdleState(false);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_CODES)) {
                mCodes = (List<String>) intentThatStartedThisActivity.getSerializableExtra(EXTRA_CODES);
                if (mCodes != null) {
                    mViewModel.fetchCurrency(mCodes);
                }
            }
        }

        bindUI();

    }

    private void bindUI() {
        showLoading();
        mViewModel.getBaseRate().observe(this, rate -> {
            if (rate != null) {
                mBaseRate = rate;
                mAdapter.updateBaseRate(mBaseRate);
                mBaseFlag.setImageResource(ResourcesUtil.getResourceByName(R.drawable.class, rate.getCode().toLowerCase()));
                mBaseValue.setText(String.format("%.2f", rate.getValue()));
            }

        });

        mViewModel.getRates().observe(this, rates -> {
            mRates = rates;
            showData();
            getIdlingResource().setIdleState(true);
        });


        mViewModel.getCurrency().observe(this, converter -> {
            mConverter = converter;
            if (mBaseRate == null) {
                mBaseRate = mViewModel.getDefaultRate();
            }
            if (mConverter.getSuccess())
                calculateNewConversion(mBaseRate.getCode(), mBaseRate.getValue());
        });

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
                String newBaseValue = mBaseValue.getText().toString();
                if (TextUtils.isEmpty(newBaseValue)) {
                    newBaseValue = "0";
                }
                updateBaseValue(Double.valueOf(newBaseValue));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        mRefresh.setOnClickListener(view -> {
            showLoading();
            if (mCodes == null) {
                mCodes = new ArrayList<>();
                for (Rate rate : mRates) {
                    mCodes.add(rate.getCode());
                }
                mCodes.add(mBaseRate.getCode());
            }
            mViewModel.fetchCurrency(mCodes);
        });
    }

    private void updateBaseValue(Double newValue) {
        // conversion will be (baseValue * eurvalue / oldBaseValue);
        mRates.add(mBaseRate);

        Double baseValue = mBaseRate.getValue() == 0 ? 1 : mBaseRate.getValue();
        Double conversion = newValue / baseValue;

        List<Rate> newRates = new ArrayList<>();
        for (Rate rate : mRates) {
            rate.setValue(rate.getValue() * conversion);
            newRates.add(rate);
        }
        mViewModel.updateRates(newRates);
    }

    private void calculateNewConversion(String newBase, Double newBaseValue) {
        boolean favorite = mBaseRate.isFavorite();
        // conversion will be (baseValue * eurvalue / oldBaseValue);
        Double conversion = newBaseValue * mConverter.getRates().get(EUR_CODE) / mConverter.getRates().get(newBase);

        mViewModel.deleteAllRates();

        for (String key : mConverter.getRates().keySet()) {
            mViewModel.insertRate(new Rate(key, mConverter.getRates().get(key) * conversion, key.equals(newBase), favorite));
        }
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

    private void showData() {
        mAdapter.updateValues(mRates, mOnClickListener);
        if (mRates != null) showDataView();
        else showLoading();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            mViewModel.setFavorite();
            Toast.makeText(this, getString(R.string.toast_favorite_changed), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_custom) {
            Log.d(LOG_TAG, "action_custom");
            Class destinationClass = DialogActivity.class;
            Intent intent = new Intent(this, destinationClass);
            if (mBaseRate != null) {
                intent.putExtra(EXTRA_BASE_CODE, mBaseRate.getCode());
            } else {
                intent.putExtra(EXTRA_BASE_CODE, EUR_CODE);
            }
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Log.d(LOG_TAG, "action_about");
            Class destinationClass = AboutActivity.class;
            Intent intent = new Intent(this, destinationClass);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Observe after inflate menu
        mViewModel.isFavorite().observe(this, favorite -> {
            ActionMenuItemView menuItem = findViewById(R.id.action_favorite);
            if (menuItem != null && favorite != null) {
                menuItem.setIcon(getDrawable(favorite ? R.drawable.ic_favorite_24dp : R.drawable.ic_favorite_border_24dp));
            }
        });
        return true;
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
        private View.OnClickListener mOnClickListener;

        private Rate mBaseRate;
        private List<Rate> mValues;
        private List<Symbol> mSymbols;

        SimpleItemRecyclerViewAdapter() {
        }

        void updateValues(List<Rate> values, View.OnClickListener onClickListener) {
            if (values != null) {
                mValues = values;
                mOnClickListener = onClickListener;
                notifyDataSetChanged();
            }
        }

        void updateSymbols(List<Symbol> symbols) {
            if (symbols != null && symbols.size() > 0) {
                mSymbols = symbols;
                notifyDataSetChanged();
            }
        }

        void updateBaseRate(Rate baseRate) {
            mBaseRate = baseRate;
            notifyDataSetChanged();
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
            holder.itemView.setTag(new Rate(mValues.get(position).getCode(), mValues.get(position).getValue(), true, false));
            holder.itemView.setOnClickListener(mOnClickListener);

            if (mBaseRate != null) {
                holder.mShare.setTag(String.format(
                        context.getResources().getString(R.string.share_message)
                        , getSymbol(mBaseRate.getCode())
                        , mBaseRate.getValue()
                        , getSymbol(mValues.get(position).getCode())
                        , mValues.get(position).getValue())
                );
            } else {
                holder.mShare.setTag(String.format(
                        context.getResources().getString(R.string.share_message_no_base)
                        , getSymbol(mValues.get(position).getCode())
                        , mValues.get(position).getValue())
                );
            }
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
