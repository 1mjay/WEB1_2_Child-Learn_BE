package com.prgrms.ijuju.domain.stock.begin.service;

import com.prgrms.ijuju.domain.stock.begin.dto.request.BeginChatGptMessage;
import com.prgrms.ijuju.domain.stock.begin.dto.request.ChatGptRequest;
import com.prgrms.ijuju.domain.stock.begin.dto.response.BeginStockPriceResponse;
import com.prgrms.ijuju.domain.stock.begin.dto.response.ChatGptResponse;
import com.prgrms.ijuju.domain.stock.begin.entity.BeginQuiz;
import com.prgrms.ijuju.domain.stock.begin.repository.BeginQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BeginStockGptService {
    private final RestClient chatGptRestClient;
    private final BeginQuizRepository beginQuizRepository;
    private final BeginStockService beginStockService;

    private final String MODEL = "gpt-3.5-turbo";
    private final String SYSTEM_ROLE = "system";
    private final String USER_ROLE = "user";
    private final String SYSTEM_CONTENT = """
            너는 주어지는 한시간봉 단위 일주일의 주식 그래프를 확인하고, 초등학생 고학년 정도가 경제 지식을 기를 수 있는 정도의 OX 문제를 내주는 전문가야.
            문제와 보기는 명확하게 판단이 가능한 문제로 출제를 해주고 무엇보다 비문이 없어야하고, 교육이니까 100% 정답이어야 해. 너는 문제 출제 전문가야!!!
            주식 데이터값은 오늘을 기점으로 -3일부터 +3일까지의 총 7일에 대한 주식 가격 값이 주어질거야.
            문제를 작성한 후에는 반드시 주식 그래프를 참고하여 문제와 지문이 정확한지 확인해. 특히 숫자나 주식 가격이 정확히 일치하는지 반드시 체크해줘.
            문제의 난이도는 초등학생 수준에 맞춰 직관적으로 이해할 수 있는 문제여야 해. 용어를 최대한 단순하게 사용하고, 문제는 30~50자 정도로 작성해.
        """;
    private final double TEMPERATURE = 0.6; // 0일수록 일관되고 예측 가능, 1일수록 창의적이고 다양한 응답

    public void generateBeginQuiz() {
        List<BeginStockPriceResponse> graphData = beginStockService.getBeginStockData();

        String stockData = graphData.stream()
                .map(stock -> stock.tradeDay() + " : " + stock.price())
                .collect(Collectors.joining("\n"));

        String response = gptResponse(stockData);

        BeginQuiz quiz = parseGptResponse(response);
        beginQuizRepository.save(quiz);
    }

    private String gptResponse(String stockData) {
        List<BeginChatGptMessage> messages = List.of(
                new BeginChatGptMessage(SYSTEM_ROLE, SYSTEM_CONTENT),
                new BeginChatGptMessage(USER_ROLE, """
                        이 주식 데이터를 보고 O/X 퀴즈를 만들어줘. 정답은 'O'이거나 'X' 한 개만 가능해.
                        응답 형식은 1.{문제 내용}\n2.{'O'에 해당하는 지문}\n3.{'X'에 해당하는 지문}\n4.{정답(O,X)}
                        {}에 해당 내용을 담아서 보내줘. 물론 {} 표기도 없이!
                        문제는 30~50자 내외로 작성해. 반드시 지문에 숫자나 주식 가격이 정확히 일치하는지 확인하고, 비문이 없도록 신경 써줘.
                        """ + stockData
                )
        );

        ChatGptRequest request = new ChatGptRequest(MODEL, messages, TEMPERATURE);

        return chatGptRestClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(ChatGptResponse.class)
                .choices()
                .get(0)
                .message()
                .content();
    }

    private BeginQuiz parseGptResponse(String response) {
        try {
            String[] lines = response.split("\n");
            String content = lines[0].replaceFirst("^1\\.", "").trim();
            String oContent = lines[1].replaceFirst("^2\\.", "").trim();
            String xContent = lines[2].replaceFirst("^3\\.", "").trim();
            String answer = lines[3].replaceFirst("^4\\.", "").trim();

            return BeginQuiz.builder()
                    .content(content)
                    .oContent(oContent)
                    .xContent(xContent)
                    .answer(answer)
                    .build();
        } catch (Exception e) {
            log.error("GPT 응답 파싱 실패: {}", response, e);
            throw new IllegalArgumentException("GPT 응답 파싱 에러 발생: " + response, e);
        }
    }
}
