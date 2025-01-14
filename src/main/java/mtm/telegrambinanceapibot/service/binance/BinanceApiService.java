package mtm.telegrambinanceapibot.service.binance;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import mtm.telegrambinanceapibot.dto.CurrencyResponseDataDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinanceApiService {
    private static final String BASE_URL = "https://api.binance.com/api/v3/"
            + "ticker/price?symbol=%s";
    private static final String BASE_FUTURES_URL = "https://fapi.binance.com/fapi/v1/"
            + "ticker/price?symbol=%s";

    private final ObjectMapper objectMapper;

    public CurrencyResponseDataDto getCurrency(String pair) throws IOException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = BASE_URL.formatted(pair);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            CurrencyResponseDataDto dataDto =
                    objectMapper.readValue(response.body(), CurrencyResponseDataDto.class);
            return dataDto;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CurrencyResponseDataDto getFuturesCurrency(String pair) throws IOException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String futuresUrl = BASE_FUTURES_URL.formatted(pair);

        HttpRequest httpRequestFutures = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(futuresUrl))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(httpRequestFutures, HttpResponse.BodyHandlers.ofString());
            CurrencyResponseDataDto dataDto =
                    objectMapper.readValue(response.body(), CurrencyResponseDataDto.class);
            return dataDto;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
