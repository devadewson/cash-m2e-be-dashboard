package com.m2e.dashboard.service.trade.Trademart;


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

    /**
     * @param remoteFileName
     * @param localPath
     */
    public void download(String remoteFileName, String localPath) {
        if (remoteFileName == null || remoteFileName.isBlank()) {
            throw new IllegalArgumentException("Nama file remote wajib diisi");
        }
        if (localPath == null || localPath.isBlank()) {
            throw new IllegalArgumentException("Path lokal wajib diisi");
        }

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

            log.info("[SFTP] Downloading {} -> {}", remotePath, localPath);

            try (SFTPClient sftp = ssh.newSFTPClient();
                 RemoteFile rf = sftp.open(remotePath);
                 FileOutputStream fos = new FileOutputStream(localFile)) {

                byte[] buf = new byte[65536];
                long offset = 0;
                long size = rf.length();

                while (offset < size) {
                    int n = rf.read(offset, buf, 0, buf.length);
                    if (n == -1) {
                        break;
                    }
                    fos.write(buf, 0, n);
                    offset += n;
                }
            }

            if (localFile.length() == 0) {
                log.warn("[SFTP] File {} terunduh tapi ukurannya 0 byte", remoteFileName);
            }

            log.info("[SFTP] Downloaded: {} ({} bytes)", localPath, localFile.length());

        } catch (Exception e) {
            log.error("[SFTP] Download gagal untuk {}: {}", remotePath, e.getMessage());
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
