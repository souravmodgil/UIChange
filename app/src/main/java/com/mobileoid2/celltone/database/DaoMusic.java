package com.mobileoid2.celltone.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.mobileoid2.celltone.pojo.Music;

import java.util.List;

/**
 * Created by mobileoid2 on 14/11/17.
 */
@Dao
public interface DaoMusic {

    @Query("SELECT * FROM music_database ORDER BY song_title ASC")
    List<Music> getAll();

    @Query("SELECT * FROM music_database where is_video LIKE :isVideo ORDER BY song_title ASC")
    List<Music> getAll(String isVideo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Music> musicList);

    @Query("DELETE FROM music_database")
    void delete();

    @Query("DELETE FROM music_database where _id = :id")
    void delete(String id);

    @Query("DELETE FROM music_database where is_video = :isVideo and _id != :id")
    void delete(String id, String isVideo);

    @Update
    int update(Music music);
}
