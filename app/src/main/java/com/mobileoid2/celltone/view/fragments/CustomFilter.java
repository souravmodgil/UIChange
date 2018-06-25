package com.mobileoid2.celltone.view.fragments;

import android.widget.Filter;

import com.mobileoid2.celltone.pojo.ContactMedia;
import com.mobileoid2.celltone.view.adapter.MyContactsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomFilter extends Filter {

    MyContactsRecyclerViewAdapter adapter;
    List<ContactMedia> filterList;

    public CustomFilter(List<ContactMedia> filterList,MyContactsRecyclerViewAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            List<ContactMedia> contactFilterList=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getName().toUpperCase().contains(constraint)
                        || filterList.get(i).getMobileNo().contains(constraint) )
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    contactFilterList.add(filterList.get(i));
                }
            }

            results.count=contactFilterList.size();
            results.values=contactFilterList;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

//        adapter.mlistFiltered= (ArrayList<ContactMedia>) results.values;
//        //REFRESH
//        adapter.notifyDataSetChanged();
    }
}