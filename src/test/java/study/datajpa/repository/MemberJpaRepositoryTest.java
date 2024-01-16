package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dtos.MemberDto;
import study.datajpa.entity.Address;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void findByAgePageTest() {
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));

        Member cc = memberRepository.save(new Member("cc", 10, teamA, new Address("qqq", "www", "eee")));
        Member aa = memberRepository.save(new Member("aa", 10, teamB, new Address("aaa", "sss", "ddd")));
        Member bb = memberRepository.save(new Member("bb", 10, teamB, new Address("aaa", "sss", "ddd")));
        Member ee = memberRepository.save(new Member("ee", 10, teamB, new Address("aaa", "sss", "ddd")));
        Member dd = memberRepository.save(new Member("dd", 10, teamB, new Address("aaa", "sss", "ddd")));

        List<MemberDto> result = memberRepository.findByAge(10, 2, 2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("cc");
        assertThat(result.get(1).getName()).isEqualTo("bb");
        System.out.println("result = " + result);

        Long totalCount = memberRepository.totalCount(10);
        assertThat(totalCount).isEqualTo(5);

    }

    @Test
    void findByNameTest() {
        Member lee = memberRepository.save(new Member("Lee", 10));
        Member kim = memberRepository.save(new Member("Kim", 20));

        Member findMember = memberRepository.findByName("Lee");
        assertThat(findMember).isEqualTo(lee);
    }

    @Test
    void findByNameAndAgeGreaterThan() {
        memberRepository.save(new Member("Lee", 10));
        memberRepository.save(new Member("Kim", 20));

        List<Member> result = memberRepository.findByNameAndAgeGreaterThan("im", 15);
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