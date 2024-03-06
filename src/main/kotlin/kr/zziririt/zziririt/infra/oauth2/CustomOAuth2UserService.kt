package kr.zziririt.zziririt.infra.oauth2

import jakarta.transaction.Transactional
import kr.zziririt.zziririt.domain.member.model.MemberRole
import kr.zziririt.zziririt.domain.member.model.MemberStatus
import kr.zziririt.zziririt.domain.member.model.OAuth2Provider
import kr.zziririt.zziririt.domain.member.model.SocialMemberEntity
import kr.zziririt.zziririt.domain.member.repository.SocialMemberRepository
import kr.zziririt.zziririt.infra.security.jwt.JwtDto
import kr.zziririt.zziririt.infra.security.jwt.JwtProvider
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val socialMemberRepository: SocialMemberRepository,
    private val jwtProvider: JwtProvider
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {

        val loadUser = super.loadUser(userRequest)

        return loadUser
    }

    fun getAttribute(attribute: String, oAuth2User: OAuth2User): String? {
        return (oAuth2User.attributes.get("response") as LinkedHashMap<String, String>).get(attribute)!!
    }

    @Transactional
    fun login(oAuth2User: OAuth2User): JwtDto {
        val email = getAttribute("email", oAuth2User)!!
        var user = socialMemberRepository.findByEmail(email)
        if (user == null) {
            user = socialMemberRepository.save(
                SocialMemberEntity(
                    email = email,
                    nickname = getAttribute("nickname", oAuth2User)!!,
                    providerId = getAttribute("id", oAuth2User)!!,
                    provider = OAuth2Provider.NAVER,
                    memberRole = MemberRole.VIEWER,
                    memberStatus = MemberStatus.NORMAL,
                    bannedStartDate = null,
                    bannedEndDate = null,
                )
            )
        }
        return JwtDto(
            jwtProvider.generateAccessToken(
                id = user.id.toString(),
                subject = user.providerId,
                email = user.email,
                role = user.memberRole.name
            )
        )
    }
}