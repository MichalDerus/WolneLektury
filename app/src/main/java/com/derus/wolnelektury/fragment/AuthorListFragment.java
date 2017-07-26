package com.derus.wolnelektury.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.derus.wolnelektury.MainActivity;
import com.derus.wolnelektury.R;
import com.derus.wolnelektury.adapter.AuthorResponseAdapter;
import com.derus.wolnelektury.data.AuthorResponse;
import com.derus.wolnelektury.listener.OnAuthorItemClickListener;
import com.derus.wolnelektury.rest.ApiClient;
import com.derus.wolnelektury.rest.ApiInterface;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.derus.wolnelektury.MainActivity.TAG_BOOK_LIST_FRAGMENT;


public class AuthorListFragment extends Fragment implements OnAuthorItemClickListener {

    public static final String SAVED_LIST = "savedList";
    public static final String SAVED_IS_DATA_DOWNLOADED = "savedIsDataDownloaded";
    private AuthorResponseAdapter authorResponseAdapter;
    private ArrayList<AuthorResponse> authors = new ArrayList<>();
    private boolean isDataDownloaded;

    private TextView tvNoResult;
    private ProgressBar progressLoadingIndicator;
    private RecyclerView mRecyclerView;
    private FastScroller mFastScroller;
    private LinearLayoutManager mLinearLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private Context context;
    private MainActivity activity;

    public AuthorListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_list, container, false);

        context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_author);
        mFastScroller = (FastScroller) view.findViewById(R.id.fastscroll_list_author);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        activity = (MainActivity) getActivity();
        progressLoadingIndicator = (ProgressBar) view.findViewById(R.id.progress_list_author);
        tvNoResult = (TextView) view.findViewById(R.id.text_list_author_no_result);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.title_author_list);
        if (savedInstanceState != null) {
            isDataDownloaded = savedInstanceState.getBoolean(SAVED_IS_DATA_DOWNLOADED);
            if (isDataDownloaded) {
                authors = savedInstanceState.getParcelableArrayList(SAVED_LIST);
                authorResponseAdapter = new AuthorResponseAdapter(authors, this);
            }
        }

        if (!isDataDownloaded) {
            getAuthors();
        } else {
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setAdapter(authorResponseAdapter);
            mFastScroller.setRecyclerView(mRecyclerView);
            showView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (authors != null) {
            outState.putParcelableArrayList(SAVED_LIST, authors);
        }
        outState.putBoolean(SAVED_IS_DATA_DOWNLOADED, isDataDownloaded);
    }


    public void getAuthors() {
        authorResponseAdapter = new AuthorResponseAdapter(authors, this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(authorResponseAdapter);
        mFastScroller.setRecyclerView(mRecyclerView);

        showLoadingIndicator();
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<AuthorResponse>> call;
        call = apiService.getListOfAuthors();
        call.enqueue(new Callback<ArrayList<AuthorResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<AuthorResponse>> call, Response<ArrayList<AuthorResponse>> response) {
                if (response.isSuccessful()) {
                    authors.addAll(response.body());
                    sortAlphabetically(authors);
                    authorResponseAdapter.notifyDataSetChanged();
                    showView();
                    isDataDownloaded = true;
                } else {
                    showTextView(R.string.error_server_code);
                    isDataDownloaded = false;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AuthorResponse>> call, Throwable t) {
                showTextView(R.string.error_fetch_data_from_server);
                isDataDownloaded = false;
            }
        });
    }


    public void sortAlphabetically(ArrayList<AuthorResponse> authors) {
        Collections.sort(authors,
                new Comparator<AuthorResponse>() {
                    public int compare(final AuthorResponse a, final AuthorResponse d) {
                        return (a.getName().compareTo(d.getName()));
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
    public void onAuthorItemClick(AuthorResponse author) {
        BookListFragment bookListFragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putString(BookListFragment.ARGUMENT_FROM, BookListFragment.BUNDLE_AUTHOR);
        args.putParcelable(BookListFragment.BUNDLE_AUTHOR, author);
        bookListFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .replace(R.id.frame_container, bookListFragment, TAG_BOOK_LIST_FRAGMENT)
                .addToBackStack(TAG_BOOK_LIST_FRAGMENT)
                .commit();
        activity.showToolbar();
    }
}
