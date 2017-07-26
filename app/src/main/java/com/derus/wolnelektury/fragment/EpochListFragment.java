package com.derus.wolnelektury.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.derus.wolnelektury.MainActivity;
import com.derus.wolnelektury.R;
import com.derus.wolnelektury.adapter.EpochAdapter;
import com.derus.wolnelektury.data.Epoch;
import com.derus.wolnelektury.data.EpochInfo;
import com.derus.wolnelektury.listener.OnEpochItemClickListener;
import com.derus.wolnelektury.rest.ApiClient;
import com.derus.wolnelektury.rest.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EpochListFragment extends Fragment implements OnEpochItemClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String SAVED_LIST = "savedList";
    public static final String SAVED_IS_DATA_DOWNLOADED = "savedIsDataDownloaded";

    private ArrayList<Epoch> epochs = new ArrayList<>();
    private EpochAdapter epochAdapter;
    private boolean isDataDownloaded;

    private View view;
    private AlertDialog alertDialog;
    private TextView tvNoResult;
    private ProgressBar progressLoadingIndicator;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private Context context;
    private MainActivity activity;

    public EpochListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_epoch_list, container, false);

        context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_epochs);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        activity = (MainActivity) getActivity();
        progressLoadingIndicator = (ProgressBar) view.findViewById(R.id.progress_list_epochs);
        tvNoResult = (TextView) view.findViewById(R.id.text_list_epochs_no_result);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getString(R.string.title_epochs_list));
        alertDialog = new AlertDialog.Builder(context).create();

        if (savedInstanceState != null) {
            isDataDownloaded = savedInstanceState.getBoolean(SAVED_IS_DATA_DOWNLOADED);
            if (isDataDownloaded) {
                epochs = savedInstanceState.getParcelableArrayList(SAVED_LIST);
                epochAdapter = new EpochAdapter(epochs, this);
            }
        }
        if (!isDataDownloaded) {
            getEpochs();
        } else {
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setAdapter(epochAdapter);
            showView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (epochs != null) {
            outState.putParcelableArrayList(SAVED_LIST, epochs);
        }
        outState.putBoolean(SAVED_IS_DATA_DOWNLOADED, isDataDownloaded);

    }

    public void getEpochs() {
        epochAdapter = new EpochAdapter(epochs, this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(epochAdapter);

        showLoadingIndicator();
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<Epoch>> call;
        call = apiService.getListOfEpochs();
        call.enqueue(new Callback<ArrayList<Epoch>>() {
            @Override
            public void onResponse(Call<ArrayList<Epoch>> call, Response<ArrayList<Epoch>> response) {
                if (response.isSuccessful()) {
                    epochs.addAll(response.body());
                    epochAdapter.notifyDataSetChanged();
                    showView();
                    isDataDownloaded = true;
                } else {
                    showTextView(R.string.error_server_code);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Epoch>> call, Throwable t) {
                showTextView(R.string.error_fetch_data_from_server);
                isDataDownloaded = false;
            }
        });
    }

    public void getEpochsInfo(String url) {

        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<EpochInfo> call;
        call = apiService.getEpochInfo(url);
        call.enqueue(new Callback<EpochInfo>() {
            @Override
            public void onResponse(Call<EpochInfo> call, Response<EpochInfo> response) {
                if (response.isSuccessful()) {
                    String name = response.body().getName();
                    String description;
                    if (response.body().getDescription().isEmpty()) {
                        description = "\n" + getString(R.string.error_no_epoch_info);
                    } else {
                        if (Build.VERSION.SDK_INT >= 24) {
                            description = String.valueOf(Html.fromHtml(response.body().getDescription(),
                                    Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            description = String.valueOf(Html.fromHtml(response.body().getDescription()));
                        }
                    }

                    if (!alertDialog.isShowing()) {
                        alertDialog.setTitle(name);
                        alertDialog.setMessage(description);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dialog.dismiss();
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(context, "Błąd pobierania danych", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EpochInfo> call, Throwable t) {

            }
        });

    }

    private void showView() {
        progressLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.INVISIBLE);
    }

    private void showLoadingIndicator() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressLoadingIndicator.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.INVISIBLE);
    }

    private void showTextView(int id) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressLoadingIndicator.setVisibility(View.INVISIBLE);
        tvNoResult.setVisibility(View.VISIBLE);
        tvNoResult.setText(id);
    }

    @Override
    public void onEpochClick(Epoch epoch) {
        BookListFragment bookListFragment = new BookListFragment();

        Bundle args = new Bundle();
        args.putString(BookListFragment.ARGUMENT_FROM, BookListFragment.BUNDLE_EPOCH);
        args.putParcelable(BookListFragment.BUNDLE_EPOCH, epoch);
        bookListFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .replace(R.id.frame_container, bookListFragment, MainActivity.TAG_BOOK_LIST_FRAGMENT)
                .addToBackStack(MainActivity.TAG_BOOK_LIST_FRAGMENT)
                .commit();
        activity.showToolbar();
    }

    @Override
    public void onInfoClick(Epoch epoch) {
        getEpochsInfo(epoch.getHref());
    }
}
