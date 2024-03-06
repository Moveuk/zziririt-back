package kr.zziririt.zziririt.api.post.controller

import jakarta.validation.Valid
import kr.zziririt.zziririt.api.dto.CommonResponse
import kr.zziririt.zziririt.api.post.dto.PostSearchCondition
import kr.zziririt.zziririt.api.post.dto.request.CreatePostRequest
import kr.zziririt.zziririt.api.post.dto.request.UpdatePostRequest
import kr.zziririt.zziririt.api.post.service.PostService
import kr.zziririt.zziririt.global.responseEntity
import kr.zziririt.zziririt.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/boards/{boardId}/posts")
class PostController(
    private val postService: PostService
) {
    @PostMapping
    fun createPost(
        @PathVariable boardId: Long,
        @Valid @RequestBody createPostRequest: CreatePostRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ) = responseEntity(HttpStatus.OK) { postService.createPost(boardId, createPostRequest, userPrincipal.memberId) }

    @GetMapping("/{postId}")
    fun getPost(
        @PathVariable postId: Long,
    ) = responseEntity(HttpStatus.OK) { postService.getPost(postId) }

    @GetMapping
    fun getPosts(
        @Valid condition: PostSearchCondition
    ) = responseEntity(HttpStatus.OK) { postService.getPosts(condition) }

    @PutMapping("/{postId}")
    fun updatePost(
        @PathVariable postId: Long,
        @Valid @RequestBody updatePostRequest: UpdatePostRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<CommonResponse<Nothing>> {
        postService.updatePost(postId, updatePostRequest, userPrincipal.memberId)
        return responseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<CommonResponse<Nothing>> {
        postService.deletePost(postId, userPrincipal.memberId)
        return responseEntity(HttpStatus.NO_CONTENT)
    }
}