package com.example.cafelog.controller;

import com.example.cafelog.entity.Post;
import com.example.cafelog.repository.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Controller
public class PostViewController {

    private final PostRepository postRepository;

    public PostViewController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/posts/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "createPost";
    }

    @PostMapping("/posts/form")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("image") MultipartFile image,
			     Model model) throws IOException {
		     	   
        String imageUrl = null;

        if (!image.isEmpty()) {
            S3Client s3 = S3Client.builder()
                    .region(Region.AP_NORTHEAST_1)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (InputStream is = image.getInputStream()) {
                Thumbnails.of(is)
                        .size(500, 500) // ← 最大サイズを指定（横500px・縦自動調整）
                        .outputFormat("jpg") // ← JPEGに変換
                        .outputQuality(0.8)   // ← 圧縮率
                        .toOutputStream(os);
    }
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket("cafelog-s3-images")
                            .key(fileName)
                            .contentType("image/jpeg")
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(image.getBytes())
            );

            imageUrl = "https://cafelog-s3-images.s3.ap-northeast-1.amazonaws.com/" + fileName;
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);
        model.addAttribute("message", "投稿が完了しました！");
        return "success";
    }
    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, Model model) {
        postRepository.deleteById(id);
        model.addAttribute("message", "投稿を削除しました。");
        return "deleteSuccess";
    }
}
