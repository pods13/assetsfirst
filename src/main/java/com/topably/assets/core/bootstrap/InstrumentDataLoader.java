package com.topably.assets.core.bootstrap;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.ImportInstrumentDto;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.repository.instrument.ETFRepository;
import com.topably.assets.instruments.repository.instrument.FXRepository;
import com.topably.assets.instruments.service.importer.DefaultInstrumentImporter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.NYSE;
import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.NYSEARCA;


@RequiredArgsConstructor
@Component
@Order(25)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class InstrumentDataLoader implements CommandLineRunner {

    private final DefaultInstrumentImporter importer;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addEtfInstruments();
        addStockInstruments();
        addFXInstruments();
    }

    private void addEtfInstruments() {
        importer.importInstrument(new ImportInstrumentDto()
                .setName("KraneShares Global Carbon Strategy ETF")
                .setType(InstrumentType.ETF)
                .setIdentifier(new Ticker("KRBN", NYSEARCA.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setName("FinEx China UCITS ETF")
                .setType(InstrumentType.ETF)
                .setIdentifier(new Ticker("FXCN", ExchangeEnum.MCX.name())));
    }

    private void addStockInstruments() {
        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Newmont Goldcorp Corp")
                .setIdentifier(new Ticker("NEM", NYSE.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Bayer AG NA")
                .setIdentifier(new Ticker("BAYN", ExchangeEnum.XETRA.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Altria Group")
                .setIdentifier(new Ticker("MO", NYSE.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Omega Healthcare Investors, Inc")
                .setIdentifier(new Ticker("OHI", NYSE.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("The Coca-Cola Company")
                .setIdentifier(new Ticker("KO", NYSE.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("TotalEnergies SE")
                .setIdentifier(new Ticker("TTE", NYSE.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("PJSC Rosneft Oil Company")
                .setIdentifier(new Ticker("ROSN", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("PJSC Gazprom")
                .setIdentifier(new Ticker("GAZP", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Ros Agro PLC")
                .setIdentifier(new Ticker("AGRO", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("PJSC Polyus")
                .setIdentifier(new Ticker("PLZL", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("PhosAgro")
                .setIdentifier(new Ticker("PHOR", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Public Joint Stock Company Inter RAO UES")
                .setIdentifier(new Ticker("IRAO", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Mobile TeleSystems Public Joint Stock Company")
                .setIdentifier(new Ticker("MTSS", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Public Joint Stock Company Magnit")
                .setIdentifier(new Ticker("MGNT", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Public Joint-Stock Company Moscow Exchange MICEX-RTS")
                .setIdentifier(new Ticker("MOEX", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("Sberbank of Russia")
                .setIdentifier(new Ticker("SBERP", ExchangeEnum.MCX.name())));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.STOCK)
                .setName("China National Offshore Oil Corporatio")
                .setIdentifier(new Ticker("0883", ExchangeEnum.HK.name())));
    }

    private void addFXInstruments() {
        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.FX)
                .setName("Currency USD.RUB")
                .setIdentifier(new Ticker("USD.RUB", ExchangeEnum.FX_IDC.name()))
                .setCurrency(Currency.getInstance("USD")));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.FX)
                .setName("Currency EUR.RUB")
                .setIdentifier(new Ticker("EUR.RUB", ExchangeEnum.FX_IDC.name()))
                .setCurrency(Currency.getInstance("EUR")));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.FX)
                .setName("Currency CNY.RUB")
                .setIdentifier(new Ticker("CNY.RUB", ExchangeEnum.FX_IDC.name()))
                .setCurrency(Currency.getInstance("CNY")));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.FX)
                .setName("Currency HKD.RUB")
                .setIdentifier(new Ticker("HKD.RUB", ExchangeEnum.FX_IDC.name()))
                .setCurrency(Currency.getInstance("HKD")));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.FX)
                .setName("Золото")
                .setIdentifier(new Ticker("GLDRUB_TOM", ExchangeEnum.MCX.name()))
                .setCurrency(Currency.getInstance("RUB")));

        importer.importInstrument(new ImportInstrumentDto()
                .setType(InstrumentType.FX)
                .setName("Валюта RUB.USD")
                .setIdentifier(new Ticker("RUB.USD", ExchangeEnum.FX_IDC.name()))
                .setCurrency(Currency.getInstance("RUB")));
    }

}
