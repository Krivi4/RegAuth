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
  private static final String DAILY_CLEANUP_CRON = "0 0 0 * * *";
  private static final String MOSCOW_ZONE        = "Europe/Moscow";


  /**Запуск по расписанию очищает устаревшие Otp.*/
  @Transactional
  @Scheduled(cron = DAILY_CLEANUP_CRON, zone = MOSCOW_ZONE)
  public void cleanExpiredOTP() {
    long removed = otpRepository.
      deleteByExpiresAtOTPBefore(LocalDateTime.now(ZoneId.of(MOSCOW_ZONE)));
    log.info("Очищенные {} аннулированные Otp токены с истекшим сроком действия", removed);
  }
}
