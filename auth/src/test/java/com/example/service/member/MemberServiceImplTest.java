package com.example.service.member;

import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.member.Role;
import com.example.web.dto.MemberJoinDTO;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Sql("/data.sql")
public class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @Before
    public void initData() {
        Optional<Member> byId = memberRepository.findById(1L);
        member = byId.get();
    }

    @Test
    public void join() {
        //given
        MemberJoinDTO memberJoinDTO = new MemberJoinDTO();
        memberJoinDTO.setEmail("jun3395@gmail.com");
        memberJoinDTO.setName("김종준");
        memberJoinDTO.setPassword("1234");

        //when
        memberService.join(memberJoinDTO);
        Member repositoryMember = memberRepository.findById(member.getId())
                .get();

        //then
        Assertions.assertThat(repositoryMember.getName())
                .isEqualTo(member.getName());
    }

    @Test
    public void adjustRoleAdmin() throws Exception {
        //given

        //when
        memberService.adjustRole(member, Role.ADMIN);
        Member repositoryMember = memberRepository.findById(member.getId())
                .get();

        //then

        Assertions.assertThat(repositoryMember.getRole())
                .isEqualTo(Role.ADMIN);
    }

    @Test
    public void adjustRoleMember() throws Exception {
        //given
        Member getMember = memberRepository.findById(2L)
                .get();

        //when
        memberService.adjustRole(getMember, Role.MEMBER);
        Member repositoryMember = memberRepository.findById(getMember.getId())
                .get();

        //then
        Assertions.assertThat(repositoryMember.getRole())
                .isEqualTo(Role.MEMBER);
    }
    @Test
    public void alreadyAdmin() throws Exception {
        //given

        //when
        Member adminMember = memberRepository.findById(3L)
                .get();

        //then
        Assertions.assertThatIllegalStateException()
                .isThrownBy(() -> memberService.adjustRole(adminMember, Role.ADMIN));
    }

}