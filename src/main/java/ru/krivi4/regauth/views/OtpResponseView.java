package ru.krivi4.regauth.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Ответ при отправке Otp.
 */
@Getter
@Setter
@AllArgsConstructor
public class OtpResponseView {
    private String otpId;
    private String OtpToken;
}
