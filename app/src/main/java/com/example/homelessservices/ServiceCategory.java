package com.example.homelessservices;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ServiceCategory extends Fragment implements View.OnClickListener{

    private TextView live,clothes_blanket,counselling,drug_alcohol,health_pharmacy,employment,hospital,
            legal_finance,phone,website,needle,shower;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_category, container, false);
        getActivity().setTitle("Other Services");

        live = (TextView) view.findViewById(R.id.tv_live);
        clothes_blanket = (TextView) view.findViewById(R.id.tv_cloth_blanket);
        counselling = (TextView) view.findViewById(R.id.tv_counsel_psychiatric);
        drug_alcohol = (TextView) view.findViewById(R.id.tv_drug_alcohol);
        health_pharmacy = (TextView) view.findViewById(R.id.tv_health_pharmacy);
        employment = (TextView) view.findViewById(R.id.tv_employment);
        hospital = (TextView) view.findViewById(R.id.tv_hospital_emergency);
        legal_finance = (TextView) view.findViewById(R.id.tv_legal_financial);
        phone = (TextView) view.findViewById(R.id.tv_help_phone);
        website = (TextView) view.findViewById(R.id.tv_help_website);
        needle = (TextView) view.findViewById(R.id.tv_needle_exchange);
        shower = (TextView) view.findViewById(R.id.tv_shower_laundry);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null)
        {
            live.setOnClickListener(this);
            clothes_blanket.setOnClickListener(this);
            counselling.setOnClickListener(this);
            drug_alcohol.setOnClickListener(this);
            health_pharmacy.setOnClickListener(this);
            employment.setOnClickListener(this);
            hospital.setOnClickListener(this);
            legal_finance.setOnClickListener(this);
            phone.setOnClickListener(this);
            website.setOnClickListener(this);
            needle.setOnClickListener(this);
            shower.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_live:
                goToResultPage("Accommodation",true);
                break;
            case R.id.tv_cloth_blanket:
                goToResultPage("Clothes and Blankets",true);
                break;
            case R.id.tv_counsel_psychiatric:
                goToResultPage("Counselling and Psychiatric Services",true);
                break;
            case R.id.tv_drug_alcohol:
                goToResultPage("Drug and Alcohol",true);
                break;
            case R.id.tv_health_pharmacy:
                goToResultPage("Health Services / Pharmacy",true);
                break;
            case R.id.tv_employment:
                goToResultPage("Employment Assistance",true);
                break;
            case R.id.tv_hospital_emergency:
                goToResultPage("Hospitals / Emergency",true);
                break;
            case R.id.tv_legal_financial:
                goToResultPage("Legal / Financial Advice",true);
                break;
            case R.id.tv_help_phone:
                goToResultPage("Helpful phone number",false);
                break;
            case R.id.tv_help_website:
                goToResultPage("Helpful website",false);
                break;
            case R.id.tv_needle_exchange:
                goToResultPage("Needle Exchange",true);
                break;
            case R.id.tv_shower_laundry:
                goToResultPage("Showers / Laundry",true);
                break;
        }
    }

    private void goToResultPage(String category,boolean flag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ServiceCategoryResult serviceCategoryResult = new ServiceCategoryResult();
        serviceCategoryResult.sendNumberToResultFragment(category,flag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content, serviceCategoryResult);
        ft.addToBackStack(null);
        ft.commit();
    }
}
