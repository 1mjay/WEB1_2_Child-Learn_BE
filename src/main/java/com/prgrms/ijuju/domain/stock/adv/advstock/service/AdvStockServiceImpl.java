package com.prgrms.ijuju.domain.stock.adv.advstock.service;

import com.prgrms.ijuju.domain.stock.adv.advstock.constant.DataType;
import com.prgrms.ijuju.domain.stock.adv.advstock.dto.PolygonCandleResponse;
import com.prgrms.ijuju.domain.stock.adv.advstock.dto.PolygonCandleResult;
import com.prgrms.ijuju.domain.stock.adv.advstock.entity.AdvStock;
import com.prgrms.ijuju.domain.stock.adv.advstock.repository.AdvStockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service 기능 중 saveStockData 를 제외한 나머지 두 기능은 실제 서비스 중 쓰이지 않습니다! 테스트 용도입니다.
 * Stock 에서는 Controller 조차 테스트 용도로만 쓰이는 데이터를 받고 저장하는 기능만을 제공합니다.
 */
@Service
public class AdvStockServiceImpl implements AdvStockService {

    private final AdvStockRepository advStockRepository;

    public AdvStockServiceImpl(AdvStockRepository advStockRepository) {
        this.advStockRepository = advStockRepository;
    }


    //레퍼런스 데이터를 받아옵니다. 레퍼런스 데이터는 1주일치 데이터이며, 한번에 업데이트 되기 때문에 builder 없이 들고옵니다.
    //지금 뇌속에서는 작동할거 같은데 테스트 해보고 안되면 builder 이용해서 가져오겠습니다. 근데 데이터 변형이 전혀 필요 없을거 같아서 될듯?
    @Override
    public List<AdvStock> getReferenceData() {
        return advStockRepository.findByDataType(DataType.REFERENCE);
    }


    //라이브 데이터를 들고 옵니다. 일단 간단하게 작성되었으며, 특정 시간과 심볼을 통해 해당 수치를 불러옵니다.
    //hour 은 실제 시간을 뜻합니다. 즉 10 을 입력시 오전 10시를 뜻합니다. 이 경우 10시의 해당 주식 데이터를 불러오게 됩니다
    //UTC 기준입니다!
    @Override
    public AdvStock getLiveData(String symbol, int hour) {

        if (hour < 14 || hour > 21) {
            throw new IllegalArgumentException(("유효하지 않은 거래 시간. NYSE 거래 시간을 따라가기에 오후 2시 ~ 오후 9시 기준으로 잡혀야 합니다"));
        }
        AdvStock liveData = advStockRepository.findBySymbolAndDataType(symbol, DataType.LIVE)
                .orElseThrow(() -> new IllegalArgumentException("해당 심볼의 라이브 데이터를 찾을 수 없습니다 : " + symbol));

        int index = hour - 14; // 9시 ~ 15시 기준 >> 해외 기준이라 시간 변동 가능성 있음.
        return AdvStock.builder()
                .symbol(liveData.getSymbol())
                .name(liveData.getName())
                .openPrices(List.of(liveData.getOpenPrices().get(index)))
                .highPrices(List.of(liveData.getHighPrices().get(index)))
                .lowPrices(List.of(liveData.getLowPrices().get(index)))
                .closePrices(List.of(liveData.getClosePrices().get(index)))
                .volumes(List.of(liveData.getVolumes().get(index)))
                .timestamps(List.of(liveData.getTimestamps().get(index)))
                .dataType(DataType.LIVE)
                .build();
    }

    @Override
    public AdvStock saveStockData(String symbol, String name, PolygonCandleResponse response, DataType dataType) {

        if (response.getResults() == null || response.getResults().isEmpty()) {
            throw new IllegalArgumentException("해당 주식에 어떠한 값도 없습니다: " + symbol);
        }

        List<Double> openPrices = response.getResults().stream().map(PolygonCandleResult::getO).toList();
        List<Double> highPrices = response.getResults().stream().map(PolygonCandleResult::getH).toList();
        List<Double> lowPrices = response.getResults().stream().map(PolygonCandleResult::getL).toList();
        List<Double> closePrices = response.getResults().stream().map(PolygonCandleResult::getC).toList();
        List<Long> volumes = response.getResults().stream().map(PolygonCandleResult::getV).toList();
        List<Long> timestamps = response.getResults().stream().map(PolygonCandleResult::getT).toList();

        int size = response.getResults().size();
        if (openPrices.size() != size || highPrices.size() != size || lowPrices.size() != size ||
                closePrices.size() != size || volumes.size() != size || timestamps.size() != size) {
            throw new IllegalStateException("해당 심볼의 값들이 mismatch 났습니다. > 모든 수치가 전달되지 않았습니다: " + symbol);
        }

        AdvStock advStock = AdvStock.builder()
                .symbol(symbol)
                .name(name)
                .openPrices(openPrices)
                .highPrices(highPrices)
                .lowPrices(lowPrices)
                .closePrices(closePrices)
                .volumes(volumes)
                .timestamps(timestamps)
                .dataType(dataType)
                .build();

        try {
            return advStockRepository.save(advStock);
        } catch (Exception e) {
            throw new RuntimeException("저장 실패: " + symbol, e);
        }
    }

    @Override
    public void deleteByDataType(DataType dataType) {
        advStockRepository.deleteByDataType(dataType);
    }
}
