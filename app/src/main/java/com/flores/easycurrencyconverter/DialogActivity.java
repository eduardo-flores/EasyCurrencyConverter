package com.flores.easycurrencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.flores.easycurrencyconverter.data.Repository;
import com.flores.easycurrencyconverter.data.model.Rate;
import com.flores.easycurrencyconverter.data.model.Symbol;
import com.flores.easycurrencyconverter.viewmodel.MainActivityViewModel;
import com.flores.easycurrencyconverter.viewmodel.MainViewModelFactory;

import java.io.Serializable;
import java.util.List;

import static com.flores.easycurrencyconverter.MainActivity.EXTRA_CODES;

public class DialogActivity extends AppCompatActivity {

    public static final String EXTRA_BASE_CODE = "DialogActivity.EXTRA_BASE_CODE";
    private List<Symbol> mSymbols;
    private List<Rate> mRates;
    private DialogRecyclerViewAdapter mAdapter;
    private MainActivityViewModel mViewModel;
    private RecyclerView recyclerView;
    private String mBaseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        setTitle(R.string.dialog_custom_title);

        MainViewModelFactory factory = new MainViewModelFactory(Repository.getInstance(getApplication()));
        mViewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);

        recyclerView = findViewById(R.id.rv_dialog_currencies);
        mAdapter = new DialogRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_BASE_CODE)) {
                mBaseCode = intentThatStartedThisActivity.getStringExtra(EXTRA_BASE_CODE);
            }
        }
        bindUI();
    }

    private void bindUI() {
        mViewModel.getRates().observe(this, rates -> {
            mRates = rates;
            mAdapter.updateRates(mRates);
        });

        mViewModel.getSymbols().observe(this, (List<Symbol> symbolsList) -> {
            mSymbols = symbolsList;
            mAdapter.updateSymbols(mSymbols);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customize_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            List<String> codes = ((DialogRecyclerViewAdapter) recyclerView.getAdapter()).getCodes();
            codes.add(mBaseCode);

            Class destinationClass = MainActivity.class;
            Intent intent = new Intent(this, destinationClass);
            intent.putExtra(EXTRA_CODES, (Serializable) codes);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
