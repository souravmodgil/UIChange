package com.mobileoid2.celltone.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobileoid2 on 14/11/17.
 */
@Dao
public interface DaoContacts {

    @Query("SELECT * FROM phone_contacts ORDER BY phoneName ASC")
    List<BeanContacts> getAll();

    @Query("SELECT * FROM phone_contacts where phone_no LIKE :phoneNo LIMIT 1")
    BeanContacts findByName(String phoneNo);


    @Query("SELECT COUNT(*) from phone_contacts")
    int countPhoneContacts();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BeanContacts> contacts);


    @Query("DELETE FROM phone_contacts")
    void delete();

    @Query("DELETE FROM phone_contacts where _id = :id")
    void delete(String id);

    /*@Query("UPDATE phone_contacts SET music_path =:musicPath AND music_thumb =:musicThumb AND is_video =:isVideo WHERE phone_no =:phoneNo")  //music_thumb  is_video
    int update(String musicPath,String musicThumb,String isVideo,String phoneNo);
    */
    @Update
    int update(BeanContacts beanContacts);
}
