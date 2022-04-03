package com.topably.assets.trades.brokerage.vtb;

import com.topably.assets.trades.domain.dto.TradeImportDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.mapping.PassThroughRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class TradesImportJobConfig {

    @Bean
    public Job tradesImportJob(JobBuilderFactory jobBuilderFactory, Step tradesImportStep) {
        return jobBuilderFactory.get("tradesImportJob")
                .incrementer(new RunIdIncrementer())
                .flow(tradesImportStep)
                .end().build();
    }

    @Bean
    public Step tradesImportStep(StepBuilderFactory stepBuilderFactory, PoiItemReader<String[]> excelReader,
                                 ItemProcessor<String[], TradeImportDto> tradeFilterProcessor, ItemWriter<TradeImportDto> excelWriter) {
        return stepBuilderFactory.get("tradesImportStep")
                .<String[], TradeImportDto>chunk(1)
                .reader(excelReader)
                .processor(tradeFilterProcessor)
                .chunk(10)
                .writer(excelWriter)
                .build();
    }

    @Bean
    @JobScope
    public PoiItemReader<String[]> excelReader(@Value("${app.upload.trades.path}") String uploadTradesPath,
                                               @Value("#{jobParameters['filename']}") String filename) {
        PoiItemReader<String[]> reader = new PoiItemReader<>();
        reader.setRowMapper(passThroughRowMapper());
        var filePath = uploadTradesPath + File.separatorChar + filename;
        reader.setResource(new FileSystemResource(filePath));
        return reader;
    }

    @Bean
    public RowMapper<String[]> passThroughRowMapper() {
        return new PassThroughRowMapper();
    }

    @Bean
    @JobScope
    ItemProcessor<String[], TradeImportDto> tradeFilterProcessor() {
        return new TradeFilterProcessor();
    }

    @Bean
    @JobScope
    ItemWriter<TradeImportDto> excelWriter() {
        return new TradeWriter();
    }
}
