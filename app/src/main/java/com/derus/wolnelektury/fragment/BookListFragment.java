package com.derus.wolnelektury.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.derus.wolnelektury.MainActivity;
import com.derus.wolnelektury.R;
import com.derus.wolnelektury.adapter.BookCursorAdapter;
import com.derus.wolnelektury.adapter.BookResponseAdapter;
import com.derus.wolnelektury.data.AuthorResponse;
import com.derus.wolnelektury.data.BookResponse;
import com.derus.wolnelektury.data.Epoch;
import com.derus.wolnelektury.db.BookDbHelper;
import com.derus.wolnelektury.listener.OnBookItemClickListener;
import com.derus.wolnelektury.rest.ApiClient;
import com.derus.wolnelektury.rest.ApiInterface;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.derus.wolnelektury.MainActivity.TAG_BOOK_DETAIL_FRAGMENT;

public class BookListFragment extends Fragment implements OnBookItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BUNDLE_AUTHOR = "author";
    public static final String BUNDLE_EPOCH = "epoch";
    public static final String ARGUMENT_FROM = "from";
    public static final String BUNDLE_DATABASE = "database";
    public static final String BUNDLE_ALL = "all";
    public static final String SAVED_LIST = "savedList";
    public static final String SAVED_TITLE = "savedTitle";
    public static final String SAVED_IS_DATA_DOWNLOADED = "isDataDownloaded";
    public static final String SAVED_IS_SAVED_LIST = "isSavedList";

    private ArrayList<BookResponse> books = new ArrayList<>();
    private Cursor booksCursor;
    private BookDbHelper dbHelper;
    private BookResponseAdapter bookResponseAdapter;
    private String mTitleAppBar;
    private boolean isDataDownloaded = false;
    private boolean isSavedList = false;

    private MainActivity activity;
    private View rootView;
    private ProgressBar progressLoadingIndicator;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private TextView tvNoResult;
    private FastScroller fastScroller;


    //TODO dodać możliwość widoku cardview
    //TODO zapisać pobrane dane (w bazie czy w pamięci) Parcel size > 1MB

    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        activity = ((MainActivity) getActivity());
        context = getActivity();
        progressLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.progress_list_book);
        tvNoResult = (TextView) rootView.findViewById(R.id.text_list_book_no_result);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_list_book);
        fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroll_list_book);
        linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = BookDbHelper.getInstance(context);

        if (savedInstanceState != null) {
            isDataDownloaded = savedInstanceState.getBoolean(SAVED_IS_DATA_DOWNLOADED);
            if (savedInstanceState.getBoolean(SAVED_IS_SAVED_LIST)) {
                isDataDownloaded = false; //jeżeli nastąpiła zmiana orientacji ekracnu pobrać ponownie dane
            } else {
                if (isDataDownloaded) {
                    books = savedInstanceState.getParcelableArrayList(SAVED_LIST);
                    mTitleAppBar = savedInstanceState.getString(SAVED_TITLE);
                    bookResponseAdapter = new BookResponseAdapter(books, R.layout.item_book, context, this);
                }
            }
        }

        if (!isDataDownloaded) {
            if (getArguments() != null) {
                switch (getArguments().getString(ARGUMENT_FROM)) {
                    case BUNDLE_AUTHOR:
                        AuthorResponse authorResponse = getArguments().getParcelable(BUNDLE_AUTHOR);
                        mTitleAppBar = authorResponse.getName();
                        getBooks(authorResponse.getHref(), BUNDLE_AUTHOR);
                        break;

                    case BUNDLE_EPOCH:
                        Epoch epoch = getArguments().getParcelable(BUNDLE_EPOCH);
                        mTitleAppBar = epoch.getName();
                        getBooks(epoch.getHref(), BUNDLE_EPOCH);
                        break;

                    case BUNDLE_DATABASE:
                        booksCursor = dbHelper.getAllBooksFromDatabase();
                        BookCursorAdapter bookCursorAdapter = new BookCursorAdapter(context, booksCursor, this);
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        mRecyclerView.setAdapter(bookCursorAdapter);
                        fastScroller.setRecyclerView(mRecyclerView);
                        if (booksCursor.getCount() == 0) {
                            showTextView(R.string.error_no_saved_books);
                        }
                        mTitleAppBar = getString(R.string.title_saved_book_list);
                        break;

                    case BUNDLE_ALL:
                        getBooks("", BUNDLE_ALL);
                        mTitleAppBar = getString(R.string.title_book_list);
                        break;
                }
            }
        } else {
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(bookResponseAdapter);
            fastScroller.setRecyclerView(mRecyclerView);
            showView();
        }
        getActivity().setTitle(mTitleAppBar);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!isSavedList)
            outState.putParcelableArrayList(SAVED_LIST, books);

        outState.putBoolean(SAVED_IS_SAVED_LIST, isSavedList);
        outState.putBoolean(SAVED_IS_DATA_DOWNLOADED, isDataDownloaded);
        outState.putString(SAVED_TITLE, mTitleAppBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        isSavedList = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        if (booksCursor != null)
            booksCursor.close();
    }

    private void getBooks(String url, String arg) {
        bookResponseAdapter = new BookResponseAdapter(books, R.layout.item_book, context, this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(bookResponseAdapter);
        fastScroller.setRecyclerView(mRecyclerView);

        showLoadingIndicator();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        final int error_message;
        Call<ArrayList<BookResponse>> call;
        switch (arg) {
            case BUNDLE_ALL:
                call = apiService.getAllBook();
                error_message = R.string.error_server_code;
                break;
            case BUNDLE_AUTHOR:
                call = apiService.getAuthorBooks(url);
                error_message = R.string.error_no_author_books;
                break;
            case BUNDLE_EPOCH:
                call = apiService.getEpochBooks(url);
                error_message = R.string.error_no_epoch_books;
                break;
            default:
                call = apiService.getAllBook();
                error_message = R.string.error_server_code;
                break;
        }

        call.enqueue(new Callback<ArrayList<BookResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<BookResponse>> call, Response<ArrayList<BookResponse>> response) {
                if (response.isSuccessful()) {
                    books.addAll(response.body());
                    sortAlphabetically(books);
                    bookResponseAdapter.notifyDataSetChanged();
                    isDataDownloaded = true;
                    showView();
                } else {
                    showTextView(error_message);
                    isDataDownloaded = false;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookResponse>> call, Throwable t) {
                showTextView(R.string.error_fetch_data_from_server);
                isDataDownloaded = false;
            }
        });
    }


    public void sortAlphabetically(ArrayList<BookResponse> books) {
        Collections.sort(books,
                new Comparator<BookResponse>() {
                    public int compare(final BookResponse a, final BookResponse d) {
                        return (a.getTitle().compareTo(d.getTitle()));
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
    public void onBookItemClick(BookResponse bookResponse, ImageView sharedImageView, TextView sharedTitle, TextView sharedAuthor) {

        Bitmap imageCover = ((BitmapDrawable) sharedImageView.getDrawable()).getBitmap();
        Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_cover);

        byte[] image = null;
        if (!imageCover.sameAs(b)) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageCover.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            image = stream.toByteArray();
        }

        Fragment bookDetailFragment = BookDetailFragment.newInstance(bookResponse,
                image,
                ViewCompat.getTransitionName(sharedImageView),
                ViewCompat.getTransitionName(sharedTitle),
                ViewCompat.getTransitionName(sharedAuthor));

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .addSharedElement(sharedImageView, ViewCompat.getTransitionName(sharedImageView))
                .addSharedElement(sharedTitle, ViewCompat.getTransitionName(sharedTitle))
                .addSharedElement(sharedAuthor, ViewCompat.getTransitionName(sharedAuthor))
                .addToBackStack(TAG_BOOK_DETAIL_FRAGMENT)
                .replace(R.id.frame_container, bookDetailFragment, TAG_BOOK_DETAIL_FRAGMENT)
                .commit();

        activity.showToolbar();
        isSavedList = true;
    }

    @Override
    public void onSavedBookClick(int id, ImageView sharedImageView, TextView sharedTitle, TextView sharedAuthor) {
        Fragment bookDetailFragment = BookDetailFragment.newInstanceFromDatabase(
                id,
                ViewCompat.getTransitionName(sharedImageView),
                ViewCompat.getTransitionName(sharedTitle),
                ViewCompat.getTransitionName(sharedAuthor));

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .addSharedElement(sharedImageView, ViewCompat.getTransitionName(sharedImageView))
                .addSharedElement(sharedTitle, ViewCompat.getTransitionName(sharedTitle))
                .addSharedElement(sharedAuthor, ViewCompat.getTransitionName(sharedAuthor))
                .addToBackStack(TAG_BOOK_DETAIL_FRAGMENT)
                .replace(R.id.frame_container, bookDetailFragment, TAG_BOOK_DETAIL_FRAGMENT)
                .commit();

        activity.showToolbar();
    }
}
