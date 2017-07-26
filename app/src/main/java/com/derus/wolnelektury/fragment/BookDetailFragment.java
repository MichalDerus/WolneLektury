package com.derus.wolnelektury.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.derus.wolnelektury.ImageViewActivity;
import com.derus.wolnelektury.MainActivity;
import com.derus.wolnelektury.R;
import com.derus.wolnelektury.ReaderActivity;
import com.derus.wolnelektury.data.Author;
import com.derus.wolnelektury.data.Book;
import com.derus.wolnelektury.data.BookResponse;
import com.derus.wolnelektury.db.BookContract;
import com.derus.wolnelektury.db.BookDbHelper;
import com.derus.wolnelektury.receiver.ConnectivityReceiver;
import com.derus.wolnelektury.rest.ApiClient;
import com.derus.wolnelektury.rest.ApiInterface;
import com.derus.wolnelektury.utilities.FilesUtil;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class BookDetailFragment extends Fragment implements View.OnClickListener {
    private static final String BUNDLE_BOOK_RESPONSE = "book_response";
    private static final String BUNDLE_TRANSITION_IMAGE = "transition_image";
    private static final String BUNDLE_TRANSITION_TITLE = "transition_title";
    private static final String BUNDLE_TRANSITION_AUTHOR = "transition_author";
    private static final String BUNDLE_IMAGE_COVER = "image_cover";
    private static final String BUNDLE_BOOK_ID = "book_id";
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_IS_BITMAP = "is_bitmap";
    public static final String EXTRA_TITLE = "extra_title";
    private static final String SAVED_AUTHOR_NAME = "saved_author_name";
    private static final String SAVED_BOOK_TITLE = "saved_book_title";
    private static final String SAVED_AUTHOR_DESCRIPTION = "saved_author_description";
    private static final String SAVED_BOOK_PDF = "saved_book_pdf";
    private static final String SAVED_BOOK = "saved_book";
    private static final String SAVED_BOOK_HREF = "saved_book_href";
    private static final String SAVED_BOOK_HTML = "saved_book_html";
    private static final String SAVED_BOOK_IMAGE_PATH = "saved_book_image_path";
    private static final String SAVED_BOOK_PDF_PATH = "saved_book_pdf_path";
    private static final String SAVED_BOOK_COVER_URL = "saved_book_cover_url";

    private Book book;
    private Author author;
    private File pdfFile;
    private File bitmapFile;
    private String mBookTitle;
    private String mAuthorName;
    private String mAuthorDescription;
    private String mBookPdf;
    private String mBookHtml;
    private String mBookCover;
    private String mBookPdfPath;
    private String mBookDirectoryPath;
    private String mBookImagePath;
    private String mBookHref;

    private MainActivity activity;
    private Context context;
    private ImageView imageCoverBook;
    private TextView tvBookTitle;
    private TextView tvAuthorName;
    private TextView tvAuthorInfoName;
    private ExpandableTextView tvAuthorInfoDescription;
    private LinearLayout linearLayout;
    private ProgressBar progressLoadingIndicator;
    private TextView tvNoResult;
    private Bitmap imageCover;
    private BookResponse bookResponse;
    private BookDbHelper dbHelper;


    public BookDetailFragment() {

    }

    public static BookDetailFragment newInstance(BookResponse bookResponse, byte[] bitmap,
                                                 String transitionImage, String transitionTitle, String transitionAuthor) {
        BookDetailFragment fragment = new BookDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_BOOK_RESPONSE, bookResponse);
        args.putByteArray(BUNDLE_IMAGE_COVER, bitmap);
        args.putString(BUNDLE_TRANSITION_IMAGE, transitionImage);
        args.putString(BUNDLE_TRANSITION_TITLE, transitionTitle);
        args.putString(BUNDLE_TRANSITION_AUTHOR, transitionAuthor);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookDetailFragment newInstanceFromDatabase(int id, String transitionImage, String transitionTitle, String transitionAuthor) {
        BookDetailFragment fragment = new BookDetailFragment();

        Bundle args = new Bundle();
        args.putInt(BUNDLE_BOOK_ID, id);
        args.putString(BUNDLE_TRANSITION_IMAGE, transitionImage);
        args.putString(BUNDLE_TRANSITION_TITLE, transitionTitle);
        args.putString(BUNDLE_TRANSITION_AUTHOR, transitionAuthor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().postponeEnterTransition();
        //postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
        imageCoverBook = (ImageView) rootView.findViewById(R.id.image_detail_book_cover);
        tvBookTitle = (TextView) rootView.findViewById(R.id.text_detail_book_title_activity);
        tvAuthorName = (TextView) rootView.findViewById(R.id.text_detail_book_author_name_activity);
        tvAuthorInfoName = (TextView) rootView.findViewById(R.id.text_author_info_name);
        tvAuthorInfoDescription = (ExpandableTextView) rootView.findViewById(R.id.expand_text_about_author);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.book_info_layout);
        progressLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.progress_detail_book);
        tvNoResult = (TextView) rootView.findViewById(R.id.text_detail_book_no_result);
        ImageView btnReadOnline = (ImageView) rootView.findViewById(R.id.image_button_read_online);
        ImageView btnReadPdf = (ImageView) rootView.findViewById(R.id.image_button_read_pdf);

        btnReadPdf.setOnClickListener(this);
        btnReadOnline.setOnClickListener(this);
        imageCoverBook.setOnClickListener(this);

        context = getActivity();
        activity = (MainActivity) getActivity();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageCover = null;
        mAuthorDescription = "";
        byte[] byteImage;
        dbHelper = BookDbHelper.getInstance(context);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_IMAGE_COVER)) {
                byteImage = savedInstanceState.getByteArray(BUNDLE_IMAGE_COVER);
                assert byteImage != null;
                imageCover = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
            }
            mBookHref = savedInstanceState.getString(SAVED_BOOK_HREF);
            mAuthorName = savedInstanceState.getString(SAVED_AUTHOR_NAME);
            mBookTitle = savedInstanceState.getString(SAVED_BOOK_TITLE);
            mAuthorDescription = savedInstanceState.getString(SAVED_AUTHOR_DESCRIPTION);
            mBookPdf = savedInstanceState.getString(SAVED_BOOK_PDF);
            mBookHtml = savedInstanceState.getString(SAVED_BOOK_HTML);
            mBookPdfPath = savedInstanceState.getString(SAVED_BOOK_PDF_PATH);
            mBookImagePath = savedInstanceState.getString(SAVED_BOOK_IMAGE_PATH);
            mBookCover = savedInstanceState.getString(SAVED_BOOK_COVER_URL);

            createFiles();

            if (savedInstanceState.containsKey(SAVED_BOOK)) {
                book = savedInstanceState.getParcelable(SAVED_BOOK);
            } else {
                if (getArguments().containsKey(BUNDLE_BOOK_RESPONSE)) {
                    if (ConnectivityReceiver.getConnectivityStatus(context) != ConnectivityReceiver.TYPE_NOT_CONNECTED) {
                        getBook(mBookHref);
                    } else {
                        //TODO zmienić komunikat
                        showTextView(R.string.error_fetch_data_from_server);
                    }
                }
            }
        } else {
            if (getArguments() != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageCoverBook.setTransitionName(getArguments().getString(BUNDLE_TRANSITION_IMAGE));
                    tvBookTitle.setTransitionName(getArguments().getString(BUNDLE_TRANSITION_TITLE));
                    tvAuthorName.setTransitionName(getArguments().getString(BUNDLE_TRANSITION_AUTHOR));
                }

                if (getArguments().containsKey(BUNDLE_BOOK_RESPONSE)) {
                    bookResponse = getArguments().getParcelable(BUNDLE_BOOK_RESPONSE);
                    mAuthorName = bookResponse.getAuthor();
                    mBookTitle = bookResponse.getTitle();
                    mBookHref = bookResponse.getHref();
                    mBookCover = bookResponse.getCover();

                    mBookImagePath = context.getExternalFilesDir(null)
                            + File.separator + mBookTitle
                            + File.separator + mBookCover.replace("book/cover/", "");
                    mBookPdfPath = context.getExternalFilesDir(null)
                            + File.separator + mBookTitle
                            + File.separator + mBookTitle + ".pdf";

                    createFiles();
                    mBookCover = "https://wolnelektury.pl/media/" + mBookCover;

                    byteImage = getArguments().getByteArray(BUNDLE_IMAGE_COVER);
                    if (byteImage != null) {
                        imageCover = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
                    } else {
                        loadImageCover(mBookCover);
                    }
                    getBook(mBookHref);

                } else if (getArguments().containsKey(BUNDLE_BOOK_ID)) {
                    int id = getArguments().getInt(BUNDLE_BOOK_ID);
                    Cursor cursor = dbHelper.getBookFromDatabase(id);
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            mAuthorName = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR_NAME));
                            mBookTitle = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE));
                            mAuthorDescription = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR_DESCRIPTION));
                            mBookHtml = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_HTML));
                            mBookPdf = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PDF));
                            mBookCover = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_COVER));
                            mBookHref = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_HREF));
                            mBookImagePath = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_IMAGE_PATH));
                            mBookPdfPath = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PDF_PATH));

                            createFiles();

                            if (bitmapFile.exists()) {
                                imageCover = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, getString(R.string.error_fetch_data_from_database));
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                    }
                }
            }
        }

        mBookDirectoryPath = context.getExternalFilesDir(null) + File.separator + mBookTitle;
        activity.setTitle(mBookTitle);

        if (!mAuthorDescription.isEmpty()) {
            tvAuthorInfoDescription.setText(mAuthorDescription);
        }
        if (imageCover != null) {
            imageCoverBook.setImageBitmap(imageCover);
        } else {
            imageCoverBook.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.no_cover));
        }
        tvAuthorName.setText(mAuthorName);
        tvAuthorInfoName.setText(mAuthorName);
        tvBookTitle.setText(mBookTitle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_button_read_online:
                if (ConnectivityReceiver.getConnectivityStatus(context) != 0 && !mBookHtml.isEmpty() && mBookHtml != null) {
                    openWebPage(mBookHtml);
                } else {
                    Toast.makeText(context, R.string.error_can_not_read_online, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.image_button_read_pdf:
                if (pdfFile.exists()) {
                    openPdfFile(context, Uri.fromFile(pdfFile));
                } else {
                    createDirectory();
                    if (!saveBookPdfFile()) {
                        Toast.makeText(context, R.string.error_pdf_file_download, Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            case R.id.image_detail_book_cover:
                if (mBookCover != null) {
                    Intent intent = new Intent(activity, ImageViewActivity.class);

                    boolean isBitmap = false;
                    if (bitmapFile.exists()) {
                        isBitmap = true;
                        intent.putExtra(EXTRA_URL, mBookImagePath);
                    } else
                        intent.putExtra(EXTRA_URL, mBookCover);
                    intent.putExtra(EXTRA_IS_BITMAP, isBitmap);
                    intent.putExtra(EXTRA_TITLE, mBookTitle);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (dbHelper.isBookInDatabase(mBookTitle)) {
            menu.findItem(R.id.action_save).setIcon(R.drawable.ic_action_favourite);
        } else {
            menu.findItem(R.id.action_save).setIcon(R.drawable.ic_action_favourite_border);
        }
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (dbHelper.isBookInDatabase(mBookTitle)) {
                boolean isDeleted = dbHelper.deleteBookByTitle(mBookTitle);
                FilesUtil.deleteFiles(new File(mBookDirectoryPath));
                if (isDeleted) {
                    Toast.makeText(context, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            } else {
                if (book != null && author != null) {
                    createDirectory();
                    saveBookImage();
                    saveBookPdfFile();
                    long result = addBookToDatabase();
                    String message = "";
                    if (result > 0) {
                        message = getString(R.string.msg_saved);
                    } else if (result == -1) {
                        message = getString(R.string.error_add_to_database);
                        FilesUtil.deleteFiles(new File(mBookDirectoryPath));
                    } else if (result == -2) {
                        message = getString(R.string.msg_book_is_in_database);
                        FilesUtil.deleteFiles(new File(mBookDirectoryPath));
                    }
                    item.setIcon(R.drawable.ic_action_favourite);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, R.string.error_can_not_save_book, Toast.LENGTH_SHORT).show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (imageCover != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageCover.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            outState.putByteArray(BUNDLE_IMAGE_COVER, image);
        }
        outState.putString(SAVED_AUTHOR_NAME, mAuthorName);
        outState.putString(SAVED_BOOK_TITLE, mBookTitle);
        outState.putString(SAVED_AUTHOR_DESCRIPTION, mAuthorDescription);
        outState.putString(SAVED_BOOK_PDF, mBookPdf);
        outState.putString(SAVED_BOOK_HREF, mBookHref);
        outState.putString(SAVED_BOOK_HTML, mBookHtml);
        outState.putString(SAVED_BOOK_PDF_PATH, mBookPdfPath);
        outState.putString(SAVED_BOOK_IMAGE_PATH, mBookImagePath);
        outState.putString(SAVED_BOOK_COVER_URL, mBookCover);
        if (book != null)
            outState.putParcelable(SAVED_BOOK, book);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void createFiles() {
        bitmapFile = new File(mBookImagePath);
        pdfFile = new File(mBookPdfPath);
    }

    private void createDirectory() {
        File directoryFile = new File(mBookDirectoryPath);
        if (!directoryFile.exists())
            directoryFile.mkdirs();
    }

    private void saveBookImage() {
        if (!bitmapFile.exists())
            FilesUtil.saveBitmap(bitmapFile, imageCover);
    }

    private boolean saveBookPdfFile() {
        if (!pdfFile.exists() && mBookPdf != null) {
            downloadBookPdfFile(pdfFile, mBookPdf);
            return true;
        } else {
            return false;
        }
    }

    private void openPdfFile(Context context, Uri localUri) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(localUri, "application/pdf");
            context.startActivity(Intent.createChooser(i, getString(R.string.msg_choose_app)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(activity, ReaderActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, mBookTitle);
        startActivity(intent);
    }

    private long addBookToDatabase() {
        ContentValues cv = new ContentValues();
        cv.put(BookContract.BookEntry.COLUMN_BOOK_TITLE, mBookTitle);
        cv.put(BookContract.BookEntry.COLUMN_AUTHOR_NAME, mAuthorName);
        cv.put(BookContract.BookEntry.COLUMN_AUTHOR_DESCRIPTION, mAuthorDescription);
        cv.put(BookContract.BookEntry.COLUMN_BOOK_IMAGE_PATH, mBookImagePath);
        cv.put(BookContract.BookEntry.COLUMN_BOOK_PDF_PATH, mBookPdfPath);
        cv.put(BookContract.BookEntry.COLUMN_BOOK_HTML, mBookHtml);
        cv.put(BookContract.BookEntry.COLUMN_BOOK_COVER, mBookCover);
        cv.put(BookContract.BookEntry.COLUMN_BOOK_PDF, mBookPdf);
        cv.put(BookContract.BookEntry.COLUMN_BOOK_HREF, mBookHref);
        return dbHelper.saveBookToDatabase(cv);
    }

    private void loadImageCover(String url) {
        if (bookResponse != null) {
            try {
                Glide.with(context)
                        .load(url)
                        .asBitmap()
                        .override(600, 800)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                imageCover = resource;
                                imageCoverBook.setImageBitmap(imageCover);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadBookPdfFile(final File pdfFile, String url) {
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage(getString(R.string.msg_downloading_files));

        final Call<ResponseBody> call = apiService.downloadFileWithDynamicUrlSync(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean isBookPdfDownloaded = FilesUtil.writeResponseBodyToDisk(response.body(), pdfFile);
                    progress.dismiss();
                    Log.d(TAG, "file download was a success? " + isBookPdfDownloaded);
                } else {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
            }
        });

        progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progress.dismiss();
                        call.cancel();
                    }
                });
        progress.show();
    }

    private void getBook(String url) {
        showLoadingIndicator();
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<Book> call;
        call = apiService.getBook(url);
        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful()) {
                    book = response.body();
                    mBookHtml = book.getHtml();
                    mBookPdf = book.getPdf();
                    getAuthorInfo(apiService);
                } else {
                    showTextView(R.string.error_server_code);
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                showTextView(R.string.error_fetch_data_from_server);
            }
        });

    }

    private void getAuthorInfo(ApiInterface apiService) {
        String authorUrl;
        authorUrl = book.getAuthors().get(0).getHref();
        Call<Author> callAuthor;
        callAuthor = apiService.getAuthor(authorUrl);
        callAuthor.enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if (response.isSuccessful()) {
                    author = response.body();
                    if (Build.VERSION.SDK_INT >= 24) {
                        mAuthorDescription = String.valueOf(Html.fromHtml(author.getDescription(), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        mAuthorDescription = String.valueOf(Html.fromHtml(author.getDescription()));
                    }
                    tvAuthorInfoDescription.setText(mAuthorDescription);
                    showView();
                } else {
                    showTextView(R.string.error_server_code);
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {
                showTextView(R.string.error_fetch_data_from_server);
            }
        });
    }

    private void showView() {
        progressLoadingIndicator.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.INVISIBLE);
    }

    private void showLoadingIndicator() {
        linearLayout.setVisibility(View.INVISIBLE);
        progressLoadingIndicator.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.INVISIBLE);
    }

    private void showTextView(int id) {
        linearLayout.setVisibility(View.INVISIBLE);
        progressLoadingIndicator.setVisibility(View.INVISIBLE);
        tvNoResult.setVisibility(View.VISIBLE);
        tvNoResult.setText(id);
    }

}
