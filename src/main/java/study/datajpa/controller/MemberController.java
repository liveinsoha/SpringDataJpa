package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dtos.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members")
    public Page<Member> findAll(@PageableDefault(size = 5, sort = "name") Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page; //엔티티를 노출하지 말고 Dto사용할 것
    }

    @GetMapping("/members-dto")
    public Page<MemberDto> findAllDto(Pageable pageable) {
        Page<Member> member = memberRepository.findMember(pageable);
        Page<MemberDto> memberDtoPage = member.map(MemberDto::new);
        return memberDtoPage;
    }

    @PostConstruct
    void initData() {
        for (int i = 1; i <= 100; i++) {
            memberRepository.save(new Member("member" + i, i));
        }
    }
}
