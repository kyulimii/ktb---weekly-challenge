package org.example.community.domain.post.application;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.community.domain.post.Post;
import org.example.community.domain.post.api.dto.request.PostRequestDto;
import org.example.community.domain.post.api.dto.response.PostDetailResponse;
import org.example.community.domain.post.api.dto.response.PostListResponse;
import org.example.community.domain.post.api.dto.response.PostPageResponse;
import org.example.community.domain.post.comment.repository.CommentRepository;
import org.example.community.domain.post.repository.LikeRepository;
import org.example.community.domain.post.repository.PostRepository;
import org.example.community.global.CursorInfo;
import org.example.community.global.ImageValidator;
import org.example.community.global.exception.CustomException;
import org.example.community.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ImageValidator imageValidator;

    // 게시글 작성
    public void createPost(Long userId, PostRequestDto postRequestDto,
                           MultipartFile postImage) {
        byte[] image = (!postImage.isEmpty())
                ? imageValidator.extractBytes(postImage)
                : null;

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .postImage(image)
                .userId(userId)
                .build();
        postRepository.save(post);
    }

    // 최초 요청: GET /posts?sort=latest&limit=10
    // 이후 요청: GET /posts?sort=latest&cursor=2026-05-31T12:00:00Z|1&limit=10
    // 게시글 목록 조회
    public PostPageResponse getPosts(String sort, String cursor, int limit) {

        // cursor 파싱 (최초 요청이면 null)
        CursorInfo cursorInfo = CursorInfo.from(cursor, sort);

        // 정렬 기준
        Comparator<Post> comparator = switch (sort) {
            case "latest" -> Comparator.comparing(Post::getCreatedAt).reversed()
                    .thenComparing(Comparator.comparing(Post::getId).reversed());
            case "oldest" -> Comparator.comparing(Post::getCreatedAt)
                    .thenComparing(Post::getId);
            case "popular" -> Comparator.comparing(Post::getLikeCount).reversed()
                    .thenComparing(Comparator.comparing(Post::getId).reversed());
            default -> throw new CustomException(ErrorCode.INVALID_SORT);
        };

        // 전체 조회 → 정렬 → cursor 이후 필터 → limit+1개 조회
        List<Post> posts = postRepository.findAll()
                .stream()
                .sorted(comparator)
                .filter(post -> isAfterCursor(post, cursorInfo, sort))
                .limit(limit + 1) // hasNext 여부 확인 위해 + 1
                .toList();

        // 다음 페이지 존재 여부 확인
        boolean hasNext = posts.size() > limit;
        List<Post> result = hasNext ? posts.subList(0, limit) : posts;

        // 다음 cursor 생성
        String nextCursor = hasNext
                ? sort.equals("popular")
                ? CursorInfo.encode(result.get(result.size() - 1).getLikeCount(), result.get(result.size() - 1).getId())
                : CursorInfo.encode(result.get(result.size() - 1).getCreatedAt(), result.get(result.size() - 1).getId())
                : null;

        return PostPageResponse.builder()
                .posts(result.stream().map(PostListResponse::from).toList())
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    // 게시글 상세 조회
    public PostDetailResponse getPost(Long postId) {
        Post post = findPostById(postId);

        post.increaseViewCount();
        postRepository.save(post);

        return PostDetailResponse.from(post);
    }

    // 게시글 수정
    public void updatePost(Long userId, Long postId, PostRequestDto postRequestDto,
                           MultipartFile postImage) {
        Post post = findPostById(postId);

        if (!Objects.equals(post.getUserId(), userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        byte[] image = (postImage != null && !postImage.isEmpty())
                ? imageValidator.extractBytes(postImage)
                : post.getPostImage();

        post.update(postRequestDto.getTitle(), postRequestDto.getContent(), image);
        postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long userId, Long postId) {
        Post post = findPostById(postId);

        if (!Objects.equals(post.getUserId(), userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        commentRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);

        postRepository.deleteById(postId);
    }

    public void createLike(Long userId, Long postId) {
        Post post = findPostById(postId);
        if (likeRepository.exists(userId, postId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }
        likeRepository.save(userId, postId);
        post.increaseLikeCount();
        postRepository.save(post);
    }

    public void deleteLike(Long userId, Long postId) {
        Post post = findPostById(postId);
        if (!likeRepository.exists(userId, postId)) {
            throw new CustomException(ErrorCode.NOT_LIKED);
        }
        likeRepository.delete(userId, postId);
        post.decreaseLikeCount();
        postRepository.save(post);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
    }

    // 커서 이후 데이터 있는지 확인
    private boolean isAfterCursor(Post post, CursorInfo cursorInfo, String sort) {
        if (cursorInfo == null) {
            return true;
        }
        return switch (sort) {
            case "latest" -> post.getCreatedAt().isBefore(cursorInfo.getCreatedAt()) ||
                    (post.getCreatedAt().isEqual(cursorInfo.getCreatedAt()) &&
                            post.getId() < cursorInfo.getId());
            case "oldest" -> post.getCreatedAt().isAfter(cursorInfo.getCreatedAt()) ||
                    (post.getCreatedAt().isEqual(cursorInfo.getCreatedAt()) &&
                            post.getId() > cursorInfo.getId());
            case "popular" -> post.getLikeCount() < cursorInfo.getLikeCount() ||
                    (post.getLikeCount() == cursorInfo.getLikeCount() &&
                            post.getId() < cursorInfo.getId());
            default -> throw new CustomException(ErrorCode.INVALID_SORT);
        };
    }
}