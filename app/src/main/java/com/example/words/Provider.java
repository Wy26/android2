package com.example.words;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Provider extends ContentProvider {
    private static final UriMatcher MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
    WordsDBHelper myHelper;
    UriMatcher matcher;
    private static final int WORDS =0;
    private static final int WORDS_ID=1;

    @Override
    public boolean onCreate(){
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authorities = "com.example.provider";
        matcher.addURI(authorities,"words",0);
        matcher.addURI(authorities,"words/#",1);
        this.myHelper=new WordsDBHelper(this.getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String  selection, String[] selectionArgs){
        SQLiteDatabase db=myHelper.getWritableDatabase();
        int count=0;
        switch (MATCHER.match(uri)){
            case WORDS:
                count=db.delete("words", selection, selectionArgs);
                return count;

            case WORDS_ID:
                long id= ContentUris.parseId(uri);
                String where="wordsid="+id;
                if(selection!=null&&!"".equals(selection)){
                    where=selection+"and"+where;
                }
                count=db.delete("words", where, selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db=myHelper.getWritableDatabase();
        switch(MATCHER.match(uri)){
            case WORDS:
                Long rovid=db.insert("words","id", values);
                Uri insertUri=ContentUris.withAppendedId(uri, rovid);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return insertUri;
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db =myHelper.getWritableDatabase();
        switch (matcher.match(uri)) {
            case WORDS_ID:
                long id = ContentUris.parseId(uri);
                selection = selection == null ? "id=" + id : selection + "AND id=" + id;
            case WORDS:
                return db.update("words", values, selection, selectionArgs);
            default:
                throw new RuntimeException("Uri cannot be identified");
        }
    }
//查找
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db=myHelper.getWritableDatabase();
        switch(MATCHER.match(uri)){
            case WORDS:
                return db.query("words", projection, selection, selectionArgs, null, null, sortOrder);


            case WORDS_ID:
                long id=ContentUris.parseId(uri);
                selection =selection==null?"id="+id:selection+"AND id="+id;
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }

}
