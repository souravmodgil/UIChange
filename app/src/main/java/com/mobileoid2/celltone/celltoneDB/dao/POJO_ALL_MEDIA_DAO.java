package com.mobileoid2.celltone.celltoneDB.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileoid2.celltone.pojo.audio.Body;
import com.mobileoid2.celltone.pojo.getmedia.PojoGETMediaResponse;
import com.mobileoid2.celltone.pojo.otpverifiy.User;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface POJO_ALL_MEDIA_DAO {

    @Query("SELECT * FROM allMedia where contentType LIKE :isAudioOrVideo")
    List<Body> getAll_AUDIO(String isAudioOrVideo);





    /*@Query("SELECT * FROM allMedia where first_name LIKE  :firstName AND last_name LIKE :lastName")
    User findByName(String firstName, String lastName);*/

    @Query("SELECT COUNT(*) from allMedia")
    int countUsers();

    @Insert
    void insertAll(Body... bodies);

    @Delete
    void delete(Body body);

    @Insert(onConflict = REPLACE)
    Long[] insertList(List<Body> bodyList);

}
