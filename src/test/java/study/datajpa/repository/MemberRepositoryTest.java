package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Address;
import study.datajpa.entity.Member;
import study.datajpa.dtos.MemberDto;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    void findByAgePageable(){
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));

        Member cc = memberRepository.save(new Member("cc", 10, teamA, new Address("qqq", "www", "eee")));
        Member aa = memberRepository.save(new Member("dd", 10, teamB, new Address("aaa", "sss", "ddd")));
        Member bb = memberRepository.save(new Member("bb", 10, teamB, new Address("aaa", "sss", "ddd")));
        Member ee = memberRepository.save(new Member("aa", 10, teamB, new Address("aaa", "sss", "ddd")));
        Member dd = memberRepository.save(new Member("ee", 10, teamB, new Address("aaa", "sss", "ddd")));

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name"));

        Page<Member> page1 = memberRepository.findByAge(10, pageRequest);

        System.out.println("page.getTotalPages() = " + page1.getTotalPages());
        System.out.println("page.getNumber() = " + page1.getNumber());
        System.out.println("page.getTotalElements() = " + page1.getTotalElements());

        List<Member> content1 = page1.getContent();
        System.out.println("content = " + content1);
        assertThat(content1.size()).isEqualTo(2);
        Pageable pageable = page1.nextPageable();

        Page<Member> page2 = memberRepository.findByAge(10, pageable);
        List<Member> content2 = page2.getContent();
        System.out.println("content2 = " + content2);

        Page<Member> page3 = memberRepository.findByAge(10, page2.nextPageable());
        List<Member> content3 = page3.getContent();
        System.out.println("content3 = " + content3);


    }

    @Test
    void findMemberDtoListTest() {
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));

        Member lee = memberRepository.save(new Member("Lee", 10, teamA, new Address("qqq", "www", "eee")));
        Member kim = memberRepository.save(new Member("Kim", 20, teamB, new Address("aaa", "sss", "ddd")));
        List<MemberDto> memberDtoList = memberRepository.findMemberDtoList();
        assertThat(memberDtoList.size()).isEqualTo(2);
        System.out.println("memberDtoList = " + memberDtoList);
    }

    @Test
    void findAddressListTest() {
        Member lee = memberRepository.save(new Member("Lee", 10, null, new Address("qqq", "www", "eee")));
        Member kim = memberRepository.save(new Member("Kim", 20, null, new Address("aaa", "sss", "ddd")));
        List<Address> addressList = memberRepository.findAddressList();
        assertThat(addressList.size()).isEqualTo(2);
        System.out.println("addressList = " + addressList);
    }

    @Test
    void findUserNameListTest() {
        Member lee = memberRepository.save(new Member("Lee", 10));
        Member kim = memberRepository.save(new Member("Kim", 20));

        List<String> userNameList = memberRepository.findUserNameList();

        assertThat(userNameList.size()).isEqualTo(2);
        assertThat(userNameList.contains("Lee")).isTrue();
        assertThat(userNameList.contains("Kim")).isTrue();

    }

    @Test
    void findByNameAndAgeTest() {
        Member lee = memberRepository.save(new Member("Lee", 10));
        Member kim = memberRepository.save(new Member("Kim", 20));

        List<Member> result = memberRepository.findByNameAndAge("Lee", 10);
        assertThat(result.get(0)).isEqualTo(lee);
    }

    @Test
    void findByNameTest() {
        Member lee = memberRepository.save(new Member("Lee", 10));
        Member kim = memberRepository.save(new Member("Kim", 20));

        List<Member> result = memberRepository.findByName("Lee");

        assertThat(result.get(0)).isEqualTo(lee);
    }

    @Test
    void findByNameAndAgeGreaterThan() {
        memberRepository.save(new Member("Lee", 10));
        memberRepository.save(new Member("Kim", 20));

        List<Member> result = memberRepository.findByNameContainingAndAgeGreaterThan("im", 15);
        assertThat(result.get(0).getName()).isEqualTo("Kim");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void basicCRUDTest() {

        Member lee = memberRepository.save(new Member("Lee"));
        Member kim = memberRepository.save(new Member("Kim"));

        Member findMember1 = memberRepository.findById(lee.getId()).get();
        Member findMember2 = memberRepository.findById(kim.getId()).get();


        assertThat(findMember1).isEqualTo(lee);
        assertThat(findMember2).isEqualTo(kim);

        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        memberRepository.delete(lee);
        memberRepository.delete(kim);

        Long afterDeleteCount = memberRepository.count();
        assertThat(afterDeleteCount).isEqualTo(0);

    }
}