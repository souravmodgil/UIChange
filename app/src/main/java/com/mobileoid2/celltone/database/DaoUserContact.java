package com.mobileoid2.celltone.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface DaoUserContact {

    @Query("SELECT * FROM user_contact")
    List<ContactEntity> getAll();

    @Query("SELECT * FROM user_contact where phone_no LIKE :phoneNumber ORDER BY phoneName ASC")
    List<ContactEntity> getContatcByNumber(String phoneNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContactEntity> contactEntities);



    @Query("DELETE FROM user_contact")
    void delete();



    @Query("DELETE FROM user_contact where phone_no = :phoneNo")
    void delete(String phoneNo);


    @Update
    int update(ContactEntity music);

}
