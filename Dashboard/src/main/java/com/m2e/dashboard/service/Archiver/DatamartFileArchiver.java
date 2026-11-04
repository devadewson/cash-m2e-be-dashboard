package com.m2e.dashboard.service.Archiver;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DatamartFileArchiver {

    private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${m2e.sftp.backup-directory:}")
    private String backupDirectory;

    /**
     * @param localFilePath file hasil download hari ini
     * @param processed     true kalau sudah sukses masuk DB
     */
    public void archive(String localFilePath, boolean processed) {
        if (backupDirectory == null || backupDirectory.isBlank()) {
            log.warn("[BACKUP] m2e.sftp.backup-directory belum diisi, backup {} dilewati", localFilePath);
            return;
        }

        try {
            Path source = Path.of(localFilePath);
            if (!Files.exists(source)) {
                log.warn("[BACKUP] File {} tidak ada, tidak ada yang dibackup", localFilePath);
                return;
            }

            Path targetDir = Path.of(backupDirectory);
            Files.createDirectories(targetDir);

            Path target = targetDir.resolve(backupName(source.getFileName().toString(), processed));
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

            log.info("[BACKUP] {} -> {} ({} bytes)", source.getFileName(), target, Files.size(target));

        } catch (Exception e) {
            log.warn("[BACKUP] Gagal backup {}: {}", localFilePath, e.getMessage());
        }
    }

    private String backupName(String fileName, boolean processed) {
        String stamp = LocalDateTime.now(JAKARTA).format(STAMP) + (processed ? "" : "_FAILED");
        int dot = fileName.lastIndexOf('.');
        return (dot > 0)
                ? fileName.substring(0, dot) + "_" + stamp + fileName.substring(dot)
                : fileName + "_" + stamp;
    }
}
