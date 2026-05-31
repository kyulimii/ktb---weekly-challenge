package org.example.community.domain.post.comment.application;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.community.domain.post.Post;
import org.example.community.global.CursorInfo;
import org.example.community.domain.post.comment.Comment;
import org.example.community.domain.post.comment.api.dto.CommentDetailResponse;
import org.example.community.domain.post.comment.api.dto.CommentPageResponse;
import org.example.community.domain.post.comment.api.dto.CommentRequestDto;
import org.example.community.domain.post.comment.repository.CommentRepository;
import org.example.community.domain.post.repository.PostRepository;
import org.example.community.domain.user.User;
import org.example.community.domain.user.repository.UserRepository;
import org.example.community.global.exception.CustomException;
import org.example.community.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    public void createComment(Long userId, Long postId, CommentRequestDto commentRequestDto) {
        Comment comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .userId(userId)
                .postId(postId)
                .build();
        commentRepository.save(comment);

        Post post = findPostById(postId);
        post.increaseCommentCount();

        postRepository.save(post);
    }

    // 댓글 조회
    public CommentPageResponse getComments(Long postId, String sort, String cursor, int limit) {
        CursorInfo cursorInfo = CursorInfo.from(cursor, sort);

        Comparator<Comment> comparator = switch (sort) {
            case "latest" -> Comparator.comparing(Comment::getCreatedAt).reversed()
                    .thenComparing(Comparator.comparing(Comment::getId).reversed());
            case "oldest" -> Comparator.comparing(Comment::getCreatedAt)
                    .thenComparing(Comparator.comparing(Comment::getId));
            default -> throw new CustomException(ErrorCode.INVALID_SORT);
        };

        List<Comment> comments = commentRepository.findByPostId(postId)
                .stream()
                .sorted(comparator)
                .filter(comment -> isAfterCursor(comment, cursorInfo, sort))
                .limit(limit + 1)
                .toList();

        boolean hasNext = comments.size() > limit;
        List<Comment> result = hasNext ? comments.subList(0, limit) : comments;

        String nextCursor = hasNext
                ? CursorInfo.encode(result.get(result.size() - 1).getCreatedAt(), result.get(result.size() - 1).getId())
                : null;

        return CommentPageResponse.builder()
                .comments(result.stream()
                        .map(comment -> {
                            // userId로 User 조회 후 nickname 전달 - DB 연결 시 성능 문제 발생 가능
                            String nickname = userRepository.findById(comment.getUserId())
                                    .map(User::getNickname)
                                    .orElse("탈퇴한 사용자");
                            return CommentDetailResponse.from(comment, nickname);
                        })
                        .toList())
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    // 댓글 수정
    public void updateComment(Long userId, Long postId, Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = findAndValidate(userId, postId, commentId);

        comment.update(commentRequestDto.getContent());
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long userId, Long postId, Long commentId) {
        findAndValidate(userId, postId, commentId);
        Post post = findPostById(postId);
        commentRepository.deleteById(commentId);
        post.decreaseCommentCount();
        postRepository.save(post);
    }

    // 댓글 조회 + 검증
    private Comment findAndValidate(Long userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        if (!Objects.equals(comment.getPostId(), postId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_COMMENT);
        }

        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return comment;
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
    }

    private boolean isAfterCursor(Comment comment, CursorInfo cursorInfo, String sort) {
        if (cursorInfo == null)
            return true;

        return switch (sort) {
            case "latest" ->
            comment.getCreatedAt().isBefore(cursorInfo.getCreatedAt()) ||
                    (comment.getCreatedAt().isEqual(cursorInfo.getCreatedAt()) &&
                            comment.getId() < cursorInfo.getId());
            case "oldest" ->
                comment.getCreatedAt().isAfter(cursorInfo.getCreatedAt()) ||
                        (comment.getCreatedAt().isEqual(cursorInfo.getCreatedAt()) &&
                                comment.getId() > cursorInfo.getId());
            default -> throw new CustomException(ErrorCode.INVALID_SORT);
        };
    }
}