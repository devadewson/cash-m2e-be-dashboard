package com.m2e.dashboard.service.trade.Refactor.TradeMart;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class SftpDownloader {

    @Value("${m2e.sftp.host}")
    private String host;

    @Value("${m2e.sftp.port}")
    private int port;

    @Value("${m2e.sftp.username}")
    private String username;

    @Value("${m2e.sftp.password}")
    private String password;

    @Value("${m2e.sftp.remote-directory}")
    private String remoteDir;

    @Value("${m2e.sftp.file-name}")
    private String fileName;

    public void download(String localPath) {
        SSHClient ssh = new SSHClient();
        try {
            // Koneksi
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(host, port);
            ssh.authPassword(username, password);

            // Buat folder lokal dulu
            File localFile = new File(localPath);
            localFile.getParentFile().mkdirs();

            // Download
            try (SFTPClient sftp = ssh.newSFTPClient();
                 RemoteFile rf = sftp.open(remoteDir + "/" + fileName);
                 FileOutputStream fos = new FileOutputStream(localFile)) {

                byte[] buf = new byte[65536];
                long offset = 0;
                long size = rf.length();

                while (offset < size) {
                    int n = rf.read(offset, buf, 0, buf.length);
                    if (n == -1) break;
                    fos.write(buf, 0, n);
                    offset += n;
                }
            }

            log.info("Downloaded: {} ({} MB)", localPath, localFile.length() / 1048576);

        } catch (Exception e) {
            log.error("Download failed: {}", e.getMessage());
            throw new RuntimeException("SFTP download failed", e);
        } finally {
            try {
                if (ssh.isConnected()) {
                    ssh.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
    }
}