package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.UploadFeedReqDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FeedService {

    @Value("${file.path}")
    private String filePath;

    // 받아온파일 서버에 저장
    public void upload(UploadFeedReqDto uploadFeedReqDto) {
        uploadFeedReqDto.getFiles().forEach(file -> {
            String originName = file.getOriginalFilename();
            String extensionName = originName.substring(originName.lastIndexOf("."));
            // 이미지 덮어쓰기 방지를 위해 UUID로 절대 겹칠 수 없게함
            String newName = UUID.randomUUID().toString().replaceAll("-", "").concat(extensionName);

            Path uploadPath = Paths.get(filePath + "/feed/" + newName);

            File f = new File(filePath + "/feed");
            // exists: 경로 오류 확인 -> mkdirs: 경로 없으면 만들어라
            if (f.exists()) {
                f.mkdirs();
            }

            // file의 Bytes를 써둬라
            try {
                Files.write(uploadPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
