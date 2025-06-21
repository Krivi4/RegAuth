    package ru.krivi4.regauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.krivi4.regauth.adapters.smsru.SmsRuClient;
import ru.krivi4.regauth.config.SmsProperties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Используем MockRestServiceServer для эмуляции
 * внешнего API SMS.ru без реальных HTTP-запросов.
 */
class SmsRuClientTest {

  private SmsRuClient smsRuClient;
  private MockRestServiceServer mockServer;

  private static final String OK_JSON = """
    {
      "status":"OK",
      "sms":{
        "79990000000":{"status":"OK","status_text":"OK"}
      }
    }
    """;

  private static final String FAIL_JSON = """
    {
      "status":"ERROR",
      "status_text":"something wrong"
    }
    """;

  /** Инициализирует RestTemplate, MockRestServiceServer и SmsRuClient перед каждым тестом. */
  @BeforeEach
  void setUp() {
   RestTemplate restTemplate = new RestTemplate();
   mockServer = MockRestServiceServer.createServer(restTemplate);

   smsRuClient = new SmsRuClient(restTemplate);

    ReflectionTestUtils.setField(smsRuClient, "smsBaseUrl", "https://sms.ru/sms/send");
    ReflectionTestUtils.setField(smsRuClient, "apiId", "dummy");
    ReflectionTestUtils.setField(smsRuClient, "from", "FROM");
  }

  /** При статусе ответа OK исключения не должны возникнуть. */
  @Test
  void sendRequest_shouldPassOnOk() {
    mockServer.expect(
      once(),
      requestTo(startsWith("https://sms.ru/sms/send"))
    ).andRespond(withSuccess(OK_JSON, MediaType.APPLICATION_JSON));

    smsRuClient.sendRequest("79990000000", "Тест");

    mockServer.verify();
  }

  /** Любой статус отличный от OK должен приводить к IllegalStateException. */
  @Test
  void sendRequest_shouldThrowOnErrorStatus() {
    mockServer.expect(
      once(),
      requestTo(startsWith("https://sms.ru/sms/send"))
    ).andRespond(withSuccess(FAIL_JSON, MediaType.APPLICATION_JSON));

    assertThatThrownBy(() ->
      smsRuClient.sendRequest("79990000000", "Тест"))
      .isInstanceOf(IllegalStateException.class);

    mockServer.verify();
  }
}
