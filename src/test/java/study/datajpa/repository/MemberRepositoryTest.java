package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
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
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;


    @Nested
    @SpringBootTest
    @Transactional
    //@Rollback(value = false)
    static class inner {

        @Autowired
        EntityManager em;
        @Autowired
        MemberRepository memberRepository;
        @Autowired
        TeamRepository teamRepository;

        @BeforeEach
        void beforeEach() {
            Team teamA = teamRepository.save(new Team("teamA"));
            Team teamB = teamRepository.save(new Team("teamB"));
            Team teamC = teamRepository.save(new Team("teamC"));
            Team teamD = teamRepository.save(new Team("teamD"));
            Team teamE = teamRepository.save(new Team("teamE"));

            Member cc = memberRepository.save(new Member("cc", 10, teamA, new Address("qqq", "www", "eee")));
            Member aa = memberRepository.save(new Member("aa", 15, teamB, new Address("aaa", "sss", "ddd")));
            Member bb = memberRepository.save(new Member("bb", 20, teamC, new Address("aaa", "sss", "ddd")));
            Member ee = memberRepository.save(new Member("ee", 21, teamD, new Address("aaa", "sss", "ddd")));
            Member dd = memberRepository.save(new Member("dd", 22, teamE, new Address("aaa", "sss", "ddd")));
        }

        @Test
        @Rollback(value = false)
        void BaseEntityTest() throws InterruptedException {
            List<Member> aa = memberRepository.findByName("aa");
            Member member = aa.get(0);

            Thread.sleep(1000);
            member.setName("aaa");
            em.flush();
            em.clear();

            Member findMember = memberRepository.findById(member.getId()).get();

            System.out.println("findMember = " + findMember);
        }

        @Test
        void findByCityWithCustomRepo() {
            List<Member> aaa = memberRepository.findByCity("aaa");
            System.out.println("aaa = " + aaa);
        }

        @Test
        void findByUserNameWithEntityGraph() {
            List<Member> aa = memberRepository.findByName("aa");
            assertThat(Hibernate.isInitialized(aa.get(0).getTeam())).isTrue();
        }

        @Test
        void findMembersEntityGraph() { //JPOL + EntityGraph
            List<Member> all = memberRepository.findMembersEntityGraph();
            assertThat(Hibernate.isInitialized(all.get(0).getTeam())).isTrue();
            for (Member member : all) {
                System.out.println("member.getTeam() = " + member.getTeam());
            }
        }

        @Test
        void findAllEntityGraph() {
            List<Member> all = memberRepository.findAll(); //@EntityGraph 애노테이션으로 페치조인 하고 오버라이딩 함
            assertThat(Hibernate.isInitialized(all.get(0).getTeam())).isTrue();
            for (Member member : all) {
                System.out.println("member.getTeam() = " + member.getTeam());
            }
        }

        @Test
        void findMembersFetchJoin() {
            List<Member> all = memberRepository.findMembersFetchJoin();

            assertThat(Hibernate.isInitialized(all.get(0).getTeam())).isTrue();

            for (Member member : all) {
                System.out.println("member.getTeam() = " + member.getTeam());
            }
        }

        @Test
        void findAllLazyLoadingTest() {

            em.flush();
            em.clear();
            List<Member> all = memberRepository.findAll();

            assertThat(Hibernate.isInitialized(all.get(0).getTeam())).isFalse();

            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(all.get(0).getTeam())).isFalse();


            assertThat(all.size()).isEqualTo(5);
            System.out.println("==================");

            for (Member member : all) {
                System.out.println("member.getTeam() = " + member.getTeam().getName());
                System.out.println("==================");
            }

            //참고: 다음과 같이 지연 로딩 여부를 확인할 수 있다.

            //Hibernate로 확인
            assertThat(Hibernate.isInitialized(all.get(0).getTeam())).isTrue();

            //JPA 표준 방법으로 확인
            assertThat(util.isLoaded(all.get(0).getTeam())).isTrue();
        }

        @Test
        void bulkAgePlusTest() {
            int resultCount = memberRepository.bulkAgePlus(20);
            assertThat(resultCount).isEqualTo(2);

            List<Member> result = memberRepository.findByName("dd");
            System.out.println("dd1 = " + result.get(0));
        }

        @Test
        void findMemberPageableTest() {

            PageRequest pageRequest = PageRequest.of(2, 2, Sort.by(Sort.Direction.DESC, "name"));

            Page<Member> page = memberRepository.findMember(pageRequest);
            List<Member> content = page.getContent();
            System.out.println("content = " + content);
        }

        @Test
        void findByAgePageable() {

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