/*
package com.mobileoid2.celltone.celltoneDB.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileoid2.celltone.pojo.getmedia.PojoGETMediaResponse;
import com.mobileoid2.celltone.pojo.otpverifiy.User;

import java.util.List;

@Dao
public interface GET_MEDIA_DAO {

    @Query("SELECT * FROM getmedia")
    List<PojoGETMediaResponse> getAll();

 */
/*   @Query("SELECT * FROM getmedia "*//*
*/
/*where first_name LIKE  :firstName AND last_name LIKE :lastName"*//*
*/
/*)
    PojoGETMediaResponse findByName();*//*


    @Query("SELECT COUNT(*) from getmedia")
    int countUsers();

    @Insert
    void insertAll(PojoGETMediaResponse... users);

    @Delete
    void delete(PojoGETMediaResponse user);


}
*/
