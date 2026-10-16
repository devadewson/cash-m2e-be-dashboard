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
    private String defaultFileName;

    /**
     * Unduh file default (trade) ke path lokal.
     */
    public void download(String localPath) {
        download(defaultFileName, localPath);
    }

    /**
     * Unduh file tertentu dari remote directory yang sama.
     *
     * @param remoteFileName
     * @param localPath
     */
    public void download(String remoteFileName, String localPath) {
        SSHClient ssh = new SSHClient();
        String remotePath = remoteDir + "/" + remoteFileName;

        try {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(host, port);
            ssh.authPassword(username, password);

            File localFile = new File(localPath);
            File parent = localFile.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            log.info("Downloading {} -> {}", remotePath, localPath);

            try (SFTPClient sftp = ssh.newSFTPClient();
                 RemoteFile rf = sftp.open(remotePath);
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

            if (localFile.length() == 0) {
                log.warn("File {} terunduh tapi ukurannya 0 byte", remoteFileName);
            }

            log.info("Downloaded: {} ({} bytes)", localPath, localFile.length());

        } catch (Exception e) {
            log.error("Download failed for {}: {}", remotePath, e.getMessage());
            throw new RuntimeException("SFTP download failed: " + remotePath, e);
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