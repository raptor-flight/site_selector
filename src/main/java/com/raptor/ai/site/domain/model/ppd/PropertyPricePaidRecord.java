package com.raptor.ai.site.domain.model.ppd;

import com.raptor.ai.site.domain.model.common.Address;
import com.raptor.ai.site.domain.model.common.Tenure;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

public class PropertyPricePaidRecord {
    /** --- members --- */
    private String price;
    private LocalDate dateOfTransfer;
    private Address address;
    private String tenure;
    private String transactionId;
    private String propertyType;


    public PropertyPricePaidRecord() {
        super();
    }


    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDate getDateOfTransfer() {
        return dateOfTransfer;
    }

    public void setDateOfTransfer(LocalDate dateOfTransfer) {
        this.dateOfTransfer = dateOfTransfer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PropertyPricePaidRecord.class.getSimpleName() + "[", "]")
                .add("price='" + price + "'")
                .add("dateOfTransfer=" + dateOfTransfer)
                .add("address=" + address)
                .add("tenure='" + tenure + "'")
                .add("transactionId='" + transactionId + "'")
                .toString();
    }
}
