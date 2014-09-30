package com.kickstartlab.android.jayonpickup;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by awidarto on 12/15/13.
 */
public class Merchant extends SugarRecord<Merchant>{
    public Long _id;
    public Long   merchant_id;
    public String district;
    public String province;
    public String city;
    public String country;
    public String zip;
    public String phone;
    public String mobile;
    public String mobile1;
    public String mobile2;
    public String merchantname;
    public String mc_email;
    public String mc_street;
    public String mc_district;
    public String mc_city;
    public String mc_province;
    public String mc_country;
    public String mc_zip;
    public String mc_phone;
    public String mc_mobile;
    public String street;

    public Merchant(Context ctx ){
        super(ctx);
    }

    public Merchant(
            Long   merchant_id,
            String district,
            String province,
            String city,
            String country,
            String zip,
            String phone,
            String mobile,
            String mobile1,
            String mobile2,
            String merchantname,
            String mc_email,
            String mc_street,
            String mc_district,
            String mc_city,
            String mc_province,
            String mc_country,
            String mc_zip,
            String mc_phone,
            String mc_mobile,
            String street

    ){
        super();
        this.id = merchant_id;
        this.merchant_id = merchant_id;
        this.district    = district;
        this.province    = province;
        this.city        = city;
        this.country     = country;
        this.zip         = zip;
        this.phone       = phone;
        this.mobile      = mobile;
        this.mobile1     = mobile1;
        this.mobile2     = mobile2;
        this.merchantname= merchantname;
        this.mc_email    = mc_email;
        this.mc_street   = mc_street;
        this.mc_district = mc_district;
        this.mc_city     = mc_city;
        this.mc_province = mc_province;
        this.mc_country  = mc_country;
        this.mc_zip      = mc_zip;
        this.mc_phone    = mc_phone;
        this.mc_mobile   = mc_mobile;
        this.street      = street;

    }

    @Override
    public String toString() {
        return  merchantname;

    }

    public Long getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Long merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getMerchantname() {
        return merchantname;
    }

    public void setMerchantname(String merchantname) {
        this.merchantname = merchantname;
    }

    public String getMc_email() {
        return mc_email;
    }

    public void setMc_email(String mc_email) {
        this.mc_email = mc_email;
    }

    public String getMc_street() {
        return mc_street;
    }

    public void setMc_street(String mc_street) {
        this.mc_street = mc_street;
    }

    public String getMc_district() {
        return mc_district;
    }

    public void setMc_district(String mc_district) {
        this.mc_district = mc_district;
    }

    public String getMc_city() {
        return mc_city;
    }

    public void setMc_city(String mc_city) {
        this.mc_city = mc_city;
    }

    public String getMc_province() {
        return mc_province;
    }

    public void setMc_province(String mc_province) {
        this.mc_province = mc_province;
    }

    public String getMc_country() {
        return mc_country;
    }

    public void setMc_country(String mc_country) {
        this.mc_country = mc_country;
    }

    public String getMc_zip() {
        return mc_zip;
    }

    public void setMc_zip(String mc_zip) {
        this.mc_zip = mc_zip;
    }

    public String getMc_phone() {
        return mc_phone;
    }

    public void setMc_phone(String mc_phone) {
        this.mc_phone = mc_phone;
    }

    public String getMc_mobile() {
        return mc_mobile;
    }

    public void setMc_mobile(String mc_mobile) {
        this.mc_mobile = mc_mobile;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
