package com.derus.wolnelektury.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.derus.wolnelektury.db.BookContract.BookEntry;

import static com.derus.wolnelektury.db.BookContract.BookEntry.TABLE_NAME;

/**
 * Created by Michal on 14.07.2017.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    //TODO utworzyć nową tabele "autor"
    // The database name
    private static final String DATABASE_NAME = "lektury.db";
    private static final int DATABASE_VERSION = 1;
    private static BookDbHelper bookDbHelper;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, " +
                BookEntry.COLUMN_AUTHOR_NAME + " TEXT NOT NULL, " +
                BookEntry.COLUMN_AUTHOR_DESCRIPTION + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_HTML + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_PDF + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_COVER + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_HREF + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_IMAGE_PATH + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_PDF_PATH + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public static synchronized BookDbHelper getInstance(Context context) {
        if (bookDbHelper == null) {
            bookDbHelper = new BookDbHelper(context.getApplicationContext());
        }
        return bookDbHelper;
    }

    public Cursor getAllBooksFromDatabase() {

        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getBookFromDatabase(int id) {
        String selection = BookContract.BookEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public long saveBookToDatabase(ContentValues cv) {
        if (!isBookInDatabase(cv.getAsString(BookEntry.COLUMN_BOOK_TITLE))) {
            SQLiteDatabase db = getWritableDatabase();
            return db.insert(TABLE_NAME, null, cv);
        }else{
            return -2;
        }
    }

    public boolean isBookInDatabase(String bookTitle) {

        String[] columns = {BookContract.BookEntry.COLUMN_BOOK_TITLE};
        String selection = BookContract.BookEntry.COLUMN_BOOK_TITLE + " =?";
        String[] selectionArgs = { bookTitle };
        String limit = "1";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean deleteAllBooksFromDatabase(){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    public boolean deleteBookById(int id){
        String whereClause = BookEntry._ID + " = ?";
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, whereClause, new String[] {Integer.toString(id)}) > 0;
    }

    public boolean deleteBookByTitle(String title){
        String whereClause = BookEntry.COLUMN_BOOK_TITLE + " = ?";
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, whereClause, new String[] {title}) > 0;
    }
}
