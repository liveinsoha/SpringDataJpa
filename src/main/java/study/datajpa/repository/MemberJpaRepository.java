package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dtos.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    @Autowired
    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public List<Member> findByNameAndAgeGreaterThan(String name, int age) {
        return em.createQuery("select m from Member m where m.name like :namePattern and m.age > :age", Member.class)
                .setParameter("namePattern", "%" + name + "%")
                .setParameter("age", age)
                .getResultList();
    }

    public Member findByName(String name) {
        return em.createNamedQuery("Member.findByName", Member.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public List<MemberDto> findByAge(int age, int offset, int limit) {
        return em.createQuery("select new study.datajpa.dtos.MemberDto(m.id, m.name, m.age, t.name)" +
                        " from Member m" +
                        " join m.team t" +
                        " where m.age = :age" +
                        " order by m.name desc", MemberDto.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public Long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }


    public int bulkAgePlus(int age) {
        int resultCount = em.createQuery("update Member m set m.age = m.age + 1 where m.age > :age")
                .setParameter("age", age)
                .executeUpdate();
        em.clear(); //벌크연산은 영속성 컨텍스트를 거치지 않고 수행되기에 영속성 컨텍스트에서 바로 조회할 경우 값이 예상과 다를 수 있다
        //따라서 벌크 연산을 한 후에 영속성 컨텍스트를 초기화한다.

        return resultCount;
    }
}
