package com.mobileoid2.celltone.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
@Dao
public interface DaoRingtone {
    @Query("SELECT * FROM ringtone")
    List<RingtoneEntity> getAll();
    @Query("SELECT COUNT(*)  FROM ringtone where outgoing_media_id LIKE :mediaId OR incoming_media_id=:mediaId")
    int getMediaId(String mediaId);

    @Query("SELECT * FROM ringtone where phone_no LIKE :phoneNumber")
    RingtoneEntity getContatcByNumber(String phoneNumber);



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<RingtoneEntity> ringtoneEntity);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(RingtoneEntity ringtoneEntity);
    @Query("DELETE FROM ringtone")
    void delete();
    @Query("DELETE FROM ringtone where phone_no = :phoneNo")
    void delete(String phoneNo);
    @Update
    int update(RingtoneEntity music);


}
