package com.derus.wolnelektury.db;

/**
 * Created by Michal on 14.07.2017.
 */

import android.provider.BaseColumns;

public class BookContract {

    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_BOOK_TITLE = "title";
        public static final String COLUMN_AUTHOR_NAME = "author";
        public static final String COLUMN_AUTHOR_DESCRIPTION = "authorDescription";
        public static final String COLUMN_BOOK_PDF = "pdf";
        public static final String COLUMN_BOOK_HTML = "html";
        public static final String COLUMN_BOOK_COVER = "cover";
        public static final String COLUMN_BOOK_HREF = "href";
        public static final String COLUMN_BOOK_IMAGE_PATH = "imagePath";
        public static final String COLUMN_BOOK_PDF_PATH = "pdfPath";
    }

}