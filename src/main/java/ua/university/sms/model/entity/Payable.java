package ua.university.sms.model.entity;

public interface Payable {
    boolean isPaid();
    void setPaid(boolean paid);
}
