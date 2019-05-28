package com.securityalertsystem.Service;

import com.securityalertsystem.entity.AlertMessage;

public interface AlertSender {
    void send1(AlertMessage message);

    void send1(String message);

    void send2(AlertMessage message);

    void send2(String message);

    void send3(AlertMessage message);

    void send3(String message);
}
