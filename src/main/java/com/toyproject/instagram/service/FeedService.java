package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.UploadFeedReqDto;
import com.toyproject.instagram.entity.Feed;
import com.toyproject.instagram.entity.FeedImg;
import com.toyproject.instagram.repository.FeedMapper;
import com.toyproject.instagram.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${file.path}")
    private String filePath;

    private final FeedMapper feedMapper;

    // 받아온 파일 DB에 저장
    // Transactional은 예외가 터지면 롤백처리해준다.
    @Transactional( rollbackFor = Exception.class )
    public void upload(UploadFeedReqDto uploadFeedReqDto) {
        String content = uploadFeedReqDto.getContent();
        List<FeedImg> feedImgList = new ArrayList<>();
        // Security를 거쳐왔기 때문에 넣어뒀던 PrincipalUser을 꺼내 쓸 수 있다
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principalUser.getUsername();

        Feed feed = Feed.builder()
                .content(content)
                .username(username)
                .build();

        // feed가 만들어진 후 getFeedId 가능 가져온 feedId로 feed_img_tb에 insert
        feedMapper.saveFeed(feed);

        uploadFeedReqDto.getFiles().forEach(file -> {
            String originName = file.getOriginalFilename();
            String extensionName = originName.substring(originName.lastIndexOf("."));
            // 이미지 덮어쓰기 방지를 위해 UUID로 절대 겹칠 수 없게함
            String saveName = UUID.randomUUID().toString().replaceAll("-", "").concat(extensionName);

            Path uploadPath = Paths.get(filePath + "/feed/" + saveName);

            File f = new File(filePath + "/feed");
            // exists: 경로 오류 확인 -> mkdirs: 경로 없으면 만들어라
            if (f.exists()) {
                f.mkdirs();
            }

            try {
                Files.write(uploadPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            feedImgList.add(FeedImg.builder()
                            .feedId(feed.getFeedId())
                            .originFileName(originName)
                            .saveFileName(saveName)
                            .build());

        });

        feedMapper.saveFeedImgList(feedImgList);

    }
}
