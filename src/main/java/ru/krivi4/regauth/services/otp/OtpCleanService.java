package ru.krivi4.regauth.services.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.repositories.OtpRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

/*** Плановое удаление просроченных Otp-кодов из БД (cron = каждый день в полночь).*/
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpCleanService {

  private final OtpRepository otpRepository;

  /**Запуск по расписанию очищает устаревшие Otp.*/
  @Transactional
  @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
  public void cleanExpiredOTP() {
    long removed = otpRepository.
      deleteByExpiresAtOTPBefore(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
    log.info("Очищенные {} аннулированные Otp токены с истекшим сроком действия", removed);
  }
}
