package com.topably.assets.trades;

import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.UploadResponseMessage;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradesController {

    private final TradeService tradeService;

    public ResponseEntity<UploadResponseMessage> uploadTradesFile(@RequestParam("file") MultipartFile file) {
        tradeService.saveExportedTradesFile(file);
        return ResponseEntity.ok(new UploadResponseMessage("The file successfully uploaded: " + file.getOriginalFilename()));
    }

    @GetMapping("")
    public Collection<TradeView> getUserTrades(Principal principal) {
        return tradeService.getUserTrades(principal.getName());
    }
}
