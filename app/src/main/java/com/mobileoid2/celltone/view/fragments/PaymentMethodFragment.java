package  com.mobileoid2.celltone.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.view.activity.OfferActivity;

public class PaymentMethodFragment extends Fragment {
    //public final TextView mIdView;
    private   TextView txtPlanName, txtPlanPrice, txtValidity, txtMediaCount, txtContactsCount, txtOwnMediaUpload, txtPlanFinalPrice,
            txtOfferName,txtOfferDescription,txtDiscountPrice,txtPlanRate;
    private LinearLayout llOffer,llOfferDescription,llDiscount,llPlanPrice;
    private String planName , mediaCount, contactMedia, mediaOwnCount, validity;
    private int planRate;


    public static PaymentMethodFragment newInstance(String planName , int planRate, String mediaCount, String contactMedia,
                                                    String mediaOwnCount, String validity) {
        PaymentMethodFragment fragment = new PaymentMethodFragment();
        fragment.planName =planName;
        fragment.planRate =planRate;
        fragment.mediaCount =mediaCount;
        fragment.contactMedia =contactMedia;
        fragment.mediaOwnCount =mediaOwnCount;
        fragment.validity =validity;
        return fragment;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_payment_method, container, false);
        txtPlanName = view.findViewById(R.id.txt_plan_name);
        txtPlanPrice = view.findViewById(R.id.txt_plan_price);
        txtValidity = view.findViewById(R.id.txt_validity);
        txtMediaCount = view.findViewById(R.id.txt_media_count);
        txtContactsCount = view.findViewById(R.id.txt_contacts_count);
        txtOwnMediaUpload = view.findViewById(R.id.txt_own_media_upload);
        txtPlanFinalPrice =view.findViewById(R.id.txt_plan_final_price);
        txtDiscountPrice =view.findViewById(R.id.txt_discount_price);
        llOfferDescription =view.findViewById(R.id.ll_offer_description);
        txtOfferDescription =view.findViewById(R.id.txt_offer_description);
        txtOfferName = view.findViewById(R.id.txt_offer_name);
        txtPlanRate  =view.findViewById(R.id.txt_plan_rate);
        llOffer= view.findViewById(R.id.ll_offer);
        llDiscount =view.findViewById(R.id.ll_discount);
        llPlanPrice =view.findViewById(R.id.ll_plan_price);
        txtPlanName.setText(planName);
        txtPlanPrice.setText("$"+planRate);
        txtOwnMediaUpload.setText(mediaOwnCount+" Upload own media");
        txtMediaCount.setText(mediaCount+" media");
        txtValidity.setText(validity);
        txtContactsCount.setText(contactMedia + " Contacts");
        txtPlanFinalPrice.setText("$"+planRate);
        llOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OfferActivity.class);
                startActivityForResult(intent, 1);


            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 1) {
            String offerName = data.getStringExtra("offer_name");
            String offerDescription = data.getStringExtra("offer_description");
            int discount = data.getIntExtra("discount_price",0);
            if(discount>0)
            {
                llOfferDescription.setVisibility(View.VISIBLE);
                llOffer.setVisibility(View.GONE);
                txtOfferDescription.setText(offerDescription);
                txtOfferName.setText(offerName);
                llPlanPrice.setVisibility(View.VISIBLE);
                llDiscount.setVisibility(View.VISIBLE);
                int finalRate =(discount*planRate)/100;
                txtDiscountPrice.setText("$ "+finalRate);
                finalRate = planRate-finalRate;
                txtPlanFinalPrice.setText("$ "+finalRate);
                txtPlanRate.setText("$ "+planRate);
            }
            else
            {
                llOfferDescription.setVisibility(View.GONE);
                llOffer.setVisibility(View.VISIBLE);
                llPlanPrice.setVisibility(View.GONE);
                llDiscount.setVisibility(View.GONE);
            }

        }
    }
}
