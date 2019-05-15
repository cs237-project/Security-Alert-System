package com.securityalertsystem.Service;

import com.securityalertsystem.entity.AlertMessage;

public interface AlertSender {
    void send1(AlertMessage message);

    void send2(AlertMessage message);

    void send3(AlertMessage message);
}
