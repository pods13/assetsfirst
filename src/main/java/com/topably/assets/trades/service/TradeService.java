package com.topably.assets.trades.service;

import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.exception.FileUploadException;
import com.topably.assets.trades.repository.TradeViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeViewRepository tradeViewRepository;

    @Value("${app.upload.trades.path}")
    private String uploadTradesPath;

    @PostConstruct
    public void createMissingUploadDirectories() throws IOException {
        Files.createDirectories(Paths.get(uploadTradesPath));
    }

    @Transactional
    public void saveExportedTradesFile(MultipartFile file) {
        try {
            Path root = Paths.get(uploadTradesPath);
            Path resolve = root.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            if (resolve.toFile().exists()) {
                throw new FileUploadException("File already exists: " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), resolve);
        } catch (Exception e) {
            throw new FileUploadException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Collection<TradeView> getUserTrades(String username) {
        return tradeViewRepository.findByUsername(username);
    }
}
