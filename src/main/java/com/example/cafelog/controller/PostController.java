package com.example.cafelog.controller;

import com.example.cafelog.entity.Post;
import com.example.cafelog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    @ResponseBody
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @PostMapping
    @ResponseBody
    public Post createPost(@RequestBody Post post) {
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket("cafelog-s3-images")
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()) // ← フルパス指定
        );

        return "https://cafelog-s3-images.s3.ap-northeast-1.amazonaws.com/" + fileName;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Post getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "投稿が見つかりませんでした"));
}
    @DeleteMapping("/{id}")
    @ResponseBody
    public String deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return "投稿ID " + id + " を削除しました";
    }
}

